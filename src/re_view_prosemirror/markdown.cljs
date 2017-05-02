(ns re-view-prosemirror.markdown
  (:require [re-view.core :as v]
            [pack.prosemirror-markdown]
            [re-view-prosemirror.core :as core]))

(def pmMarkdown (.-pmMarkdown js/window))

(def Editor (v/partial core/Editor {:serialize #(.serialize (.-defaultMarkdownSerializer pmMarkdown) %)
                                    :parse     #(.parse (.-defaultMarkdownParser pmMarkdown) %)
                                    :schema    (.-schema pmMarkdown)}))