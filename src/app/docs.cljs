(ns app.docs
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [app.markdown :refer [md]]
            [goog.dom :as gdom]
            [goog.dom.classes :as classes]
            [clojure.string :as string]
            [goog.events :as events]
            [re-view-routing.core :as routing]
            [re-db.d :as d]
            [re-view-material.icons :as icons]
            [app.util :as util]))



(defview toolbar []
  (ui/ToolbarSection
    {:class "flex items-center"}
    (ui/ToolbarTitle {:class "no-underline"
                      :href  "/"} "Docs")
    [:.flex-auto]))

(defn md-path [path]
  (str
    (when-not (string/starts-with? path "/") "/")
    (cond-> path
            (string/ends-with? path "/") (str "index")
            (not (string/ends-with? path ".md")) (str ".md"))))

(defn download-url [path]
  (str "https://re-view.github.io/re-view" (md-path path)))

(defn edit-url [path]
  (str "https://github.com/re-view/re-view/edit/master/docs" (md-path path)))

(defn path->keys [url]
  (if (= url "/")
    (list "index" :*file)
    (-> (string/replace url #"\.md$" "")
        (string/split #"/")
        (concat '(:*file)))))

(defn hyphens->readable [s]
  (if (= s "re-view")
    "Re-View"
    (string/replace s #"(?:^|-)([a-z])" (fn [[_ s]] (str " " (string/upper-case s))))))

(defn parse-header [^js/Element anchor-element]
  (let [heading (.-parentNode anchor-element)]
    {:label (gdom/getTextContent heading)
     :level (js/parseInt (second (re-find #"H(\d)" (.-tagName heading))))
     :id    (.-id anchor-element)}))



(def index nil)

(def get-index (fn [cb]
                 (util/GET :json "https://re-view.github.io/re-view/json/index.json"
                           (fn [{:keys [value]}]
                             (set! index
                                   (->> (js->clj value :keywordize-keys true)
                                        (reduce (fn [m {:keys [title name path] :as doc}]
                                                  (assoc-in m (path->keys path) doc)) {})))
                             (cb)))))


(defn breadcrumb [url]
  (let [all-segments (routing/segments url)]
    [:.f6.mh2 (let [num-segments (count all-segments)]
                (->> all-segments
                     (map-indexed
                       (fn [i label]
                         [(hyphens->readable label)
                          (as-> (take (inc i) all-segments) segments
                                (string/join "/" segments)
                                (str "/docs/" segments)
                                (if-not (= i (dec num-segments)) (str segments "/") segments))]))
                     (cons ["Docs" "/docs/"])
                     (map (fn [[label href]] [:a.no-underline {:href href
                                                               :key  href} label]))
                     (interpose " / ")))]))

(defn docs-at-path [path-keys]
  (let [children (get-in index (drop-last path-keys))]
    [:.pa3
     [:h3.mv3 "Index"]
     (->> (vals children)
          (keep :*file)
          (sort-by :title)
          (map (fn [{:keys [title name path]}]
                 [:p [:a {:href (str "/docs/" (string/replace path #"\.md$" ""))} title]])))]))

(defn html-toc [el]
  (->> (gdom/findNodes el #(classes/has % "heading-anchor"))
       (clj->js)
       (keep (comp (fn [{:keys [label level id]}]
                     (when (> level 1)
                       [:a.db.no-underline {:href  (str "#" id)
                                            :style {:padding-left (str (- level 2) "rem")}} label])) parse-header))))

(defview markdown-page
  {:initial-state           (fn [_ url] {:value (get util/cache url)
                                         :index index})
   :update-toc              (fn [{:keys [view/state] :as this}]
                              (swap! state assoc :toc (html-toc (v/dom-node this))))
   :update                  (fn [{:keys [view/state]} url]
                              (swap! state assoc :loading true :toc nil)
                              (let [path-keys (path->keys url)]
                                (if (get-in index path-keys)
                                  (util/GET :text (download-url url) #(do (reset! state %)))

                                  (swap! state merge {:value   (docs-at-path path-keys)
                                                      :loading false}))))
   :life/did-mount          (fn [{:keys [view/state] :as this} url]
                              (when-not index (get-index #(.update this url)))
                              (.update this url))
   :life/will-receive-props (fn [{:keys [view/children view/prev-children] :as this}]
                              (when (not= children prev-children)
                                (.update this (first children))))
   :life/did-update         (fn [{:keys [view/state view/prev-state] :as this}]
                              (.updateToc this))}
  [{:keys [view/state]} url]
  (let [{:keys [value error toc]} @state]
    [:.markdown-copy.pa4.flex-grow
     [:.flex.items-center.pv2.mw7
      (breadcrumb url)
      [:.flex-auto]
      (ui/Button {:href    (edit-url url)
                  :target  "_blank"
                  :icon    icons/ModeEdit
                  :label   "Edit"
                  :dense   true
                  :compact true})]
     [:.ph4.pv2.lh-copy.mw7.elevated-card.relative

      (when-not (empty? toc)
        [:.f7.fr-l.pa3.bg-darken-1.mt3.relative.z-1
         [:.b.f6 "On this page:"]
         toc])
      (cond error [:div "Error..." error]
            (not index) [:div "Loading..."]
            value (if (string? value)
                    (md value)
                    value)
            :else [:div "Loading..."])]]))

(defview home
  []
  [:div
   (markdown-page "/")])

(defview page
  [this segments]
  (markdown-page (string/join "/" segments)))