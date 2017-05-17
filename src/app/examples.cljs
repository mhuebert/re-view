(ns app.examples
  (:require [re-view.core :as v :refer-macros [defview view]]
            [goog.dom :as gdom]
            [goog.dom.classes :as classes]
            [goog.object :as gobj]
            [re-view-material.example :as material-example]
            [re-view-prosemirror.example :as prosemirror-example]
            [re-view.example.helpers :as h :include-macros true]
            [re-view-material.icons :as icons]
            [clojure.string :as string]
            [re-view-material.core :as ui]
            [clojure.set :as set]
            [re-db.d :as d]
            [re-view-routing.core :as routing]
            [re-view-material.util :as util]

            cljsjs.react.dom.server
            [app.markdown :refer [md]]
            [re-view-hiccup.react.html :as html]))

(d/transact! (->> (concat material-example/examples-data
                          prosemirror-example/examples-data)
                  (map (fn [{:keys [component label] :as example}]
                         (let [{{:keys [react/docstring react/display-name] :as r} :react-keys} (aget component "re$view$base")
                               label (or label (-> display-name
                                                   (string/split "/")
                                                   (last)
                                                   (string/replace #"([a-z])([A-Z])" "$1 $2")))]
                           (merge example {:db/id           (str "ui-" (-> label
                                                                           (string/lower-case)
                                                                           (string/replace " " "-")))
                                           :label           label
                                           :react/docstring docstring
                                           :kind            :re-view/component}))))))


#_(defn table-of-contents [toc]
    (ui/PermanentDrawer
      {:class "w5 h-100 overflow-y-scroll"}
      (ui/ListGroup
        (for [[[{:keys [label id] :as subheader}] items] (->> toc
                                                              (partition-by #(= (:level %) 1))
                                                              (partition 2))]
          (list
            (ui/List {:key (str "list-" label)}
                     (ui/ListItem {:href           (str "#" id)
                                   :text-secondary label
                                   :ripple         true})
                     (map (fn [{:keys [label level id]}]
                            (case level 1 (ui/ListGroupSubheader label)
                                        (ui/ListItem {:text-primary label
                                                      :ripple       (string/starts-with? label "B")
                                                      :href         (str "#" id)}))) items))
            (ui/ListItemDivider))))))


(defview component-card
  {:key :label}
  [{:keys [view/state
           label
           db/id
           react/docstring
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
    [:.bw2.bb.dib.flex-auto.flex.flex-column.shadow-4.br2.border-box
     {:classes [(if (d/get :ui/globals :theme/dark?) "bg-dark-gray b--gray" "bg-white b--light-gray")
                (if mobile? "mb3" "ma3" )]
      :style (when mobile? (cond-> {:min-width 260}
                                   element-width (assoc :width element-width
                                                        :flex "none")))}
     [:.w-100
      [:.relative.pa3.nb3
       label
       [:a.tr.absolute.left-0.right-0.bottom-0.top-0.pointer.hover-o-100.o-40
        {:href (str "/components/" id)}
        (-> icons/ArrowExpand
            (update-in [1 :class] str " pa3 br-pill"))]]]

     [:.flex.items-center.flex-auto.pa4
      [:.center
       (if custom-view (custom-view)
                       (try (cond-> (h/with-prop-atom* nil component prop-atom)
                                    wrap (wrap))
                            (catch js/Error e "Error")))]]]))

(defview component-detail
  {:key :label}
  [{:keys [view/state
           label
           db/id
           react/docstring
           prop-atom
           component
           children
           wrap
           wrap-component
           custom-view] :as this}]

  (let [component (cond-> component
                          wrap-component (wrap-component))]
    [:.ma3.bw2.bb.shadow-4.br2.border-box.relative
     {:class (str (if (d/get :ui/globals :theme/dark?) "bg-dark-gray b--gray" "bg-white b--light-gray"))}
     [:a.pa3.ma1.absolute.top-0.right-0.dib {:href "/components"} icons/Close]

     [:.flex
      {:style {:max-height "100%"}}
      [:.flex.flex-column.ph4.mv2.pv3.mw6
       [:.mv3.f4 label]
       (when docstring
         [:.o-70.f6 (md docstring)])
       [:.flex-auto]
       [:.mv3 (if custom-view (custom-view)
                              (try (cond-> (h/with-prop-atom* nil component prop-atom)
                                           wrap (wrap))
                                   (catch js/Error e "Error")))]
       [:.flex-auto]]

      [:.pv3.b--darken-2.bt.mw6
       {:class (if (d/get :ui/globals :theme/dark?)
                 "bg-darken-7"
                 "bg-darken-3")
        :style {:max-height 500
                :overflow-y "scroll"}}
       (or (some->> prop-atom (h/props-editor {:component component}))
           [:.f6.o-60.tc.pv3 "No props"])]]]))


(def theme-mods {:accent "mdc-theme--accent-bg mdc-theme--text-primary-on-accent"})

(defview test-view
  [this & body]
  [:div "Tested this" body])

(defview toolbar
  []
  (let [query (d/get-in :router/location [:query :search])]
    [:.flex.flex-auto.items-center
     (ui/ToolbarTitle "Components")
     [:.flex-auto]
     [:.flex.items-center
      (-> icons/Search
          (icons/class "o-50 mr2 flex-none"))
      (ui/Input {:placeholder "Search"
                 :value       query
                 :style       {:max-width 100}
                 :class       "mr2 bn outline-0 pv2 bg-transparent"
                 :auto-focus  true
                 :on-change   #(routing/swap-query! assoc :search (.. % -target -value))})
      (when query
        (-> icons/Close
            (icons/class "o-50 hover-o-100 mr3 flex-none pointer")
            (assoc-in [1 :on-click] #(routing/swap-query! dissoc :search))))]]))

(defview library
  {:life/did-mount (fn [{:keys [view/state] :as this}]
                     (let [headings (gdom/findNodes (v/dom-node this)
                                                    (fn [^js/Element el]
                                                      (some->> (.-tagName el) (re-find #"H\d"))))
                           toc (map (fn [^js/Element el]
                                      {:label (gdom/getTextContent el)
                                       :level (js/parseInt (second (re-find #"H(\d)" (.-tagName el))))
                                       :id    (.-id el)}) headings)]
                       (swap! state assoc :toc toc)))}
  [{:keys [detail-view
           view/state]}]
  (let [query (d/get-in :router/location [:query :search])]
    [:div
     (when detail-view
       [:.fixed.left-0.right-0.top-0.bottom-0.z-999.flex.items-center
        {:on-click #(when (let [current-target (aget % "currentTarget")]
                            (or (= current-target (aget % "target"))
                                (= current-target (aget % "target" "parentNode"))))
                      (routing/nav! "/components"))
         :style    {:background-color (if (d/get :ui/globals :theme/dark?)
                                        "rgba(100, 100, 100, 0.9)"
                                        "rgba(255,255,255,0.85)")}}

        [:.mw8.center
         {:style {:max-height "100%"
                  :overflow-y "auto"}}
         (component-detail (d/entity detail-view))]
        ])
     (when-not query [:.tc.ph4.pt3.pb0 (md "Views for Google's [Material Design Components](https://github.com/material-components/material-components-web), and a [ProseMirror](prosemirror.net) rich text editor that reads and writes Markdown.")])
     [:div.br2.ma3.flex.justify-between.items-stretch.flex-wrap
      (->> (cond->> (->> (d/entities [[:kind :re-view/component]])
                         (sort-by :label))
                    (util/ensure-str query)
                    (filter (let [pattern (re-pattern (str "(?i)\\b" query))]
                              #(re-find pattern (:label %)))))
           (map (v/partial component-card {:mobile? (d/get :ui/globals :media/mobile?)})))]]))