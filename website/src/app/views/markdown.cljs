(ns app.views.markdown
  (:require [re-view.core :as v :refer [defview]]
            [goog.object :as gobj]
            [clojure.string :as string]
            ["highlight.js/lib/highlight" :as hljs]
            ["highlight.js/lib/languages/clojure" :as highlight-clj]
            ["highlight.js/lib/languages/xml" :as highlight-xml]
            ["markdown-it" :as markdownit]))

(.registerLanguage hljs "clojure" highlight-clj)
(.registerLanguage hljs "xml" highlight-xml)

(defn content-anchor [s]
  (str "__" (-> s
                (string/lower-case)
                (string/replace #"(?i)[\W-]+" "-"))))

(defn heading-anchors [md]
  (let [heading-open (aget md "renderer" "rules" "heading_open")]
    (aset md "renderer" "rules" "heading_open"
          (fn [tokens idx x y self]
            (let [heading-tokens (aget tokens (inc idx) "children")
                  anchor (->> (areduce heading-tokens i out ""
                                       (str out (aget heading-tokens i "content")))
                              (content-anchor))]
              (str (if heading-open
                     (.apply heading-open (js-this) (js-arguments))
                     (.apply (aget self "renderToken") self (js-arguments)))
                   "<a id=" anchor " class='heading-anchor' href=\"#" anchor "\"></a>"))))))


(def MD (let [MarkdownIt (markdownit "default"
                           #js {"highlight" (fn [s lang]
                                              (try (-> (.highlight hljs "clojure" s)
                                                       (.-value))
                                                   (catch js/Error e "")))})]
          (doto MarkdownIt
            (.use heading-anchors))))

(defn scroll-to-anchor [{:keys [view/children view/prev-children] :as this}]
  (when (not= children prev-children)
    (when-let [hash (aget js/window "location" "hash")]
      (some-> (.getElementById js/document (subs hash 1))
              (.scrollIntoView)))))

(defview md
  {:view/did-update scroll-to-anchor
   :view/did-mount  scroll-to-anchor}
  [{:keys [view/props]} s]
  [:div (assoc props :dangerouslySetInnerHTML {:__html (.render MD s)})])