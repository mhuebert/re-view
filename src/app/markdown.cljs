(ns app.markdown
  (:require [cljsjs.markdown-it]
            [goog.object :as gobj]
            [cljsjs.highlight]
            [cljsjs.highlight.langs.clojure]
            [cljsjs.highlight.langs.xml]))

(def MD ((gobj/get js/window "markdownit") "default"
          #js {"highlight" (fn [s lang]
                             (try (-> (.highlight js/hljs "clojure" s)
                                      (.-value))
                                  (catch js/Error e "")))}))
(defn md [s]
  {:dangerouslySetInnerHTML {:__html (.render MD s)}})