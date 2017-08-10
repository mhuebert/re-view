(ns re-view-prosemirror.core
  (:require [pack.prosemirror]

            [goog.object :as gobj]
            [clojure.string :as string]))

;; javacript interop with the Prosemirror bundle
;; (type hints for externs inference)

(set! *warn-on-infer* true)

(def ^js/Object pm (.-pm js/window))
(def ^js/Object commands (gobj/get pm "commands"))


(def InputRule (gobj/get pm "InputRule"))
(def TextSelection (gobj/get pm "TextSelection"))
(def NodeSelection (gobj/get pm "NodeSelection"))
(def Selection (gobj/get pm "Selection"))

(def Schema (gobj/get pm "Schema"))

(def history (gobj/get pm "history"))


(def ^js/pm.EditorView EditorView (gobj/get pm "EditorView"))
(def ^js/pm.EditorState EditorState (gobj/get js/pm "EditorState"))

(def chain (gobj/get commands "chainCommands"))

(defn ^js/pm.Schema ensure-schema [state-or-schema]
  (cond-> state-or-schema
          (= (gobj/get state-or-schema "constructor") EditorState) (gobj/get "schema")))

(defn ^js/pm.MarkType get-mark [state-or-schema mark-name]
  (gobj/getValueByKeys (ensure-schema state-or-schema) "marks" (name mark-name)))

(defn ^js/pm.NodeType get-node [state-or-schema node-name]
  (aget (ensure-schema state-or-schema) "nodes" (name node-name)))

(defn ^js/pm.Transaction scroll-into-view [^js/pm.Transaction tr]
  (.scrollIntoView tr))

(defn toggle-mark
  [mark-name]
  (fn [state dispatch]
    (let [the-command (.toggleMark commands (get-mark state mark-name))]
      (the-command state dispatch))))

(defn cursor-node [^pm.EditorState state]
  (let [^pm.Selection sel (.-selection state)]
    (or (.-node sel) (.-parent (.-$from sel)))))

(defn cursor-depth [state]
  (.-depth (.-$from (.-selection state))))

(defn toggle-mark-tr [state mark-name]
  (let [sel (.-selection state)
        empty (.-empty sel)
        $cursor (.-$cursor sel)
        mark-type (get-mark state mark-name)
        the-node (cursor-node state)
        tr (.-tr state)]
    (when (and $cursor
               (not (or (and empty (not $cursor))
                        (not (.-inlineContent the-node))
                        (not (.allowsMark (.contentMatchAt the-node 0))))))
      (if (.isInSet mark-type (or (.-storedMarks state) (.marks $cursor)))
        (.removeStoredMark tr mark-type)
        (.addStoredMark tr (.create mark-type nil))))))

