(ns app.views
  (:require [re-view.core :as v :refer [defview]]
            [goog.dom :as gdom]
            [app.views.markdown :as markdown]
            [goog.dom.classes :as classes]
            [app.util :as util]
            [clojure.string :as string]
            [re-db.d :as d]
            [re-view-material.icons :as icons]
            [re-view-material.core :as ui]))

(defview edit-button [this]
  (ui/Button (-> (v/pass-props this)
                 (merge {:target  "_blank"
                         :icon    icons/ModeEdit
                         :label   "Edit"
                         :dense   true
                         :compact true}))))

(defn breadcrumb []
  (let [all-segments (d/get :router/location :segments)]
    (when (second all-segments)
      (->> all-segments
           (map-indexed
             (fn [i label]
               [label
                (as-> (take (inc i) all-segments) segments
                      (string/join "/" segments)
                      (if-not (= i (dec (count all-segments))) (str segments "/") segments)
                      (str "/" segments))]))
           (map (fn [[label href]] (if href [:a.no-underline {:href href
                                                              :key  href} label]
                                            label)))
           (interpose " / ")))))

(defn page [& content]
  (into [:.flex-grow.markdown-copy.mw-page.center.mv3.lh-copy] content))

(defview click-copy
  {:spec/props {:outer-class :String}}
  [{:keys [style outer-class] :as this} s]
  [:span.relative.di
   {:class outer-class}
   [:.code.o-0.border-box.dib (v/pass-props this) s]
   [:input.code.bn.z-1.absolute.top-0.left-0.di.w-100
    (merge (v/pass-props this)
           {:value     s
            :read-only true
            :style     (merge style {:outline    0
                                     :box-sizing "border-box"})
            :on-click  #(.select (-> (v/dom-node this)
                                     (gdom/findNode (fn [el] (= "INPUT" (.-tagName el))))))})]])

(defview WithClojarsVersion
  {:life/will-mount (fn [{:keys [view/state]} group _]
                      (util/GET :json (str "https://clojars.org/" group "/latest-version.json")
                                (fn [{:keys [value]}]
                                  (when value (reset! state (aget value "version"))))))}
  [{:keys [view/state]} repo f]
  (or (some-> @state (f))
      [:span]))

(defn clickable-version [group & [artifact]]
  (WithClojarsVersion (str group "/" (or artifact group))
                      (fn [version]
                        (click-copy {:outer-class "mr2"
                                     :class       "pa1 bg-darken f7"}
                                    (str "[" group (some->> artifact (str "/")) (str " \"" (or version "...") "\"]"))))))



(defn parse-header [^js/Element anchor-element]
  (let [heading (.-parentNode anchor-element)]
    {:label (gdom/getTextContent heading)
     :level (js/parseInt (second (re-find #"H(\d)" (.-tagName heading))))
     :id    (.-id anchor-element)}))

(defn element-TOC
  "Returns a table of contents (in Hiccup) for a DOM element"
  [el]
  (->> (gdom/findNodes el #(classes/has % "heading-anchor"))
       (clj->js)
       (keep (comp (fn [{:keys [label level id]}]
                     (when (> level 1)
                       [:a.db.no-underline.f6.color-inherit
                        {:href  (str "#" id)
                         :style {:margin-left (str (* 0.5 (- level 2)) "rem")}} label])) parse-header))))

(defview toc-page [])


(defview markdown-page
  {:spec/props              {:read :String
                             :edit :String
                             :toc? :Boolean}
   :update-toc              (fn [{:keys [view/state] :as this}]
                              (swap! state assoc :TOC (element-TOC (v/dom-node this))))
   :update                  (fn [{:keys [view/state read edit]}]
                              (swap! state assoc :loading true)
                              (util/GET :text read #(do (reset! state %))))
   :life/did-mount          (fn [this] (.update this))
   :life/will-receive-props (fn [{:keys [view/children view/prev-children] :as this}]
                              (when (not= children prev-children)
                                (.update this (first children))))
   :life/did-update         (fn [{:keys             [view/state view/prev-state]
                                  read              :read
                                  {prev-read :read} :view/prev-props
                                  :as               this}]
                              (if (not= read prev-read)
                                (.update this)
                                (.updateToc this)))}
  [{:keys [view/state read edit toc?]
    :or   {toc? true}}]
  (let [{:keys [value error loading TOC]} @state]
    (page [:div
           {:class (when loading "o-50")}
           (some->> (breadcrumb) (conj [:.f6.mt3.mb3]))
           [(if toc? :.flex.flex-column.flex-row-ns
                     :.center.w6)
            [:.flex-auto (cond error [:div "Error fetching " read]
                               value [:div
                                      (markdown/md value)
                                      (edit-button {:class "bg-darken mv3"
                                                    :href  edit})]
                               :else [:div "Loading..."])]
            (when toc?
              [:.mt4.ml3-ns (when (seq TOC)
                              [:.bl.bw2.b--darken-5.ph2.lh-copy
                               TOC])])]])))

(defn index-page [children]
  [:.flex.flex-column.flex-row-ns
   [:.ph3-ns.w5]
   [:div
    [:.f6.mt3.mb3 (breadcrumb)]
    [:h3.mv3 "Index"]
    (->> (vals children)
         (keep :*file)
         (sort-by :title)
         (map (fn [{:keys [title name path]}]
                [:p [:a {:href (str "/docs/" (string/replace path #"\.md$" ""))} title]])))]])



(def github-icon
  [:svg {:fill              "currentColor"
         :width             "24"
         :height            "24"
         :view-box          "0 0 16 16",
         :xmlns             "http://www.w3.org/2000/svg"
         :fill-rule         "evenodd"
         :clip-rule         "evenodd"
         :stroke-linejoin   "round"
         :stroke-miterlimit "1.414"}
   [:path {:d "M8 0C3.58 0 0 3.582 0 8c0 3.535 2.292 6.533 5.47 7.59.4.075.547-.172.547-.385 0-.19-.007-.693-.01-1.36-2.226.483-2.695-1.073-2.695-1.073-.364-.924-.89-1.17-.89-1.17-.725-.496.056-.486.056-.486.803.056 1.225.824 1.225.824.714 1.223 1.873.87 2.33.665.072-.517.278-.87.507-1.07-1.777-.2-3.644-.888-3.644-3.953 0-.873.31-1.587.823-2.147-.09-.202-.36-1.015.07-2.117 0 0 .67-.215 2.2.82.64-.178 1.32-.266 2-.27.68.004 1.36.092 2 .27 1.52-1.035 2.19-.82 2.19-.82.43 1.102.16 1.915.08 2.117.51.56.82 1.274.82 2.147 0 3.073-1.87 3.75-3.65 3.947.28.24.54.73.54 1.48 0 1.07-.01 1.93-.01 2.19 0 .21.14.46.55.38C13.71 14.53 16 11.53 16 8c0-4.418-3.582-8-8-8"}]])