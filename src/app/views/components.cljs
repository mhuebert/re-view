(ns app.views.components
  (:require [re-view.core :as v :refer-macros [defview view]]
            [goog.dom :as gdom]
            [goog.dom.classes :as classes]
            [goog.object :as gobj]
            [re-view-routing.core :as routing]
            [re-view-material.example :as material-example]
            [re-view-prosemirror.example :as prosemirror-example]
            [re-view.example.helpers :as h :include-macros true]
            [re-view-material.icons :as icons]
            [re-view.hoc :as hoc]
            [clojure.string :as string]
            [re-view-material.core :as ui]
            [clojure.set :as set]
            [re-db.d :as d]
            [re-view-routing.core :as routing]
            [re-view-material.util :as util]

            cljsjs.react.dom.server
            [app.views.markdown :refer [md]]
            [re-view-hiccup.react.html :as html]))

(d/transact! (->> (concat material-example/examples-data
                          prosemirror-example/examples-data)
                  (map (fn [{:keys [component label] :as example}]
                         (let [{{:keys [docstring display-name] :as r} :react-keys} (aget component "re$view$base")
                               label (or label (-> display-name
                                                   (string/split "/")
                                                   (last)
                                                   (string/replace #"([a-z])([A-Z])" "$1 $2")))]
                           (merge example {:db/id     (str "ui-" (-> label
                                                                     (string/lower-case)
                                                                     (string/replace " " "-")))
                                           :label     label
                                           :docstring docstring
                                           :kind      :re-view/component}))))))

(defview component-card
  {:key :label}
  [{:keys [view/state
           label
           db/id
           docstring
           prop-atom
           component
           children
           wrap
           wrap-component
           custom-view
           mobile?] :as this}]
  (let [{:keys [element-width]} @state
        component (cond-> component
                          wrap-component (wrap-component))]
    [:.dib.flex-auto.flex.flex-column.border-box.elevated-card.mv2.ma3-ns
     {:style (when mobile? (cond-> {:min-width 260}
                                   element-width (assoc :width element-width
                                                        :flex "none")))}
     [:.flex.items-center.pt2
      [:.flex-auto.pl3 label]
      (ui/Button {:icon  icons/ArrowExpand
                  :href  (str "/components/" id)
                  :class "o-50 hover-o-100"
                  })]

     [:.flex.items-center.flex-auto.pa4
      [:.center
       (if custom-view (custom-view)
                       (try (cond-> (hoc/bind-atom component prop-atom)
                                    wrap (wrap))
                            (catch js/Error e
                              (.log js/console e)
                              "Error")))]]]))

(defview component-detail
  {:key :label}
  [{:keys [view/state
           label
           db/id
           docstring
           prop-atom
           component
           children
           wrap
           wrap-component
           custom-view] :as this}]

  (let [component (cond-> component
                          wrap-component (wrap-component))]
    [:.relative
     (ui/Button {:icon  icons/Close
                 :class "mv2 absolute top-0 right-0"
                 :href  "/components"})

     [:.flex.flex-row-l.flex-column
      {:style {:max-height "100%"}}
      [:.flex.flex-column.ph4.mv2.pv3.mw6-l
       [:.mv3.f4 label]
       (when docstring
         (md {:class "o-70 f6"} docstring))
       [:.flex-auto]
       [:.mv3 (if custom-view (custom-view)
                              (try (cond-> (hoc/bind-atom component prop-atom)
                                           wrap (wrap))
                                   (catch js/Error e "Error")))]
       [:.flex-auto]]

      (some->> prop-atom (h/props-editor {:component       component
                                          :container-props {:style {:max-height 500
                                                                    :min-width  200
                                                                    :overflow-y "auto"}
                                                            :class "pa3 pt0 b--darken-2 bt bg-darken mw6-l"}}))]]))


(def theme-mods {:accent "mdc-theme--accent-bg mdc-theme--text-primary-on-accent"})

(defview test-view
  [this & body]
  [:div "Tested this" body])

(defview fixed-content
  {:spec/props        {:on-close {:spec :Function
                                  :doc  "Function will be called when full-screen mode is exited."}}
   :spec/children     [{:spec :Element
                        :doc  "Element will be displayed centered on a full-screen, fixed background"}]
   :view/did-mount    #(classes/add js/document.body "overflow-hidden")
   :view/will-unmount #(classes/remove js/document.body "overflow-hidden")}
  [{:keys [on-close]} content]
  [:.fixed.left-0.right-0.top-0.bottom-0.z-999.flex.items-center
   {:on-click #(when (let [current-target (aget % "currentTarget")]
                       (or (= current-target (aget % "target"))
                           (= current-target (aget % "target" "parentNode"))))
                 (on-close))
    :style    {:background-color (if (d/get :ui/globals :theme/dark?)
                                   "rgba(100, 100, 100, 0.9)"
                                   "rgba(255,255,255,0.85)")
               :overflow-y       "auto"}}
   [:.mw8.center-l.w-100.w-auto-l
    {:style {:max-height "100%"}}
    [:.bw2.bb.shadow-4.br2.border-box.relative.ma2.ma3-l
     {:class (str (if (d/get :ui/globals :theme/dark?) "bg-dark-gray b--gray" "bg-white b--light-gray"))}
     content]]])

(defview library
  [{:keys [detail-view
           view/state]}]
  (let [query (d/get-in :router/location [:query :search])]
    [:.pv3
     (when detail-view
       (fixed-content {:on-close #(routing/nav! "/components")}
                      (component-detail (d/entity detail-view))))
     (when-not query [:.tc.pb0.mw-page.center.lh-copy (md "Views for Google's [Material Design Components](https://github.com/material-components/material-components-web), and a [ProseMirror](http://www.prosemirror.net) rich text editor that reads and writes Markdown.")])
     [:div.br2.flex.justify-between.items-stretch.flex-wrap.mw-page.center
      (let [query (d/get-in :router/location [:query :search])]
        [:.flex.items-center.w-100.ph3-ns
         (-> icons/Search
             (icons/class "o-50 mr2 flex-none"))
         (ui/Input {:placeholder "Search"
                    :value       query
                    :class       "mr2 bn outline-0 pv2 bg-transparent flex-auto"
                    :auto-focus  true
                    :on-change   #(routing/swap-query! assoc :search (.. % -target -value))})
         (when query
           (-> icons/Close
               (icons/class "o-50 hover-o-100 flex-none pointer")
               (assoc-in [1 :on-click] #(routing/swap-query! dissoc :search))))])
      (->> (cond->> (->> (d/entities [[:kind :re-view/component]])
                         (sort-by :label))
                    (util/ensure-str query)
                    (filter (let [pattern (re-pattern (str "(?i)\\b" query))]
                              #(re-find pattern (:label %)))))
           (map (v/partial component-card {:mobile? (d/get :ui/globals :media/mobile?)})))]]))