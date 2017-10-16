(ns re-view-prosemirror.base
  (:require [re-view.core :as v :refer [defview]]
            [re-view-prosemirror.core :as pm]
            ["prosemirror-history" :refer [history]]
            ["prosemirror-keymap" :as keymap]
            ["prosemirror-commands" :as commands]
            [goog.object :as gobj]))

;; todo
;; Editor accepts :default-value and :value but is not an ordinary controlled component.
;; Behaving as a controlled component could be problematic, because serializing on every
;; change would slow down editing on large documents (but be consistent w/ React inputs).
;; *INTERIM MEASURE* - a CHANGED :value prop will replace the current editor state, but
;; passing the same value does not prevent edits to the local editor state.


(defview Editor
         "A ProseMirror editor view."
  {:spec/props              {:input-rules       :Vector
                             :doc               {:spec object?
                                                 :doc  "A prosemirror doc"}
                             :serialize         {:spec :Function
                                                 :doc  "Should convert a ProseMirror doc to Markdown."}
                             :parse             {:spec :Function
                                                 :doc  "Should convert a Markdown string ot a ProseMirror doc."}
                             :schema            {:spec #(and (gobj/containsKey % "nodes")
                                                             (gobj/containsKey % "marks"))
                                                 :doc  "a ProseMirror schema"}
                             :on-dispatch       {:spec :Function
                                                 :doc  "(this, EditorView) - called after every update."}
                             :editor-view-props {:spec :Map
                                                 :doc  "Passed to the EditorView constructor."}
                             :keymap            {:spec :Map
                                                 :doc  "Merged as the highest-priority keymap (http://prosemirror.net/docs/ref/#keymap)."}
                             :default-value     {:spec :String
                                                 :doc  "Parsed as the initial editor state."
                                                 :pass true}
                             :value             {:spec :String
                                                 :doc  "Behaves differently from ordinary React controlled inputs. When a *new/different* :value is passed, it replaces the current doc, but continuing to pass the same :value does not freeze local state."
                                                 :pass true}}
   :view/did-mount          (fn [{:keys       [value
                                               default-value
                                               on-dispatch
                                               view/state
                                               editor-view-props
                                               input-rules
                                               plugins
                                               parse
                                               doc
                                               schema]
                                  user-keymap :keymap
                                  :as         this}]
                              (let [editor-state (.create pm/EditorState
                                                          #js {"doc"     (or doc (parse (or value default-value "")))
                                                               "schema"  schema
                                                               "plugins" (cond-> [(history)
                                                                                  (pm/inputRules
                                                                                    #js {:rules (to-array (into input-rules
                                                                                                                pm/allInputRules))})]
                                                                                 user-keymap (conj (keymap/keymap (clj->js user-keymap)))
                                                                                 plugins (into plugins)
                                                                                 false (conj (keymap/keymap commands/baseKeymap))
                                                                                 true (to-array))})
                                    editor-view  (-> (v/dom-node this)
                                                     (pm/EditorView. (clj->js (merge editor-view-props
                                                                                     {:state      editor-state
                                                                                      :spellcheck false
                                                                                      :attributes {:class "outline-0"}
                                                                                      :dispatchTransaction
                                                                                                  (fn [tr]
                                                                                                    (let [^js/pm.EditorView pm-view (get @state :pm-view)
                                                                                                          prev-state                (.-state pm-view)]
                                                                                                      (pm/transact! pm-view tr)
                                                                                                      (when-not (nil? on-dispatch)
                                                                                                        (on-dispatch this pm-view prev-state))))}))))]
                                (set! (.-reView editor-view) this)
                                (reset! state {:pm-view editor-view})))
   :reset-doc               (fn [{:keys [view/state parse schema]} new-value]
                              (let [view (:pm-view @state)]
                                (.updateState view
                                              (.create pm/EditorState #js {"doc"     (cond-> new-value
                                                                                             (string? new-value) (parse))
                                                                           "schema"  schema
                                                                           "plugins" (aget view "state" "plugins")}))))
   :view/will-receive-props (fn [{value             :value
                                  doc               :doc
                                  {prev-value :value
                                   prev-doc   :doc} :view/prev-props
                                  :as               this}]
                              (when (or (and value
                                             (not= value prev-value))
                                        (and doc
                                             (not= doc prev-doc)))
                                (.resetDoc this (or doc value))))
   :pm-view                 #(:pm-view @(:view/state %))
   :view/will-unmount       (fn [{:keys [view/state]}]
                              (pm/destroy! (:pm-view @state)))
   :serialize               (fn [{:keys [view/state serialize]}]
                              (some-> (:pm-view @state)
                                      (gobj/getValueByKeys "state" "doc")
                                      (serialize)))}
         [this]
         [:.prosemirror-content
          (-> (v/pass-props this)
              (assoc :dangerouslySetInnerHTML {:__html ""}))])