(ns re-view-prosemirror.core
  (:require [re-view.core :as v :refer [defview]]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [goog.dom.classes :as classes]
            [re-view-material.icons :as icons]
            [re-view-material.core :as ui]
            [re-view-prosemirror.prosemirror :as pm]))

;; one remaining:
;; - if cursor is in list-item, and clicks bullet-list or ordered-list, try to set list type.

(set! *warn-on-infer* true)

(defn ^js/React.Component get-view [^js/pm.EditorView editor-view]
  (.-reView editor-view))

(def icon-el :.dib.pa2.serif)

(defn menu-icon [key pm-state dispatch cmd active? icon]
  (let [enabled? (cmd pm-state)]
    [icon-el (-> (if (false? enabled?)
                   {:className "o-30"}
                   {:className   (str "pointer hover-bg-near-white "
                                      (when active? "blue"))
                    :onMouseDown (fn [^js/Event e]
                                   (.preventDefault e)
                                   (cmd pm-state dispatch))})
                 (assoc :key key))
     (update icon 1 assoc :width 18)]))

(defn toolbar [^js/pm.EditorState state dispatch]
  (let [marks (pm/current-marks state)]
    [:div.bb.bw2.b--light-gray.ph1
     (menu-icon :strong state dispatch (pm/toggle-mark pm/strong) (contains? marks "strong") icons/FormatBold)
     (menu-icon :em state dispatch (pm/toggle-mark pm/em) (contains? marks "em") icons/FormatItalic)
     (let [in-bullet-list? (= pm/bullet-list (some-> (pm/first-ancestor (.. state -selection -$from) pm/is-list?)
                                                     (.-type)))]
       (menu-icon :bullet-list state dispatch (cond-> (pm/wrap-in-list pm/bullet-list)
                                                      in-bullet-list? (pm/chain
                                                                        pm/lift
                                                                        pm/lift-list-item))
                  in-bullet-list? icons/FormatListBulleted))
     (let [in-ordered-list? (= pm/ordered-list (some-> (pm/first-ancestor (.. state -selection -$from) pm/is-list?)
                                                       (.-type)))]
       (menu-icon :ordered-list state dispatch (cond-> (pm/wrap-in-list pm/ordered-list)
                                                       in-ordered-list? (pm/chain
                                                                          pm/lift
                                                                          pm/lift-list-item))
                  in-ordered-list? icons/FormatListOrdered))
     (menu-icon :outdent state dispatch (pm/chain
                                          pm/lift-list-item
                                          pm/lift) false icons/FormatOutdent)
     (menu-icon :indent state dispatch pm/sink-list-item false icons/FormatIndent)
     (menu-icon :code-block state dispatch (pm/chain
                                             (pm/set-block-type pm/code-block nil)
                                             (pm/set-block-type pm/paragraph nil)) (pm/is-block-type? state pm/code-block nil) icons/Code)
     (menu-icon :blockquote state dispatch (pm/wrap-in pm/blockquote) false icons/FormatQuote)
     (let [set-p (pm/set-block-type pm/paragraph nil)
           set-h1 (pm/set-block-type pm/heading #js {"level" 1})
           active? (or (set-p state) (set-h1 state))]
       (ui/DropDown nil
                    [icon-el (if active? {:className "pointer hover-bg-near-white "}
                                         {:className "o-30"}) (update icons/FormatSize 1 assoc :width 18)]
                    (when active?
                      (ui/Menu {:direction [:right :down]}
                               [:div
                                (ui/MenuItem {:key         "p"
                                              :primaryText "Paragraph"
                                              :disabled    (false? (set-p state))
                                              :onClick     #(set-p state dispatch)})
                                (for [i (range 1 4)
                                      :let [cmd (pm/set-block-type pm/heading #js {"level" i})]]
                                  (ui/MenuItem {:key         i
                                                :disabled    (false? (cmd state))
                                                :onClick     #(cmd state dispatch)
                                                :primaryText [:div {:className (str "f" i)} (str "Heading " i)]}))]))))
     ;; format-size dropdown, icons/FormatSize
     ;; ... or one button to increase size, one to decrease.

     ]))


(defview editor
  "A ProseMirror editor which (de)serializes to Markdown."
  {:initial-state {}
   :did-mount     (fn [{:keys [defaultValue view/state editor-view-props] :as ^js/React.Component this}]
                    (let [editor-state (.create pm/EditorState
                                                #js {"doc"     (.parse pm/defaultMarkdownParser (or defaultValue ""))
                                                     "schema"  pm/schema
                                                     "plugins" #js [
                                                                    (pm/history)
                                                                    pm/md-keymap
                                                                    pm/input-rules
                                                                    (pm/keymap pm/base-keymap)
                                                                    ]})
                          editor-view (-> (v/dom-node this)
                                          (gdom/findNode #(classes/has % "prosemirror-content"))
                                          (pm/EditorView. (clj->js (merge editor-view-props
                                                                          {:state      editor-state
                                                                           :attributes {:class "outline-0"}
                                                                           :dispatchTransaction
                                                                                       (fn [tr]
                                                                                         (pm/transact! (:pm-view @state) tr)
                                                                                         (v/force-update this))}))))]
                      (set! (.-reView editor-view) this)
                      (reset! state {:pm-view editor-view})))
   :getMarkdown   (fn [{:keys [view/state]}]
                    (pm/serialize-markdown (:pm-view @state)))
   :will-unmount  (fn [{:keys [view/state]}]
                    (pm/destroy! (:pm-view @state)))}
  [{:keys [value
           view/state
           view/props
           container-props] :as ^js/React.Component this}]
  (let [{:keys [pm-view]} @state]
    [:div
     (when-not (nil? pm-view)
       (toolbar (pm/state pm-view) (pm/view-dispatch-fn pm-view)))
     [:.prosemirror-content.pa3.pb0.cf
      (-> props
          (dissoc :value :onSave)
          (assoc :dangerouslySetInnerHTML {:__html ""}))]]))