(def auto-join #(.autoJoin commands % (fn [] true)))

(def wrap-in-list (comp auto-join (fn [list-tag]
                                    (fn [state dispatch]
                                      (let [the-command (.wrapInList pm (get-node state list-tag))]
                                        (the-command state dispatch))))))
(defn set-block-type
  ([block-tag]
   (set-block-type block-tag nil))
  ([block-tag attrs]
   (fn [state dispatch]
     (let [the-command (.setBlockType commands (get-node state block-tag) attrs)]
       (the-command state dispatch)))))


(defn input-rule-wrap-inline
  ;; from textblockTypeInputRule
  [pattern node-tag attrs]
  (InputRule. pattern
              (fn [state match start end]
                (let [$start (.. state -doc (resolve start))
                      start-node (.node $start -1)
                      attrs (if (fn? attrs) (attrs match) attrs)
                      the-node (get-node state node-tag)]
                  (if-not (-> start-node
                              (.canReplaceWith (.index $start -1) (.indexAfter $start -1) the-node attrs))
                    nil
                    (-> (.-tr state)
                        (.delete start end)
                        (.setBlockType start start the-node attrs)))))))

(defn input-rule-wrap-block
  ;; from wrappingInputRule
  ([pattern node-tag attrs] (input-rule-wrap-block pattern node-tag attrs nil))
  ([pattern node-tag attrs join-predicate]
   (InputRule. pattern
               (fn [state match start end]
                 (let [the-node (get-node state node-tag)
                       attrs (if (fn? attrs) (attrs match) attrs)
                       tr (.delete (.-tr state) start end)
                       $start (.resolve (.-doc tr) start)
                       range (.blockRange $start)
                       wrapping (and range (.findWrapping pm range the-node attrs))]
                   (when wrapping
                     (.wrap tr range wrapping)
                     (let [before (-> tr
                                      (.-doc)
                                      (.resolve (dec start))
                                      (.-nodeBefore))]
                       (when (and before
                                  (= (.-type before) the-node)
                                  (.canJoin pm (.-doc tr) (dec start))
                                  (or (not join-predicate) (join-predicate match before)))
                         (.join tr (dec start)))
                       tr)))))))


(def lift (.-lift commands))

(defn lift-list-item [state dispatch]
  (let [the-command (.liftListItem pm (get-node state :list_item))]
    (the-command state dispatch)))

(defn sink-list-item [state dispatch]
  (let [the-command (.sinkListItem pm (get-node state :list_item))]
    (the-command state dispatch)))

(def keymap (.-keymap pm))
(def keymap-base (.-baseKeymap commands))


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

(defn has-mark? [^js/pm.EditorState pm-state mark-name]
  (let [^js/MarkType mark (get-mark pm-state mark-name)]
    (if-let [cursor (.. pm-state -selection -$cursor)]
      (.isInSet mark (or (.-storedMarks pm-state) (.marks cursor)))
      (every? true? (map (fn [^js/pm.SelectionRange range]
                           (.rangeHasMark (.-doc pm-state)
                                          (.. range -$from -pos)
                                          (.. range -$to -pos)
                                          mark))
                         (.. pm-state -selection -ranges))))))

(defn state [^js/pm.EditorView pm-view]
  (.-state pm-view))

(defn transact! [^js/pm.EditorView pm-view tr]
  (.updateState pm-view (.apply (.-state pm-view) tr)))

(defn destroy! [^js/pm.EditorView pm-view]
  (.destroy pm-view))

(defn is-list? [^js/pm.Node node]
  (string/ends-with? (aget node "type" "name") "list"))

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

(defn has-markup? [^js/pm.EditorState state node-type-name attrs]
  (let [^js/pm.Selection selection (.-selection state)
        ^js/pm.NodeType kind (get-node state node-type-name)]
    (if-let [^js/pm.Node node (.-node selection)]
      (.hasMarkup node kind attrs)
      (let [$from ^js/pm.ResolvedPos (.-$from selection)]
        (and (<= (.-to selection) (.end $from))
             (.hasMarkup (.-parent $from) kind attrs))))))

(defn wrap-in [state type-name]
  (.wrapIn commands (get-node state type-name)))

(defn ^js/pm.NodeType node-type [^js/pm.Node node]
  (aget node "type"))

(defn in-list? [^js/pm.EditorState state ^js/pm.NodeType list-type-name]
  (= list-type-name (some-> (first-ancestor (.. state -selection -$from) is-list?)
                            (aget "type" "name"))))

(def is-node-type?
  (fn [state node-tag]
    (= (.-type (cursor-node state)) (get-node state node-tag))))

(defn heading-level [state]
  (let [node (cursor-node state)]
    (when (= (.-type node) (get-node state :heading))
      (.-level (.-attrs node)))))

(def split-list-item
  (fn [state dispatch]
    ((.splitListItem pm (get-node state :list_item)) state dispatch)))

;; + or - the level
;; if para - only up
;; if heading - determine up or down


(defn start-$pos [state]
  (.-$head (.atStart Selection (.. state -doc))))

(defn end-$pos [state]
  (.-$head (.atEnd Selection (.. state -doc))))

(defn cursor-$pos [state]
  (.-$head (.-selection state)))