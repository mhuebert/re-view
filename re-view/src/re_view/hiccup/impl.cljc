(ns re-view.hiccup.impl
  (:require [clojure.string :as str]
            [re-view.perf.util :as util]
            #?@(:cljs [[applied-science.js-interop :as j]
                       [re-view.perf.bench :as bench]
                       [re-view.perf.util :as perf]])
            [re-view.perf.destructure :as dest]))

(def ^:private tag-pattern #"([^#.]+)?(?:#([^.]+))?(?:\.(.*))?")

(defn parse-key*
  "Parses a hiccup key like :div#id.class1.class2 to return the tag name, id, and classes.
   If tag-name is ommitted, defaults to 'div'. Class names are padded with spaces."
  [s]
  #?(:cljs (dest/let [^array [_ tag id classes] (.exec tag-pattern s)]
             #js{:tag     (or tag "div")
                 :id      id
                 :classes (some-> classes (.replace "." " "))})
     :clj  (let [[_ tag id classes] (re-matches tag-pattern s)]
             {:tag     tag
              :id      id
              :classes (some-> classes (str/replace "." " "))})))

;; memoized at runtime
(def parse-key (util/cljs-> parse-key*
                            (perf/js-memo-1)))

(defn camel-case*
  "Converts strings from dash-cased to camelCase"
  [s]
  #?(:cljs (.replace s (js/RegExp. "-(.)" "g")
                     (fn [match group index] (.toUpperCase group)))
     :clj  (str/replace s #"-(.)"
                        (fn [[_ s]] (str/upper-case s)))))

;; memoized at runtime
(def camel-case (util/cljs-> camel-case*
                             (perf/js-memo-1)))

(defn react-attribute*
  "Return js (react) key for keyword/string.

  - Namespaced keywords are ignored
  - area- and data- prefixed keys are not camelCased
  - other keywords are camelCased"
  [s]
  (cond (identical? s "for") "htmlFor"
        (identical? s "class") "className"
        (or (str/starts-with? s "data-")
            (str/starts-with? s "aria-")) s
        :else (camel-case s)))

(def react-attribute (util/cljs-> react-attribute*
                                  (perf/js-memo-1)))

;; TODO
;; a "compiled props" object that can speedily merge other props onto itself