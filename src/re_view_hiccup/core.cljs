(ns re-view-hiccup.core
  (:require [clojure.string :as string]
            [re-view-hiccup.react-html :as react-html]))

(enable-console-print!)
(set! *warn-on-infer* true)


;; patch IPrintWithWriter to print javascript symbols without throwing errors
(when (exists? js/Symbol)
  (extend-protocol IPrintWithWriter
    js/Symbol
    (-pr-writer [sym writer _]
      (-write writer (str "\"" (.toString sym) "\"")))))

(defn parse-key
  "Parses a hiccup key like :div#id.class1.class2 to return the base name, id, and classes.
   If base-name is ommitted, defaults to 'div'. Class names are padded with spaces."
  [x]
  (-> (re-find #":([^#.]*)(?:#([^.]+))?(.*)?" (str x))
      (update 1 #(if (= "" %) "div" (string/replace % "/" ":")))
      (update 3 #(when %
                   (str (string/replace % "." " ") " ")))))

;; parse-key is an ideal target for memoization, because keyword forms are
;; frequently reused (eg. in lists) and almost never generated dynamically.
(def parse-key-memoized (memoize parse-key))

(defn reduce-concat
  "Recursively apply f to nested vectors. Lists are unwrapped, other forms left untouched. Similar to recursive `mapcat` but returns a vector."
  [f init coll]
  (reduce (fn my-f [c x]
            (cond (vector? x)
                  (conj c (f x))
                  (seq? x)
                  (reduce-concat f c x)
                  :else (conj c x))) init coll))

(defn parse-args
  "Return props and children for a hiccup form. If the second element is not a map, supplies an empty map as props."
  [form]
  (let [len (count form)]
    (cond (= len 1) [{} []]
          (map? (form 1)) [(form 1) (if (> len 2) (subvec form 2 len) [])]
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
  "Concatenate the various attributes which may contain classes
   for the element. "
  [^js/String k-classes ^js/String class classes]
  (cond-> k-classes
          class (str " " class)
          classes (str " " (string/join " " classes))))

(def ^:dynamic *wrap-props* nil)

(defn props->js
  "Returns a React-conformant javascript object. An alternative to clj->js,
  allowing for key renaming without an extra loop through every prop map."
  [k-id k-classes {:keys [class class-name classes] :as props}]
  (when props
    (let [prop-js (cond-> (js-obj)
                          k-id (doto (aset "id" k-id))
                          (or k-classes class class-name classes) (doto (aset "className" (concat-classes k-classes (or class class-name) classes))))]
      (doseq [[k v] (cond-> props (not (nil? *wrap-props*)) (*wrap-props*))]
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

(defn render-hiccup-node
  "Recursively create React elements from Hiccup vectors."
  [form]
  (try
    (let [[_ k id classes] (parse-key-memoized (form 0))
          [props children] (parse-args form)
          args (reduce-concat render-hiccup-node [k (props->js id classes props)] children)]
      (apply (.-createElement js/React) args))
    (catch js/Error e
      (println "Error in render-hiccup-node:")
      (println form)
      (.error js/console e))))


(defn element
  "Converts a `hiccup` vector into a React element. If a non-vector form
   is supplied, it is returned untouched. You may also pass an options map
   with `:wrap-props`, which will be applied to all props maps during parsing.
   Attribute and style keys are converted from `dashed-names` to `camelCase`
   where required by React."
  ([form]
   (cond-> form
           (vector? form) (render-hiccup-node)))
  ([form {:keys [wrap-props]}]
   (binding [*wrap-props* wrap-props]
     (element form))))

(comment
  (assert (= (-> (props->js "el" "pink" {:data-collapse true
                                         :aria-label    "hello"
                                         :class         "bg-black"
                                         :classes       ["white"]
                                         :style         {:font-family "serif"
                                                         :font-size   12}})
                 (js->clj :keywordize-keys true))
             {:data-collapse true
              :aria-label    "hello"
              :className     "pink bg-black white"
              :style         {:fontFamily "serif"
                              :fontSize   12}
              :id            "el"})))