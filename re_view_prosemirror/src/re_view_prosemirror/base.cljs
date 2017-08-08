(ns re-view-prosemirror.base
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.core :as pm]))

;; todo
;; Editor accepts :default-value and :value but is not an ordinary controlled component.
;; Behaving as a controlled component could be problematic, because serializing on every
;; change would slow down editing on large documents (but be consistent w/ React inputs).
;; *INTERIM MEASURE* - a CHANGED :value prop will replace the current editor state, but
;; passing the same value does not prevent edits to the local editor state.


(defview Editor
         "A ProseMirror editor view."
         {:spec/props              {:input-rules       :Vector
                                    :serialize         {:spec :Function
                                                        :doc  "Should convert a ProseMirror doc to Markdown."}
                                    :parse             {:spec :Function
                                                        :doc  "Should convert a Markdown string ot a ProseMirror doc."}
                                    :schema            :Object
                                    :on-dispatch       {:spec :Function
                                                        :doc  "(this, EditorView) - called after every update."}
                                    :editor-view-props {:spec :Map
                                                        :doc  "Passed to the EditorView constructor."}
                                    :keymap            {:spec :Object
                                                        :doc  "Merged as the highest-priority keymap (http://prosemirror.net/docs/ref/#keymap)."}
                                    :default-value     {:spec :String
                                                        :doc  "Parsed as the initial editor state."
                                                        :pass true}
                                    :value             {:spec :String
                                                        :doc  "Behaves differently from ordinary React controlled inputs. When a *new/different* :value is passed, it replaces the current doc, but continuing to pass the same :value does not freeze local state."
                                                        :pass true}}
          :view/did-mount          (fn [{:keys [value
                                                default-value
                                                on-dispatch
                                                view/state
                                                editor-view-props
                                                keymap
                                                input-rules
                                                plugins
                                                parse
                                                schema] :as this}]
                                     (let [editor-state (.create pm/EditorState
                                                                 #js {"doc"     (parse (or value default-value ""))
                                                                      "schema"  schema
                                                                      "plugins" (cond-> [(.history pm/history)
                                                                                         (.inputRules pm/pm
                                                                                                      #js {:rules (to-array (into input-rules
                                                                                                                                  (.-allInputRules pm/pm)))})]
                                                                                        keymap (conj (pm/keymap (clj->js keymap)))
                                                                                        plugins (into plugins)
                                                                                        false (conj (pm/keymap pm/keymap-base))
                                                                                        true (to-array))})
                                           editor-view (-> (v/dom-node this)
                                                           (pm/EditorView. (clj->js (merge editor-view-props
                                                                                           {:state      editor-state
                                                                                            :spellcheck false
                                                                                            :attributes {:class "outline-0"}
                                                                                            :dispatchTransaction
                                                                                                        (fn [tr]
                                                                                                          (let [^js/pm.EditorView pm-view (get @state :pm-view)
                                                                                                                prev-state (.-state pm-view)]
                                                                                                            (pm/transact! pm-view tr)
                                                                                                            (when-not (nil? on-dispatch)
                                                                                                              (on-dispatch this pm-view prev-state))))}))))]
                                       (set! (.-reView editor-view) this)
                                       (reset! state {:pm-view editor-view})))
          :reset-doc               (fn [{:keys [view/state parse schema]} string-value]
                                     (let [view (:pm-view @state)]
                                       (.updateState view
                                                     (.create pm/EditorState #js {"doc"     (parse string-value)
                                                                                  "schema"  schema
                                                                                  "plugins" (aget view "state" "plugins")}))))
          :view/will-receive-props (fn [{value               :value
                                         {prev-value :value} :view/prev-props
                                         :as                 this}]
                                     (when (and value
                                                (not= value prev-value))
                                       (.resetDoc this value)))
          :pm-view                 #(:pm-view @(:view/state %))
          :view/will-unmount       (fn [{:keys [view/state]}]
                                     (pm/destroy! (:pm-view @state)))
          :serialize               (fn [{:keys [view/state serialize]}]
                                     (when-let [doc (some-> (:pm-view @state)
                                                            (aget "state" "doc"))]
                                       (serialize doc)))}
         [this]
         [:.prosemirror-content
          (-> (merge {:on-click #(when (= (.-target %) (.-currentTarget %))
                                   (.focus (.pmView this)))}
                     (v/pass-props this))
              (assoc :dangerouslySetInnerHTML {:__html ""}))])

