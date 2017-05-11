(ns re-view.hiccup.react.html
  (:refer-clojure :exclude [string])
  (:require [goog.dom :as gdom]
            [cljsjs.react]
            [cljsjs.react.dom]))

(defn- react-string
  "Returns string for React element, preserving React identifiers and comments."
  [element]
  (let [el (gdom/createDom "div")]
    (.render js/ReactDOM element el)
    (aget el "innerHTML")))

(defn string
  "Returns plain HTML string for React element. Use for client-side rendering of static markup.
  A little slower than ReactDOMServer."
  [element]
  (clojure.string/replace (react-string element) #"(?: data-react[^=]*?=\"[^\"]*?\")|(?:<!-- /?react.*?-->)" ""))

