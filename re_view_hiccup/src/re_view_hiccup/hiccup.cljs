(ns re-view-hiccup.hiccup
  (:require [re-view-hiccup.react.attrs :as react-html]
            [react :as react]
            [clojure.string :as string]))


(defn parse-key
  "Parses a hiccup key like :div#id.class1.class2 to return the tag name, id, and classes.
   If tag-name is ommitted, defaults to 'div'. Class names are padded with spaces."
  [x]
  (-> (re-find #":([^#.]*)(?:#([^.]+))?(.*)?" (str x))
      (update 1 #(if (= "" %) "div" (string/replace % "/" ":")))
      (update 3 #(when %
                   (string/replace (subs % 1) "." " ")))))

;; parse-key is an ideal target for memoization, because keyword forms are
;; frequently reused (eg. in lists) and almost never generated dynamically.
(def parse-key-memoized (memoize parse-key))

(defn reduce-flatten-seqs
  "Recursively apply f to nested vectors, unwrapping seqs. Similar to recursive `mapcat` but returns a vector."
  [f init coll]
  (reduce (fn my-f [c x]
            (if (seq? x)
              (reduce-flatten-seqs f c x)
              (conj c (f x)))) init coll))

(defn parse-args
  "Return props and children for a hiccup form. If the second element is not a map, supplies an empty map as props."
  [form]
  (let [len (count form)]
    (cond (= len 1) [{} []]
          (let [first-child (form 1)]
            (or (nil? first-child)
                (instance? PersistentArrayMap first-child)
                (instance? PersistentHashMap first-child))) [(form 1) (if (> len 2) (subvec form 2 len) [])]
          :else [{} (subvec form 1 len)])))

(defn camelCase [s]
  (string/replace s #"-([a-z])" (fn [[_ s]] (string/upper-case s))))

(defn key->react-attr
  "CamelCase react keys, except for aria- and data- attributes"
  [k]
  (if (keyword-identical? k :for)
    "htmlFor"
    (let [k-str (name k)]
      (cond-> k-str
              (or (contains? react-html/attrs k)
                  (string/starts-with? k-str "on-"))
              (camelCase)))))

(defn map->js
  "Return javascript object with camelCase keys. Not recursive."
  [style]
  (let [style-js (js-obj)]
    (doseq [[k v] style]
      (aset style-js (camelCase (name k)) v))
    style-js))

(defn concat-classes
  "Build className from keyword classes, :class and :classes."
  [^js/String k-classes ^js/String class classes]
  (->> (cond-> []
               k-classes (conj k-classes)
               class (conj class)
               classes (into classes))
       (string/join " ")))

(def ^:dynamic *wrap-props* nil)

(defn props->js
  "Returns a React-conformant javascript object. An alternative to clj->js,
  allowing for key renaming without an extra loop through every prop map."
  [tag k-id k-classes props]
  (when (or props k-id k-classes)
    (let [{:keys [class class-name classes] :as props} (cond-> props
                                                               (boolean *wrap-props*)
                                                               (*wrap-props* tag))
          prop-js (cond-> (js-obj)
                          k-id (doto (aset "id" k-id))
                          (or k-classes class class-name classes) (doto (aset "className" (concat-classes k-classes (or class class-name) classes))))]
      (doseq [[k v] props]
        (cond
          ;; convert :style and :dangerouslySetInnerHTML to js objects
          (or (keyword-identical? k :style)
              (keyword-identical? k :dangerouslySetInnerHTML))
          (aset prop-js (name k) (map->js v))
          ;; ignore className-related keys
          (or (keyword-identical? k :classes)
              (keyword-identical? k :class)) nil
          ;; passthrough all other values
          :else (aset prop-js (key->react-attr k) v)))
      prop-js)))


(def ^:dynamic *create-element* react/createElement)