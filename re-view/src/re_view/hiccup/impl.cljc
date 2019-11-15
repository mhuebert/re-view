(ns re-view.hiccup.impl
  (:require [clojure.string :as str]
            [re-view.perf.util :as util]
            #?@(:cljs [[applied-science.js-interop :as j]
                       [re-view.perf.bench :as bench]
                       [re-view.perf.util :as perf]
                       [applied-science.js-interop.destructure :as jd]])))

(def ^:private tag-pattern #"([^#.]+)?(?:#([^.]+))?(?:\.(.*))?")

(defn parse-key*
  "Parses a hiccup key like :div#id.class1.class2 to return the tag name, id, and classes.
   If tag-name is ommitted, defaults to 'div'. Class names are padded with spaces."
  [s]
  #?(:cljs (jd/let [^js [_ tag id classes] (.exec tag-pattern s)]
             #js[(or tag "div") id classes])
     :clj  (let [[_ tag id classes] (re-matches tag-pattern s)]
             [(or tag "div") id classes])))

(def parse-key (util/cljs-> parse-key* (perf/js-memo-1)))

(defn camel-case*
  "Converts strings from dash-cased to camelCase"
  [s]
  #?(:cljs (.replace s (js/RegExp. "-(.)" "g") (fn [match group index] (str/upper-case group)))
     :clj  (str/replace s #"-(.)" (fn [[_ s]] (str/upper-case s)))))

(def camel-case (util/cljs-> camel-case* (perf/js-memo-1)))

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

(def react-attribute (util/cljs-> react-attribute* (perf/js-memo-1)))

(defn dots->classes* [s]
  (perf/replace-pattern s "\\." " "))

(def dots->classes (util/cljs-> dots->classes* (perf/js-memo-1)))

(defn class-str [s]
  (cond (string? s) s
        (keyword? s) (name s)
        (vector? s) (str/join " " (mapv class-str s))
        :else s))

(def ^:dynamic *wrap-props* nil)

(defn- merge-classes [tag-classes clj-classes]
  (let [n (cond-> 0 (perf/defined? tag-classes) inc (some? clj-classes) inc)]
    (case n 0 nil
            1 (dots->classes (or tag-classes (class-str clj-classes)))
            2 (dots->classes (str tag-classes " " (class-str clj-classes))))))

(jd/defn props->js
  "Returns a React-conformant javascript object. An alternative to clj->js,
  allowing for key renaming without an extra loop through every prop map."
  ([props] (props->js nil props))
  ([^js [tag id tag-classes :as parsed-tag] props]
   (if (and (undefined? id) (undefined? tag-classes) (nil? props))
     #js{:tag tag}
     (let [clj-props (cond-> props
                             (some? *wrap-props*) (*wrap-props* parsed-tag))
           clj-classes (:class clj-props)
           classes (merge-classes tag-classes clj-classes)
           js-props #js{:id id
                        :tag tag
                        :class classes}]
       (reduce-kv
         (fn [js-props k v]
           (if (qualified-keyword? k)                       ;; pass-through qualified keywords
             js-props
             (let [^string js-key (react-attribute (name k))]
               (j/!set js-props
                       js-key
                       (case js-key
                         ("style"
                           "dangerouslySetInnerHTML") (perf/to-obj v (comp camel-case name) identity)
                         v))
               js-props)))
         js-props
         (cond-> clj-props (some? clj-classes) (dissoc :class)))))))
