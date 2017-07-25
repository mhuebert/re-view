(ns app.core
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [clojure.core.match :refer-macros [match]]

            [re-view.core :as v :refer-macros [defview]]
            [re-view-routing.core :as r]
            [re-view-material.core :as ui]
            [re-db.d :as d]

            [app.views :as views]
            [app.views.docs :as docs]
            [app.views.code :as code]
            [app.views.markdown :refer [md]]
            [app.views.layout :as layout]

            [app.views.components :as examples]
            [app.util :as u]

            [clojure.string :as string]

            [re-view-material.icons :as icons]
            [re-view-material.util :as util]

            [goog.events :as events]
            [goog.functions :as gf]))

(def header-github-url "https://www.github.com/braintripping/re-view")

(def main-nav-items (list [(-> icons/Home
                               (icons/style {:margin "-0.4rem 0"})) "/"]
                          ["Docs" "/docs/"]
                          ["Components" "/components"]
                          ["Code" "/code"]))


(enable-console-print!)
(let [display-mobile? #(<= (.-innerWidth js/window) 500)
      mobile? (display-mobile?)]
  (events/listen js/window "resize" (gf/throttle #(let [mobile? (display-mobile?)]
                                                    (d/transact! [{:db/id               :ui/globals
                                                                   :media/mobile?       mobile?
                                                                   :layout/drawer-open? (not mobile?)}])) 300))
  (d/transact! [{:db/id               :ui/globals
                 :media/mobile?       mobile?
                 :theme/dark?         false
                 :layout/drawer-open? (not mobile?)}]))

(defview layout [this
                 main-content]
  (let [{:keys [theme/dark?
                media/mobile?
                layout/drawer-open?]} (d/entity :ui/globals)
        home? (= (d/get :router/location :segments) [])]
    [:.mdc-typography
     (layout/page-meta)
     (ui/ToolbarWithContent
       {:classes   ["z-3"
                    (when dark? "mdc-theme--dark")]
        :style     {:background-color (if dark? "#464646" "#eaeaea")
                    :color            "inherit"}
        :waterfall true
        :fixed     true}
       (ui/ToolbarRow
         {:class "ph4-ns ph3"}
         (ui/ToolbarSection
           {:classes ["flex items-center mw-page center"]}
           (for [[label href] main-nav-items]
             [:a.pv2.mh2.no-underline.relative.color-inherit
              {:href  href
               :class (when (layout/active? href) "mdc-toolbar--active-link")} label])

           [:.flex-auto]
           ((if mobile? ui/Switch ui/SwitchField)
             (cond-> {:id        "theme"
                      :color     (when dark? :accent)
                      :checked   dark?
                      :on-change #(d/transact! [[:db/update-attr :ui/globals :theme/dark? not]])}
                     (not mobile?) (assoc :label "Dark theme"
                                          :field-classes ["ph2"])))))

       [:.ph4-ns.ph3.pv1.relative
        main-content])]))

(defview root
  "The root component reads current router location from re-db,
   and will therefore re-render whenever this value changes."
  []
  (let [segments (d/get :router/location :segments)]
    (match segments
           [] (layout [:div
                       [:.serif.tc.mw-page.center.mv3
                        [:.f0.pt4 "Re-View"]
                        [:.f4 "Simple React apps in ClojureScript."]]
                       (code/repo-file-page {:toc? false} "docs" "intro.md")])
           ["components"] (layout (examples/library {}))
           ["components" id] (layout (examples/library {:detail-view id}))
           ["docs"] (layout (docs/doc-page "/"))
           ["docs" & doc-path] (layout (docs/doc-page (string/join "/" doc-path)))
           ["code"] (layout (code/repositories-index))
           ["code" repo] (layout (code/repository-page repo))
           ["code" repo file] (layout (views/page (code/repo-file-page repo file)))

           :else [:div "not found"])))

(defn init []
  (r/listen
    (fn [route] (d/transact! [(assoc route :db/id :router/location)])))
  (v/render-to-dom (root) "app"))

(defonce _ (init))