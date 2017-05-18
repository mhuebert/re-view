(ns app.markdown
  (:require [cljsjs.markdown-it]
            [re-view.core :as v :refer [defview]]
            [goog.object :as gobj]
            [cljsjs.highlight]
            [cljsjs.highlight.langs.clojure]
            [cljsjs.highlight.langs.xml]
            [clojure.string :as string]))

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
                                       (+ out (aget heading-tokens i "content")))
                              (content-anchor))]
              (str (if heading-open
                     (.apply heading-open (js-this) (js-arguments))
                     (.apply (aget self "renderToken") self (js-arguments)))
                   "<a id=" anchor " class='heading-anchor' href=\"#" anchor "\"></a>"))))))


(def MD (let [MarkdownIt ((gobj/get js/window "markdownit") "default"
                           #js {"highlight" (fn [s lang]
                                              (try (-> (.highlight js/hljs "clojure" s)
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
  {:life/did-update scroll-to-anchor
   :life/did-mount scroll-to-anchor}
  [{:keys [view/props]} s]
  [:div (assoc props :dangerouslySetInnerHTML {:__html (.render MD s)})])