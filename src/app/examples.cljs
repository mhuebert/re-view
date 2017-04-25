(ns app.examples
  (:require [re-view.core :as v :refer-macros [defview view]]
            [goog.dom :as gdom]
            [re-view-material-example.example :as material-example]
            [re-view-prosemirror-example.example :as prosemirror-example]
            [re-view-example.helpers :as h :include-macros true]
            [re-view-material.icons :as icons]
            [clojure.string :as string]
            [re-view-material.core :as ui]
            [clojure.set :as set]
            [re-db.d :as d]
            [re-view-material.util :as util]))

(defn table-of-contents [toc]
  (ui/PermanentDrawer
    {:className "w5 h-100 overflow-y-scroll"}
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

(defview re-view-examples
  {:did-mount (fn [{:keys [view/state] :as this}]
                (let [headings (gdom/findNodes (v/dom-node this)
                                               (fn [^js/Element el]
                                                 (some->> (.-tagName el) (re-find #"H\d"))))
                      toc (map (fn [^js/Element el]
                                 {:label (gdom/getTextContent el)
                                  :level (js/parseInt (second (re-find #"H(\d)" (.-tagName el))))
                                  :id    (.-id el)}) headings)]
                  (swap! state assoc :toc toc)))}
  [{:keys [view/state]}]

  [:.mdc-typography
   (util/html-props {:classes (if (d/get :ui :theme/dark)
                                ["mdc-theme--dark" "bg-mid-gray" "white"]
                                ["bg-near-white"])
                     :style   {:minHeight "100%"}})
   [:.f1.pv4.pt5.fw2.tc.serif.i "Re-View Material"]
   [:.ph4.pv3.mv3.flex.items-center
    {:className (str "mdc-theme--dark" (if (d/get :ui :theme/dark) " mdc-theme--accent-bg  " " mdc-theme--primary-bg"))}
    (-> icons/Search
        (icons/class "o-50 mr2"))
    (ui/Input {:placeholder "Search"
               :value       (:search @state)
               :className   "mr3 w-100 bn outline-0 pv2 bg-transparent"
               :autoFocus   true
               :onChange    #(swap! state assoc :search (.. % -target -value))})

    (ui/Checkbox {:name      :theme
                  :checked   (d/get :ui :theme/dark)
                  :onChange  #(d/transact! [[:db/update-attr :ui :theme/dark not]])
                  :className "flex-none"
                  :label     "Dark theme"})]

   [:div.br2.ma3.flex.justify-between.items-stretch.flex-wrap
    (for [{:keys [label prop-atom component children wrap custom-view]} (cond->> (concat material-example/examples-data
                                                                                         prosemirror-example/examples-data)
                                                                                 (util/ensure-str (:search @state))
                                                                                 (filter (let [search (string/lower-case (:search @state))]
                                                                                           #(string/starts-with?
                                                                                              (string/lower-case (:label %))
                                                                                              search))))]
      ((view {:key label}
             [{:keys [view/state]}]
             (let [{:keys [edit-mode]} @state]
               [:.ma3.bw2.bb.dib.flex-auto.flex.flex-column.shadow-4.br2
                {:className (str (if (d/get :ui :theme/dark) "bg-dark-gray b--gray" "bg-white b--light-gray"))}

                [:.flex.w-100.pa3.pb0.nb3.items-center
                 label
                 [:.flex-auto]

                 (when prop-atom
                   [:.pointer.na2
                    {:onClick #(swap! state update :edit-mode not)}
                    (-> (if edit-mode
                          icons/ExpandLess icons/ExpandMore)
                        (assoc-in [1 :className] " pa2 br-pill hover-o-100 o-50"))])]
                [:.flex.items-center.flex-auto.pa4
                 [:.flex-auto]
                 (if custom-view (custom-view)
                                 (try (cond-> (apply h/with-prop-atom* component prop-atom children)
                                              wrap (wrap))
                                      (catch js/Error e "Error")))
                 [:.flex-auto]]
                (when edit-mode
                  [:.ph3.b--darken-2.bt.bg-darken-1
                   (or (some-> prop-atom (h/state-editor))
                       [:.f6.o-60.tc.pv3 "No props"])])]))))]])