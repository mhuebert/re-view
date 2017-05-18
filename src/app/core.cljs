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
        [:.f4.mw6.center.text-shadow.serif.mv4 "Create beautiful, fast, and intuitive user experiences with ClojureScript."]]
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

  (match (d/get :router/location :segments)

         (:or [] ["components"]) (layout (examples/toolbar)
                                         (examples/library nil))
         ["components" id] (layout (examples/toolbar)
                                   (examples/library {:detail-view id}))
         ["docs"] (layout (docs/toolbar)
                          (docs/home))
         ["docs" & segments] (layout (docs/toolbar)
                                     (docs/page segments))
         :else [:div "not found"]))

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
