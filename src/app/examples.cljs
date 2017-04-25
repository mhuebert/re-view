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
            [re-db.d :as d]))

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

  [:.flex-auto.pa3
   {:className (if (d/get :ui :theme/dark) "mdc-theme--dark bg-mid-gray white" "bg-near-white")}

   [:.f1.pt5.fw2.tc.serif.i "Re-View Material"]
   [:.br2.pa3.mv3
    (ui/Checkbox {:name     :theme
                  :checked  (boolean (:theme/dark @state))
                  :onChange #(d/transact! [[:db/update-attr :ui :theme/dark not]])
                  :label    "Dark theme"})]

   [:div.br2.mt2.flex.justify-between.items-stretch.flex-wrap
    (for [{:keys [label prop-atom component children wrap custom-view]} (concat material-example/examples-data
                                                                                prosemirror-example/examples-data)]
      ((view {:key label}
             [{:keys [view/state]}]
             (let [{:keys [edit-mode]} @state]
               [:.ma3.bw2.bb.dib.flex-auto.flex.flex-column.shadow-4.br2
                {:className (str (if (d/get :ui :theme/dark) "bg-dark-gray b--gray" "bg-white b--light-gray"))}
                (if prop-atom
                  (cond-> [:.flex.w-100.pa3.overflow-hidden
                           {:className "mdc-ripple-surface pointer"
                            :onClick   #(swap! state update :edit-mode not)}
                           label
                           [:.flex-auto]
                           (if edit-mode
                             icons/ExpandLess icons/ExpandMore)]
                          prop-atom (ui/Ripple))
                  [:.pa3 label])
                [:.flex.items-center.flex-auto.pv3.ph4
                 [:.flex-auto]
                 (if custom-view (custom-view)
                                 (try (cond-> (apply h/with-prop-atom* component prop-atom children)
                                              wrap (wrap))
                                      (catch js/Error e "Error")))
                 [:.flex-auto]]
                (when edit-mode
                  [:.ph3
                   (or (some-> prop-atom (h/state-editor))
                       [:.f6.o-60.tc.pv3 "No props"])])]))))]])