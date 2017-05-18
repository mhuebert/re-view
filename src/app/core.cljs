(ns app.core
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [clojure.core.match :refer-macros [match]]

            [re-view.core :as v :refer-macros [defview]]
            [re-view-routing.core :as r]
            [re-view-material.core :as ui]
            [re-db.d :as d]

            [app.docs :as docs]
            [app.examples :as examples]
            [re-view-material.icons :as icons]
            [app.markdown :refer [md]]
            [clojure.string :as string]
            [re-view-material.util :as util]
            [app.util :refer [GET]]
            [goog.events :as events]
            [goog.functions :as gf]))

(enable-console-print!)
(let [display-mobile? #(<= (.-innerWidth js/window) 600)
      mobile? (display-mobile?)]
  (events/listen js/window "resize" (gf/debounce #(let [mobile? (display-mobile?)]
                                                    (d/transact! [{:db/id               :ui/globals
                                                                   :media/mobile?       mobile?
                                                                   :layout/drawer-open? (not mobile?)}])) 200))
  (d/transact! [{:db/id               :ui/globals
                 :media/mobile?       mobile?
                 :theme/dark?         false
                 :layout/drawer-open? (not mobile?)}]))
(set! v/DEBUG true)

(defn active? [href]
  (let [path (d/get :router/location :path)]
    (case href "/" (= path href)
               (string/starts-with? path href))))

(defview layout [_ toolbar-content body]
  (let [{:keys [theme/dark?
                media/mobile?
                layout/drawer-open?]} (d/entity :ui/globals)]
    [:.mdc-typography
     (util/sync-element!
       {:class       (if (d/get :ui/globals :theme/dark?)
                       "mdc-theme--dark bg-mid-gray"
                       "bg-near-white")
        :style       {:min-height "100%"}
        :get-element #(when (exists? js/document)
                        (.-documentElement js/document))})
     [:link {:rel  "stylesheet"
             :type "text/css"
             :href (if dark? "/styles/railscasts.css"
                             "/styles/github.css")}]
     (ui/ToolbarWithContent
       {:style     {:background-color    "transparent"
                    :background-image    "url(/images/bg_sky.jpg)"
                    :background-size     "cover"
                    :background-position "center bottom"
                    :background-repeat   "no-repeat"}
        :waterfall true
        :fixed     :lastrow-only}


       (ui/Button {:label   "GitHub"
                   :class   "absolute right-0 top-0 ma2"
                   :dense   true
                   :compact true
                   :color   :primary
                   :raised  true
                   :target  "_blank"
                   :icon    [:svg {:fill "currentColor" :view-box "0 0 16 16", :xmlns "http://www.w3.org/2000/svg", :fill-rule "evenodd", :clip-rule "evenodd", :stroke-linejoin "round", :stroke-miterlimit "1.414"}
                             [:path {:d "M8 0C3.58 0 0 3.582 0 8c0 3.535 2.292 6.533 5.47 7.59.4.075.547-.172.547-.385 0-.19-.007-.693-.01-1.36-2.226.483-2.695-1.073-2.695-1.073-.364-.924-.89-1.17-.89-1.17-.725-.496.056-.486.056-.486.803.056 1.225.824 1.225.824.714 1.223 1.873.87 2.33.665.072-.517.278-.87.507-1.07-1.777-.2-3.644-.888-3.644-3.953 0-.873.31-1.587.823-2.147-.09-.202-.36-1.015.07-2.117 0 0 .67-.215 2.2.82.64-.178 1.32-.266 2-.27.68.004 1.36.092 2 .27 1.52-1.035 2.19-.82 2.19-.82.43 1.102.16 1.915.08 2.117.51.56.82 1.274.82 2.147 0 3.073-1.87 3.75-3.65 3.947.28.24.54.73.54 1.48 0 1.07-.01 1.93-.01 2.19 0 .21.14.46.55.38C13.71 14.53 16 11.53 16 8c0-4.418-3.582-8-8-8"}]]
                   :href    "https://www.github.com/re-view/re-view"})

       [:.tc.center.pa4.pt6
        {:style {:max-width 500}}
        [:a.relative.db
         {:href  "/"
          ;; hard-code the image ratio so that toolbar size is calculated properly on image load
          :style {:display     "block"
                  :width       "100%"
                  :height      0
                  :padding-top "23%"}}
         [:.absolute.top-0.left-0.w-100
          [:img {:src "/images/re-view-text.png"}]]
         ]
        [:.f4.mw6.center.text-shadow.serif.mv4 "Build fast, intuitive user interfaces in ClojureScript."]]
       (ui/ToolbarRow
         {:classes ["mdc-theme--dark"
                    (if dark?
                      "mdc-theme--primary-bg-dark"
                      "mdc-theme--primary-bg")]}
         (ui/ToolbarSection
           {:classes ["flex" "items-center"]}
           [:.pointer.o-70.hover-o-100.z-2.white
            {:style    {:margin-right "1rem"
                        :margin-top   3}
             :on-click #(d/transact! [[:db/update-attr :ui/globals :layout/drawer-open? not]])} (icons/class icons/Menu "svg-shadow")]
           toolbar-content
           (ui/SwitchField {:id        "theme"
                            :color     (when dark? :accent)
                            :checked   dark?
                            :on-change #(d/transact! [[:db/update-attr :ui/globals :theme/dark? not]])
                            :label     "Dark theme"})))


       [:.flex.items-stretch
        (let [sidebar-content (->> [{:text-primary "Docs"
                                     :href         "/docs/"}
                                    {:text-primary "Components"
                                     :href         "/components"}]
                                   (map (fn [{:keys [href] :as item}]
                                          (cond-> (assoc item :style {:padding-left "1.75rem"})

                                                  (active? href) (update :classes conj (if dark? "bg-darken-10" "bg-darken-3")))))
                                   (map ui/ListItem)
                                   (apply ui/List))]
          (if mobile? (ui/TemporaryDrawer
                        {:class    "z-3"
                         :open?    drawer-open?
                         :on-open  #(d/transact! [[:db/add :ui/globals :layout/drawer-open? true]])
                         :on-close #(d/transact! [[:db/add :ui/globals :layout/drawer-open? false]])}
                        sidebar-content)
                      (if drawer-open? (ui/PermanentDrawer {:class "flex-none"
                                                            :style {:height "auto"}}
                                                           sidebar-content))))
        [:.flex-auto body]])]))

(defn home-toolbar []
  (ui/ToolbarSection
    {:class "flex items-center"}
    (ui/ToolbarTitle "")
    [:.flex-auto]))

(defview root
  "The root component reads current router location from re-db,
   and will therefore re-render whenever this value changes."
  []
  (let [segments (d/get :router/location :segments)]
    (match segments
           (:or [] ["components"]) (layout (examples/toolbar)
                                           (examples/library nil))
           ["components" id] (layout (examples/toolbar)
                                     (examples/library {:detail-view id}))
           ["docs"] (layout (docs/toolbar)
                            (docs/home))
           ["docs" & doc-path] (layout (docs/toolbar)
                                       (docs/page doc-path))
           :else [:div "not found"])))

(defn init []
  (r/listen
    (fn [route] (d/transact! [(assoc route :db/id :router/location)])))
  (v/render-to-dom (root) "app"))

(defonce _ (init))


;; X menu icon in toolbar
;; X toolbar controlled per-component in router
;; X search via url
;; X search bar in component library
;; X fix fixed-adjust-size, doesn't work when changing orientation
;; X permanent drawer in desktop
;; X URLs for components
;; tighter spacing / smaller grid in mobile
;; read docs from repo / render markdown inline
;; expanded view should take up whole screen
