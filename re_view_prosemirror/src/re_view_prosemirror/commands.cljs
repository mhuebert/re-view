(ns re-view-prosemirror.commands
  (:require [re-view-prosemirror.core :as pm]
            [goog.object :as gobj]))

(def chain pm/chain)

(def undo (gobj/get pm/history "undo"))

(def redo (gobj/get pm/history "redo"))

(def inline-bold (pm/toggle-mark :strong))

(def inline-italic (pm/toggle-mark :em))

(def inline-code (pm/toggle-mark :code))

(def block-list-bullet (pm/wrap-in-list :bullet_list))

(def block-list-ordered (pm/wrap-in-list :ordered_list))

(def block-paragraph (pm/set-block-type :paragraph))

(defn block-heading [i]
  (pm/set-block-type :heading #js {"level" i}))

(def outdent (pm/chain
               pm/lift-list-item
               pm/lift))

(def indent (chain pm/sink-list-item block-list-bullet))

(def hard-break
  (pm/chain
    (.-exitCode pm/commands)
    (fn [^js/pm.EditorState state dispatch]
      (dispatch (->> (.create (pm/get-node state :hard_break))
                     (.replaceSelectionWith (.-tr state))
                     (pm/scroll-into-view)))
      true)))

(def newline-in-code (.-newlineInCode pm/commands))

(defn empty-node? [node]
  (= 0 (.-size (.-content node))))

(defn delete-cursor-node [state dispatch]
  (let [pos (.. state -selection -$anchor -pos)]
    (dispatch (.deleteRange (.-tr state) (max 0 (dec pos)) (inc pos)))))

(def enter (pm/chain pm/split-list-item
                     (.-createParagraphNear pm/commands)
                     (.-liftEmptyBlock pm/commands)
                     (.-splitBlock pm/commands)))

(defn clear-empty-non-paragraph-nodes
  "If the cursor is in an empty node which is a heading or code-block, convert the node to a paragraph."
  [state dispatch]
  (let [node (pm/cursor-node state)
        $cursor (.-$anchor (.-selection state))]
    (when (and (#{(pm/get-node state :heading)
                  (pm/get-node state :code_block)} (.-type node))
               (or (= 0 (.-size (.-content node)))
                   (= 0 (some-> $cursor (.-parentOffset)))))
      ((pm/set-block-type :paragraph) state dispatch))))



(def backspace (pm/chain (.-deleteSelection pm/commands)
                         clear-empty-non-paragraph-nodes
                         (.-joinBackward pm/commands)
                         (.-undoInputRule pm/pm)))


(def join-up (.-joinUp pm/commands))

(def join-down (.-joinDown pm/commands))

(def select-parent-node (.-selectParentNode pm/commands))

(def select-all (.-selectAll pm/commands))

(def selection-stack (atom '()))

(defn clear-selections! []
  (reset! selection-stack '()))

(defn stack-selection! [n]
  (when (not= n (first @selection-stack))
    (swap! selection-stack conj n)))

(defn read-selection! []
  (let [n (second @selection-stack)]
    (swap! selection-stack rest)
    n))

(defn select-word [state dispatch]
  ;; TODO
  ;; implement `select-word` as the first step of `expand-selection`
  ;; also: select word by default on M1, to match behaviour of code
  )

(defn expand-selection
  "Expand selection upwards, by block."
  [state dispatch]
  (let [original-selection (.-selection state)
        had-selected-node? (and (not= (.-from original-selection)
                                      (.-to original-selection))
                                (let [node-selection (.create pm/NodeSelection (.-doc state) (.-from original-selection))]
                                  (and (= (.-from original-selection)
                                          (.-from node-selection))
                                       (= (.-to original-selection)
                                          (.-to node-selection)))))]
    (when (= (.-from original-selection) (.-to original-selection))
      (clear-selections!))
    (loop [sel original-selection]
      (let [$from (.-$from sel)
            to (.-to sel)
            same (.sharedDepth $from to)]
        (if (= same 0)
          (do
            (stack-selection! 0)
            (select-all state dispatch))
          (let [pos (.before $from same)
                $pos (.resolve (.-doc state) pos)
                the-node (.-nodeAfter $pos)
                node-selection (pm/NodeSelection. $pos)]
            (if (and (= 1 (.-childCount the-node))
                     had-selected-node?)
              (recur node-selection)
              (when dispatch
                (stack-selection! pos)
                (dispatch (.setSelection (.-tr state) node-selection))))))))))

(defn shrink-selection [state dispatch]
  (when dispatch
    (let [sel (.-selection state)]
      (if (= (.-from sel) (.-to sel))
        (clear-selections!)
        (dispatch (.setSelection (.-tr state) (if-let [pos (read-selection!)]
                                                (pm/NodeSelection. (.resolve (.-doc state) pos))
                                                (.near pm/Selection (.-$anchor sel)))))))))

(defn heading->paragraph [state dispatch]
  (when (pm/is-node-type? state :heading)
    ((pm/set-block-type :paragraph) state dispatch)))

(defn adjust-font-size [f state dispatch]
  (let [node (pm/cursor-node state)]
    (when-let [heading-level (condp = (.-type node)
                               (pm/get-node state :paragraph) 7
                               (pm/get-node state :heading) (.-level (.-attrs node))
                               :else nil)]
      (let [target-index (min (f heading-level) 6)]
        (when-not (> target-index 7)
          ((if (= 7 target-index)
             (pm/set-block-type :paragraph)
             (pm/set-block-type :heading #js {:level target-index})) state dispatch))))))

;;;;;; Input rules

(def rule-blockquote-start
  (pm/input-rule-wrap-block
    #"^>\s"
    :blockquote
    nil))

(def rule-toggle-code
  (pm/InputRule. #"[^`\\]+`$"
                 (fn [state & _]
                   (pm/toggle-mark-tr state :code))))

(def rule-toggle-italic
  (pm/InputRule. #"[^`\\]+\* $"
                 (fn [state & _]
                   (pm/toggle-mark-tr state :code))))

(def rule-block-list-bullet-start
  (pm/input-rule-wrap-block
    #"^\s*([-+*])\s$"
    :bullet_list
    nil))

(def rule-block-list-numbered-start
  (pm/input-rule-wrap-block
    #"^(\d+)\.\s$"
    :ordered_list
    (fn [match] #js {"order" (second match)})))

(def rule-block-code-start
  (pm/input-rule-wrap-inline
    #"^```$"
    :code_block
    nil))

(def rule-paragraph-start
  (pm/input-rule-wrap-inline
    #"^/p$"
    :paragraph
    nil))

(comment
  ;; need to implement a transform that adds an hr element
  ;; also need to handle selection/delete of hr
  (def hr
    (pm/InputRule. #"—-" "---")))

(def rule-block-heading-start
  (pm/input-rule-wrap-inline
    #"^(#{1,6})\s$"
    :heading
    (fn [match]
      #js {"level" (count (second match))})))

;; TODO
;; command to increase/decrease header level
#_(defn size-change
    [mode]
    (fn [state dispatch]
      (let [set-p (pm/set-block-type :paragraph)
            set-h1 (pm/set-block-type :heading #js {"level" 1})
            active? (or (set-p state) (set-h1 state))]
        )))


(defn apply-command [prosemirror command]
  (command (.-state prosemirror) (.-dispatch prosemirror) prosemirror))