(ns app.markdown
  (:require [cljsjs.markdown-it]
            [goog.object :as gobj]))

(def MD ((gobj/get js/window "markdownit") "default"))
(defn md [s]
  {:dangerouslySetInnerHTML {:__html (.render MD s)}})