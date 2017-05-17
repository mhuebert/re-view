(ns app.docs
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [app.markdown :refer [md]]
            [goog.net.XhrIo :as xhr]
            [goog.dom :as gdom]
            [clojure.string :as string]
            [goog.events :as events]
            [re-view-routing.core :as routing]
            [re-db.d :as d]
            [re-view-material.icons :as icons]))

(def cache {})

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
  (str "http://re-view.github.io/re-view" (md-path path)))

(defn edit-url [path]
  (str "https://github.com/re-view/re-view/edit/master/docs" (md-path path)))

(defn GET [url cb]
  (if-let [value (get cache url)]
    (cb {:value value})
    (xhr/send url (fn [e]
                    (let [value (.getResponseText (.-target e))]
                      (set! cache (assoc cache url value))
                      (cb {:value value}))))))

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

(def index nil)

(def get-index (fn [cb]
                 (GET "http://re-view.github.io/re-view/json/index.json"
                      (fn [{:keys [value]}]
                        (set! index
                              (->> (js->clj (.parse js/JSON value) :keywordize-keys true)
                                   (reduce (fn [m {:keys [title name path] :as doc}]
                                             (assoc-in m (path->keys path) doc)) {})))
                        (cb)))))


(defn breadcrumb [url]
  (when-let [all-segments (seq (routing/segments url))]
    [:.f6.mb3 (let [num-segments (count all-segments)]
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

(defn dir-index [path-keys]
  (let [children (get-in index (drop-last path-keys))]
    [:.pa3
     [:h3.mv3 "Index"]
     (->> (vals children)
          (keep :*file)
          (sort-by :title)
          (map (fn [{:keys [title name path]}]
                 [:p [:a {:href (str "/docs/" (string/replace path #"\.md$" ""))} title]])))]))

(defview markdown-page
  {:initial-state           (fn [_ url] {:value (get cache url)
                                         :index index})
   :update                  (fn [{:keys [view/state]} url]
                              (swap! state assoc :loading true)
                              (let [path-keys (path->keys url)]
                                (if (get-in index path-keys)
                                  (GET (download-url url) #(do (reset! state %)))

                                  (swap! state merge {:value   (dir-index path-keys)
                                                      :loading false}))))
   :life/did-mount          (fn [{:keys [view/state] :as this} url]
                              (when-not index (get-index #(.update this url)))
                              (.update this url))
   :life/will-receive-props (fn [{:keys [view/children view/prev-children] :as this}]
                              (when (not= children prev-children)
                                (.update this (first children))))}
  [{:keys [view/state]} url]

  (let [{:keys [value error]} @state]
    [:.markdown-copy.pa4.flex-grow
     (breadcrumb url)
     [:.ph4.pv2.lh-copy.mw7.elevated-card.relative
      (ui/Button {:href    (edit-url url)
                  :target  "_blank"
                  :icon    icons/ModeEdit
                  :label   "Edit on GitHub"
                  :dense   true
                  :compact true
                  :class   "absolute top-0 right-0 ma2"})
      (cond error [:div "Error..." error]
            value (if (string? value)
                    [:div (md value)]
                    value)
            :else [:div "Loading..."])]]))

(defview home
  []
  [:div
   (markdown-page "/")])

(defview page
  [this segments]
  (markdown-page (string/join "/" segments)))