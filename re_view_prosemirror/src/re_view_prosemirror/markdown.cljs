(ns re-view-prosemirror.markdown
  (:require [re-view.core :as v]
            [cljsjs.markdown-it]
            [pack.prosemirror-markdown]
            [re-view-prosemirror.base :as base]
            [re-view-prosemirror.tables :as tables]
            [re-view-prosemirror.core :as pm]
            [goog.object :as gobj]))

(def *tables?* true)
(def *fenced-code-blocks?* true)

(def pmMarkdown (.-pmMarkdown js/window))
(def markdown-schema (gobj/get pmMarkdown "schema"))
(def defaultMarkdownSerializer (gobj/get pmMarkdown "defaultMarkdownSerializer"))
(def defaultMarkdownParser (gobj/get pmMarkdown "defaultMarkdownParser"))
(def MarkdownSerializerState (gobj/get pmMarkdown "MarkdownSerializerState"))

(def default-serializer-nodes (gobj/get defaultMarkdownSerializer "nodes"))
(def default-serializer-marks (gobj/get defaultMarkdownSerializer "marks"))

(defn patch-state
  "Patch markdown serializer state to emit tight lists."
  [st]
  (let [render-list (.-renderList st)]
    (aset st "renderList" (fn [node delim first-delim]
                            (aset node "attrs" #js {:tight true})
                            (this-as this
                              (.apply render-list this (js-arguments)))))
    st))

(defn MarkdownSerializer [nodes marks]
  #js {:serialize (fn [content]
                    (let [state (patch-state (MarkdownSerializerState.
                                               (doto default-serializer-nodes (gobj/extend (clj->js (or nodes #js {}))))
                                               (doto default-serializer-marks (gobj/extend (clj->js (or marks #js {})))) nil))]
                      (.renderContent state content)
                      (.-out state)))})

(def fenced-code-nodes {:code_block  (fn [^js/pmMarkdown.MarkdownSerializerState state node]
                                       (.write state (str "```" (.-params (.-attrs node)) "\n"))
                                       (.text state (.-textContent node) false)
                                       (.ensureNewLine state)
                                       (.write state "```")
                                       (.closeBlock state node))
                        :bullet_list (fn [^js/pmMarkdown.MarkdownSerializerState state node]
                                       (.renderList state node "    " (fn []
                                                                        (str (or (.. node -attrs -bullet) "*") " "))))})

(def schema (cond-> markdown-schema
                    *tables?* (tables/add-schema-nodes)))

(def serializer (MarkdownSerializer (merge {}
                                           (when *tables?*
                                             tables/table-nodes)
                                           (when *fenced-code-blocks?*
                                             fenced-code-nodes)) nil))

(def parser (cond-> defaultMarkdownParser
                    *tables?* (tables/add-parser-nodes schema (gobj/get pmMarkdown "MarkdownParser"))))

(def Editor (v/partial base/Editor {:serialize #(.serialize serializer %)
                                    :parse     #(.parse parser %)
                                    :schema    schema}))