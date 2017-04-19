(ns re-view-prosemirror.prosemirror
  (:require [bundle.prosemirror]
            [clojure.set :as set]))

;; javacript interop with the Prosemirror bundle
;; (type hints for externs inference)

(set! *warn-on-infer* true)

(def pm (.-pm js/window))
(def commands (.-commands pm))
(def chain (.-chainCommands commands))

(def ^js/pm.EditorView EditorView (.-EditorView pm))
(def ^js/pm.EditorState EditorState (.-EditorState pm))
(def ^js/pm.Schema schema (.-schema pm))

(defn ^js/pm.Transaction scroll-into-view [^js/pm.Transaction tr]
  (.scrollIntoView tr))

(def nodes (.-nodes schema))
(def marks (.-marks schema))
(def toggle-mark (.-toggleMark commands))
(def wrap-in-list (.-wrapInList pm))
(def input-rule-wrap (.-wrappingInputRule pm))
(def input-rule-text (.-textblockTypeInputRule pm))


(def defaultMarkdownSerializer (.-defaultMarkdownSerializer pm))
(def defaultMarkdownParser (.-defaultMarkdownParser pm))
(defn serialize-markdown [^js/pm.EditorView pm-view]
  (when-let [doc (some-> pm-view
                         (.. -state -doc))]
    (.serialize defaultMarkdownSerializer doc)))

(def history (.. pm -history -history))
(def paragraph (.-paragraph nodes))
(def bullet-list (.-bullet_list nodes))
(def ordered-list (.-ordered_list nodes))
(def heading (.-heading nodes))
(def blockquote (.-blockquote nodes))
(def code-block (.-code_block nodes))
(def list-item (.-list_item nodes))
(def em (.-em marks))
(def code (.-code marks))
(def strong (.-strong marks))

(def lift-list-item (.liftListItem pm list-item))
(def lift (.-lift commands))
(def sink-list-item (.sinkListItem pm list-item))

(def keymap (.-keymap pm))
(def base-keymap (.-baseKeymap commands))

(def md-keymap
  (let [marks (.-marks schema)
        cmd-hard-break (chain
                         (.-exitCode commands)
                         (fn [^js/pm.EditorState state dispatch]
                           (dispatch (->> (.create (.-hard_break nodes))
                                          (.replaceSelectionWith (.-tr state))
                                          (scroll-into-view)))
                           true))
        lift (chain lift-list-item lift)]
    (.keymap pm (-> (merge {"Mod-z"        (.-undo history)
                            "Mod-y"        (.-redo history)
                            "Mod-s"        (fn [state _ ^js/pm.EditorView pm-view]
                                             (let [{:keys [onSave] :as this} (.-reView pm-view)]
                                               (when onSave (onSave (serialize-markdown pm-view))))
                                             true)
                            "Backspace"    (.-undoInputRule pm)
                            "Mod-b"        (toggle-mark strong)
                            "Mod-i"        (toggle-mark em)
                            "Mod-`"        (toggle-mark code)
                            "Shift-Ctrl-8" (wrap-in-list bullet-list)
                            "Shift-Ctrl-9" (wrap-in-list ordered-list)
                            "Ctrl->"       (.wrapIn commands list-item)
                            "Shift-Ctrl-0" (.setBlockType commands paragraph)
                            "Enter"        (.splitListItem pm list-item)
                            "Mod-["        lift
                            "Shift-Tab"    lift
                            "Mod-]"        sink-list-item
                            "Tab"          sink-list-item
                            "Mod-Enter"    cmd-hard-break
                            "Shift-Enter"  cmd-hard-break
                            "Ctrl-Enter"   cmd-hard-break}
                           (reduce (fn [m i]
                                     (assoc m (str "Shift-Ctrl-" i) (.setBlockType commands heading #js {"level" i}))) {} (range 1 7)))
                    (clj->js)))))


(def input-rules (.inputRules pm
                              #js {"rules" (-> #js [(input-rule-wrap #"^>\s" blockquote)
                                                    (input-rule-wrap #"^(\d+)\.\s$"
                                                                     ordered-list
                                                                     (fn [match] #js {"order" (second match)}))
                                                    (input-rule-wrap #"^\s*([-+*])\s$" bullet-list)
                                                    (input-rule-text #"^```$" code-block)
                                                    (input-rule-text #"^(#{1,6})\s$" heading (fn [match]
                                                                                               #js {"level" (count (second match))}))]
                                               (.concat (.-allInputRules pm)))}))

(defn range-nodes [^js/pm.Node node start end]
  (let [out #js []]
    (.nodesBetween node start end #(.push out %))
    (vec out)))

(defn selection-nodes [^js/pm.Node doc ^js/pm.Selection selection]
  (->> (.-ranges selection)
       (reduce (fn [nodes ^js/pm.NodeRange range]
                 (into nodes (range-nodes doc (.. range -$from -pos) (.. range -$to -pos)))) [])))

(defn mark-name [^js/pm.Mark mark]
  (.. mark -type -name))

(defn current-marks
  "Set of marks applied to current cursor position or selection(s)"
  [^js/pm.EditorState pm-state]
  (let [selection (.-selection pm-state)]
    (if-let [cursor (.-$cursor selection)]
      (->> (or (.-storedMarks pm-state) (.marks cursor))
           (mapv mark-name)
           (into #{}))
      (->> (selection-nodes (.-doc pm-state) selection)
           (reduce (fn [marks ^js/pm.Node node]
                     (into marks (mapv mark-name (.-marks node)))) #{})))))

(defn has-mark? [^js/pm.EditorState pm-state ^js/pm.Node kind]
  (if-let [cursor (.. pm-state -selection -$cursor)]
    (.isInSet kind (or (.-storedMarks pm-state) (.marks cursor)))
    (every? true? (map (fn [^js/pm.SelectionRange range]
                         (.rangeHasMark (.-doc pm-state)
                                        (.. range -$from -pos)
                                        (.. range -$to -pos)
                                        kind))
                       (.. pm-state -selection -ranges)))))



(defn view-dispatch-fn [^js/pm.EditorView pm-view]
  (.-dispatch pm-view))

(defn state [^js/pm.EditorView pm-view]
  (.-state pm-view))

(defn transact! [^js/pm.EditorView pm-view tr]
  (.updateState pm-view (.apply (.-state pm-view) tr)))

(defn destroy! [^js/pm.EditorView pm-view]
  (.destroy pm-view))

(defn is-list? [^js/pm.Node node]
  (contains? #{ordered-list
               bullet-list} (.-type node)))

(defn first-ancestor [^js/pm.ResolvedPos pos pred]
  (loop [^js/pm.Node node (.node pos)
         depth (some-> pos .-depth)]
    (cond (not node) nil
          (pred node) node
          :else (recur (.node pos (dec depth))
                       (dec depth)))))

(defn descends-from? [^js/pm.ResolvedPos $from kind attrs]
  (first-ancestor $from (fn [^js/pm.Node node]
                         (.hasMarkup node kind attrs))))

(defn is-block-type? [^js/pm.EditorState state ^js/pm.NodeType kind attrs]
  (let [^js/pm.Selection selection (.-selection state)]
    (if-let [^js/pm.Node node (.-node selection)]
      (.hasMarkup node kind attrs)
      (let [$from ^js/pm.ResolvedPos (.-$from selection)]
        (and (<= (.-to selection) (.end $from))
             (.hasMarkup (.-parent $from) kind attrs))))))

(defn set-block-type [kind attrs]
  (.setBlockType commands kind attrs))

(defn wrap-in [^js/pm.NodeType kind]
  (.wrapIn commands kind))