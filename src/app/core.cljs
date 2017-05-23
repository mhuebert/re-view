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
            [goog.functions :as gf]
            ))


(def header-background "/images/bg_sky.jpg")
(def header-logo "/images/re-view-text.png")
(def header-github-url "https://www.github.com/re-view/re-view")

(def main-nav-items (list [(-> icons/Home
                               (icons/styles :margin "-0.4rem 0")) "/"]
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
       {:class     "z-3"
        :waterfall true
        :fixed     :lastrow-only}
       (ui/ToolbarRow
         {:classes ["mdc-theme--dark ph4-ns ph3"
                    (if dark?
                      "mdc-theme--primary-bg-dark"
                      "mdc-theme--primary-bg")]}
         (ui/ToolbarSection
           {:classes ["flex items-center mw7 center"]}
           (for [[label href] main-nav-items]
             [:a.pv2.mh2.no-underline.relative
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

       [:div
        (when home?
          [:div.overflow-hidden.relative
           {:style {:background-color    "transparent"
                    :background-image    (str "url(" header-background ")")
                    :background-size     "cover"
                    :background-position "center center"
                    :background-repeat   "no-repeat"}}
           #_(ui/Button {:label   "GitHub"
                       :class   "absolute right-0 top-0 ma2"
                       :dense   true
                       :compact true
                       :raised  true
                       :color   :primary

                       :target  "_blank"
                       :icon    views/github-icon
                       :href    header-github-url})
           [:.ph5.ph0-ns.center.mt6-ns.mb5-ns.mt5.mb4
            {:style {:width      450
                     :transition "width 0.3s linear"
                     :max-width  "100%"}}

            [:a.relative.dib
             {:href  "/"
              ;; hard-code the image ratio so that toolbar size is calculated properly on image load
              :style {:display     "block"
                      :height      0
                      :padding-top "23%"}}
             [:.absolute.top-0.left-0.w-100
              [:img {:src header-logo}]]]
            [:.text-shadow.serif.tc.dib.mt4-ns.mt3.f4.f3-ns.white "Build fast, intuitive user interfaces in ClojureScript."]]])

        [:.ph4-ns.ph3.pv1.relative
         main-content]])]))

(defview root
  "The root component reads current router location from re-db,
   and will therefore re-render whenever this value changes."
  []
  (let [segments (d/get :router/location :segments)]
    (match segments
           [] (layout (views/page {:toolbar-items [(views/clojars-latest-version "re-view")
                                                   [:.flex-auto]
                                                   (views/edit-button (str "https://github.com/re-view/re-view/edit/master/INTRO.md"))]}
                                  (views/markdown-page (str "https://raw.githubusercontent.com/re-view/re-view/master/INTRO.md"))))
           ["components"] (layout (examples/library {}))
           ["components" id] (layout (examples/library {:detail-view id}))
           ["docs"] (layout (docs/doc-page "/"))
           ["docs" & doc-path] (layout (docs/doc-page (string/join "/" doc-path)))
           ["code"] (layout (code/repositories-index))
           ["code" repo] (layout (code/repository-page repo))
           ["code" repo file] (layout (code/repo-file-page repo file))

           :else [:div "not found"])))

(defn init []
  (r/listen
    (fn [route] (d/transact! [(assoc route :db/id :router/location)])))
  (v/render-to-dom (root) "app"))

(defonce _ (init))