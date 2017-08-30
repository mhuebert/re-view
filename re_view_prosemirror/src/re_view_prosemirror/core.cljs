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
(def Slice (gobj/get pm "Slice"))

(def Schema (gobj/get pm "Schema"))

(def history (gobj/get pm "history"))


(def ^js/pm.EditorView EditorView (gobj/get pm "EditorView"))
(def ^js/pm.EditorState EditorState (gobj/get js/pm "EditorState"))

(def chain (gobj/get commands "chainCommands"))

(defn ^js/pm.Schema ensure-schema [state-or-schema]
  (cond-> state-or-schema
          (= (gobj/get state-or-schema "constructor") EditorState) (gobj/get "schema")))

(defn ^js/pm.MarkType get-mark [state-or-schema mark-name]
  (if (keyword? mark-name)
    (gobj/getValueByKeys (ensure-schema state-or-schema) "marks" (name mark-name))
    mark-name))

(defn ^js/pm.NodeType get-node [state-or-schema node-name]
  (aget (ensure-schema state-or-schema) "nodes" (name node-name)))

(defn ^js/pm.Transaction scroll-into-view [^js/pm.Transaction tr]
  (prn :scroll-into-view)
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

(defn pos-mark [state $pos mark]
  (let [^js/MarkType mark (get-mark state mark)
        stored-marks (.-storedMarks state)
        cursor-marks (some-> $pos (.marks))]
    (first (filter #(= mark (.-type %))
                   (cond-> #js []
                           stored-marks (.concat stored-marks)
                           cursor-marks (.concat cursor-marks)))))
  )

(defn has-mark? [^js/pm.EditorState pm-state mark]
  (let [^js/MarkType mark (get-mark pm-state mark)]
    (if-let [cursor (.. pm-state -selection -$cursor)]
      (.isInSet mark (or (.-storedMarks pm-state) (.marks cursor)))
      (every? true? (mapv (fn [^js/pm.SelectionRange range]
                            (.rangeHasMark (.-doc pm-state)
                                           (.. range -$from -pos)
                                           (.. range -$to -pos)
                                           mark))
                          (.. pm-state -selection -ranges))))))

(defn toggle-mark-tr
  ([state mark-name] (toggle-mark-tr state mark-name nil))
  ([state mark-name attrs]
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
         (.addStoredMark tr (.create mark-type attrs)))))))


(defn add-link-tr
  [state from to label href]
  (let [tr (.-tr state)
        link (get-mark state :link)
        from from]
    (-> tr
        (.insertText label from to)
        (.addMark from (+ from (count label)) (.create link #js {:href href}))
        (.removeStoredMark link))))

(defn add-image-tr
  [state from to label href]
  (let [tr (.-tr state)
        image (get-node state :image)]
    (-> tr
        (.setSelection (.create TextSelection (.-doc state) from to))
        (.replaceSelectionWith (.createAndFill image #js {:src   href
                                                          :title label
                                                          :alt   label})))))

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




;function markApplies(doc, ranges, type) {
;  for (let i = 0; i < ranges.length; i++) {
;    let {$from, $to} = ranges[i]
;    // at depth zero? then can = doc.contentMatch.
;    //
;    let can = $from.depth == 0 ? doc.contentMatchAt(0).allowsMark(type) : false
;    doc.nodesBetween($from.pos, $to.pos, node => {
;      if (can) return false
;      can = node.inlineContent && node.contentMatchAt(0).allowsMark(type)
;    })
;    if (can) return true
;  }
;  return false
;}

#_(defn mark-applies? [doc ranges type]
    (let [node-matches #(and (.-inlineContent %)
                             (-> % (.contentMatchAt 0) (.allowsMark type)))]
      (every? identity (for [range ranges
                             :let [$from (.-$from range)
                                   root-can? (and (= 0 (.-depth $from))
                                                  (node-matches doc))
                                   nodes (range-nodes doc (.-pos $from) (.-pos (.-$to range)))]]
                         (or (and root-can? (empty? nodes))
                             (every? identity (for [node nodes]
                                                (and (not root-can?)
                                                     (node-matches node)))))))))
#_(defn toggle-mark-tr
    ([state mark-name]
     (toggle-mark-tr state mark-name nil))
    ([state mark-name attrs]
     (let [sel (.-selection state)
           ranges (.-ranges sel)
           doc (.-doc state)
           empty (.-empty sel)
           $cursor (.-$cursor sel)
           mark-type (get-mark state mark-name)
           tr (.-tr state)
           invalid-selection (or (and empty (not $cursor))
                                 (not (mark-applies? doc ranges mark-type)))]
       (when-not invalid-selection
         (let [has-mark (has-mark? state mark-name)]
           (if $cursor
             (if has-mark (.removeStoredMark tr mark-type)
                          (.addStoredMark tr (.create mark-type attrs)))
             (reduce (fn [tr range]
                       (if has-mark
                         (.removeMark tr
                                      (.. range -$from -pos)
                                      (.. range -$to -pos)
                                      mark-type)
                         (.addMark tr
                                   (.. range -$from -pos)
                                   (.. range -$to -pos)
                                   (.create mark-type attrs)))) tr ranges)))))))

(defn toggle-ranges-mark
  ([state ranges mark] (toggle-ranges-mark state ranges mark nil))
  ([state ranges mark attrs]
   (let [mark (get-mark state mark)
         has-mark (has-mark? state mark)]
     (reduce (fn [tr range]
               (if has-mark
                 (.removeMark tr
                              (.. range -$from -pos)
                              (.. range -$to -pos)
                              mark)
                 (.addMark tr
                           (.. range -$from -pos)
                           (.. range -$to -pos)
                           (.create mark attrs)))) (.-tr state) ranges))))

(defn mark-extend [state $pos mark]
  (let [mark (get-mark state mark)
        parent (.-parent $pos)
        start-index (loop [start-index (.index $pos)]
                      (if (or (<= start-index 0)
                              (not (.isInSet mark (.. $pos -parent (child (dec start-index)) -marks))))
                        start-index
                        (recur (dec start-index))))
        end-index (loop [end-index (.indexAfter $pos)]
                    (if (or (>= end-index (.. $pos -parent -childCount))
                            (not (.isInSet mark (.. $pos -parent (child end-index) -marks))))
                      end-index
                      (recur (inc end-index))))
        [start-pos end-pos] (loop [start-pos (.start $pos)
                                   end-pos start-pos
                                   i 0]
                              (if (>= i end-index)
                                [start-pos end-pos]
                                (let [size (.. parent (child i) -nodeSize)]
                                  (if (< i start-index)
                                    (recur (+ start-pos size) (+ end-pos size) (inc i))
                                    (recur start-pos (+ end-pos size) (inc i))))))]
    {:from start-pos
     :to   end-pos}))

(defn cursor-coords [pm-view]
  (when-let [coords (some->> (.. pm-view -state -selection -$cursor)
                             (.-pos)
                             (.coordsAtPos pm-view))]
    #js {:left (.-left coords)
         :top  (-> (.-top coords)
                   (+ (/ (- (.-bottom coords)
                            (.-top coords))
                         2)))}))

(defn coords-selection [pm-view position]
  (some->> (.posAtCoords pm-view position)
           (.-pos)
           (.resolve (.. pm-view -state -doc))
           (.near Selection)))