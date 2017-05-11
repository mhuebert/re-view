(ns re-view.prosemirror.markdown
  (:require [re-view.core :as v]
            [goog.object :as gobj]
            [pack.prosemirror-markdown]
            [re-view.prosemirror.base :as base]
            [clojure.string :as string]))

(def pmMarkdown (.-pmMarkdown js/window))
(def Schema (aget js/pm "Schema"))
(def schema (let [s (gobj/get pmMarkdown "schema")
                  add-table-nodes (aget js/pm "table" "addTableNodes")]
              (Schema. #js {"nodes" (-> (aget s "spec" "nodes")
                                        (.append (clj->js {:table        {:toDOM   (fn [] #js ["table" 0])
                                                                          :content "table_header table_body"
                                                                          :group   "block"}
                                                           :table_header {:toDOM   (fn [] #js ["thead" 0])
                                                                          :content "table_row"}
                                                           :table_body   {:toDOM   (fn [] #js ["tbody" 0])
                                                                          :content "table_row+"}
                                                           :table_row    {:content  "table_cell+"
                                                                          :toDOM    (fn [] #js ["tr" 0])
                                                                          :tableRow true}
                                                           :table_cell   {:attrs     {:cellType {:default "td"}}
                                                                          :toDOM     (fn [x] #js [(aget x "attrs" "cellType") 0])
                                                                          :content   "inline<_>*"
                                                                          :isolating true}})))
                            "marks" (aget s "spec" "marks")})))


(def serializer
  (let [MarkdownSerializer (.-MarkdownSerializer pmMarkdown)
        nodes (.. pmMarkdown -defaultMarkdownSerializer -nodes)
        marks (.. pmMarkdown -defaultMarkdownSerializer -marks)]
    (MarkdownSerializer. (doto nodes
                           (gobj/extend
                             (clj->js {:table             (fn [state node]
                                                            (.renderContent state node)
                                                            (.write state "\n"))
                                       :table_body        (fn [state node]
                                                            (.renderContent state node))
                                       :table_header      (fn [state node]
                                                            (.renderContent state node)
                                                            (let [columns (.. node -firstChild -content -childCount)]
                                                              (.write state
                                                                      (str "|" (string/join
                                                                                 (take columns (repeat "---|")))
                                                                           "\n"))))
                                       :table_header_cell (fn [state node]
                                                            (.write state "| ")
                                                            (.renderInline state node)
                                                            (.write state " "))
                                       :table_row         (fn [state node]
                                                            (.renderContent state node)
                                                            (.write state "|\n"))
                                       :table_cell        (fn [state node]
                                                            (.write state "| ")
                                                            (.renderInline state node)
                                                            (.write state " "))}))) marks)))

(def parser
  (let [MarkdownParser (.-MarkdownParser pmMarkdown)]
    (MarkdownParser.
      schema (js/markdownit "default" #js {"html" false})
      (-> (.-defaultMarkdownParser pmMarkdown)
          (.-tokens)
          (doto (gobj/extend (clj->js {:table {:block "table"}
                                       :thead {:block "table_header"}
                                       :tbody {:block "table_body"}
                                       :tr    {:block "table_row"}
                                       :th    {:block "table_cell"
                                               :attrs {:cellType "th"}}
                                       :td    {:block "table_cell"}})))))))

(def Editor (v/partial base/Editor {:serialize #(.serialize serializer %)
                                    :parse     #(.parse parser %)
                                    :schema    schema}))