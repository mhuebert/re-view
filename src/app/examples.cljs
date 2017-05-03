(ns app.examples
  (:require [re-view.core :as v :refer-macros [defview view]]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [re-view-material-example.example :as material-example]
            [re-view-prosemirror-example.example :as prosemirror-example]
            [re-view-example.helpers :as h :include-macros true]
            [re-view-material.icons :as icons]
            [clojure.string :as string]
            [re-view-material.core :as ui]
            [clojure.set :as set]
            [re-db.d :as d]
            [re-view-material.util :as util]
            [cljsjs.markdown-it]
            cljsjs.react.dom.server

            [re-view-hiccup.react.html :as html]))

(def MD ((gobj/get js/window "markdownit") "commonmark"))
(defn md [s]
  {:dangerouslySetInnerHTML {:__html (.render MD s)}})

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
                                   :text/secondary label
                                   :ripple         true})
                     (map (fn [{:keys [label level id]}]
                            (case level 1 (ui/ListGroupSubheader label)
                                        (ui/ListItem {:text/primary label
                                                      :ripple       (string/starts-with? label "B")
                                                      :href         (str "#" id)}))) items))
            (ui/ListItemDivider))))))


(defview example-view
  {:key (fn [{:keys [component]}] (.. (component) -type -displayName))}
  [{:keys [view/state label docstring prop-atom component children wrap custom-view] :as this}]

  (let [{:keys [expanded? element-width]} @state]
    [:.ma3.bw2.bb.dib.flex-auto.flex.flex-column.shadow-4.br2.border-box
     {:class (str (if (d/get :ui :theme/dark) "bg-dark-gray b--gray" "bg-white b--light-gray"))
      :style (cond-> {:min-width 260}
                     element-width (assoc :width element-width
                                          :flex "none"))}
     [:.w-100
      [:.relative.pa3.nb3
       label
       [:.tr.absolute.left-0.right-0.bottom-0.top-0.pointer.hover-o-100.o-40
        {:on-click (fn []
                     (swap! state assoc
                            :expanded? (not expanded?)
                            :element-width (if expanded? nil
                                                         (-> (v/dom-node this)
                                                             (.-offsetWidth)))))}
        (-> (if expanded?
              icons/ExpandLess icons/ExpandMore)
            (update-in [1 :class] str " pa3 br-pill"))]]
      (when (and expanded? docstring)
        [:.o-70.f6.ph3.nb3 (md docstring)])]

     [:.flex.items-center.flex-auto.pa4
      [:.flex-auto]
      (if custom-view (custom-view)
                      (try (cond-> (apply h/with-prop-atom* component prop-atom children)
                                   wrap (wrap))
                           (catch js/Error e "Error")))
      [:.flex-auto]]

     (when expanded?
       [:.ph2.pv1.bg-darken-1.b--darken-2.bt
        (or (some-> prop-atom (h/state-editor))
            [:.f6.o-60.tc.pv3 "No props"])])]))

(def all-examples (->> (concat material-example/examples-data
                               prosemirror-example/examples-data)
                       (map (fn [{:keys [component label docstring] :as example}]
                              (let [view-class (.-type (component))]
                                (merge example {:label     (or label (-> (gobj/get view-class "displayName")
                                                                         (string/split "/")
                                                                         (last)
                                                                         (string/replace #"([a-z])([A-Z])" "$1 $2")))
                                                :docstring (or docstring (gobj/get view-class "docstring"))}))))))

(def theme-mods {:accent  "mdc-theme--accent-bg mdc-theme--text-primary-on-accent"
                 :primary "mdc-theme--primary-bg mdc-theme--text-primary-on-primary"})

(defview test-view
  [this & body]
  [:div "Tested this" body])

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
  (let [query (:search @state)]

    [:.mdc-typography
     (util/sync-element!
       {:class       (if (d/get :ui :theme/dark)
                       "mdc-theme--dark bg-mid-gray white"
                       "bg-near-white")
        :style       {:min-height "100%"}
        :get-element #(when (exists? js/document)
                        (.-documentElement js/document))})



     [:.flex.items-center.relative
      #_[:.mh4.f4.pv2.ph3.serif.flex-none.i.absolute.top-0.left-0.flex.items-center.bg-darken-2
         #_(-> icons/ArrowBack
               (icons/class "mr2"))]
      [:.pv4.pt5.fw2.tc.serif.tc.flex-auto.ph4.mh3
       [:.f1.i.mv3 "Re-View Components"]
       [:.f6.mw6.lh-copy.center (md "Views for Google's [Material Design Components](https://github.com/material-components/material-components-web), and a [ProseMirror](prosemirror.net) rich text editor that reads and writes Markdown.")]]]
     [:.ph4.pv3.mv3.flex.items-center
      {:classes ["mdc-theme--dark"
                 (get theme-mods (if (d/get :ui :theme/dark) :accent :primary))]}
      (-> icons/Search
          (icons/class "o-50 mr2 flex-none"))
      (ui/Input {:placeholder "Search"
                 :value       query
                 :class       "mr2 w-100 bn outline-0 pv2 bg-transparent"
                 :auto-focus  true
                 :on-change   #(swap! state assoc :search (.. % -target -value))})

      (when (util/ensure-str query)
        (-> icons/Close
            (icons/class "o-50 hover-o-100 mr3 flex-none pointer")
            (assoc-in [1 :on-click] #(swap! state assoc :search ""))))
      [:.flex-none
       (ui/Checkbox {:name      :theme
                     :checked   (boolean (d/get :ui :theme/dark))
                     :on-change #(d/transact! [[:db/update-attr :ui :theme/dark not]])
                     :class     "flex-none"
                     :label     "Dark theme"})]]

     [:div.br2.ma3.flex.justify-between.items-stretch.flex-wrap
      (->> (cond->> all-examples
                    (util/ensure-str query)
                    (filter (let [pattern (re-pattern (str "(?i)\\b" query))]
                              #(re-find pattern (:label %)))))
           (map example-view))]]))