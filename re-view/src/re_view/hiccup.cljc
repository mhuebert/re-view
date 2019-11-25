(ns re-view.hiccup
  (:refer-clojure :exclude [compile])
  (:require [clojure.string :as str]
            [re-view.perf.util :as perf]
            [applied-science.js-interop :as j]
            [re-view.staged :refer [defstaged]]
            [re-view.perf.util :as perf]
            #?@(:clj [[net.cgrand.macrovich :as macros]
                      [re-view.inf :as inf]])
            #?@(:cljs [[re-view.perf.bench :as bench]])
            [clojure.set :as set])
  #?(:cljs (:require-macros [net.cgrand.macrovich :as macros])))

;; (def parse-key (util/cljs-> parse-key* (perf/js-memo-1)))

(defstaged dots->classes
  {:macrotime (constantly "compiled")
   :runtime (constantly "interpreted")}
  (fn [s] (perf/replace-pattern s "\\." " ")))

(comment
  (macroexpand
    '(defstaged dots->classes
       {:macrotime (constantly "compiled")
        :runtime (constantly "interpreted")}
       (fn [s] (perf/replace-pattern s "\\." " ")))))

(comment
  (apply dots->classes ["x.a"])
  (macroexpand '(dots->classes "x.a"))

  (let [a "x.a"] (dots->classes a))
  (macroexpand '(re-view.hiccup/dots->classes a)))
;(re-view.hiccup/dots->classes-impl a)

(comment
  (= (h/dots->classes "a.b") "a b")
  (macroexpand '(dots->classes "a.b")))

(def ^:private tag-pattern #"([^#.]+)?(?:#([^.]+))?(?:\.(.*))?")

(defstaged parse-key
  "Parses a hiccup key like :div#id.class1.class2 to return the tag name, id, and classes.
   If tag-name is ommitted, defaults to 'div'. Class names are padded with spaces."
  (fn [s]
    #?(:cljs
       (j/let [^js [_ tag id classes] (.exec tag-pattern (name s))]
         #js[(or tag "div") id (some-> classes dots->classes)])
       :clj
       (let [[_ tag id classes] (re-matches tag-pattern (name s))]
         [(or tag "div") id (some-> classes dots->classes)]))))

(comment
  (= (vec (parse-key :div#hello.a.b)) ["div" "hello" "a b"]))

(defstaged camel-case
  "Converts strings from dash-cased to camelCase"
  (fn [s]
    #?(:cljs
       (perf/replace-pattern s "-(.)" (fn [match group index]
                                        (str/upper-case group)))
       :clj
       (str/replace s #"-(.)" (fn [[_ group]]
                                (str/upper-case group))))))

(comment
  (= (camel-case "a-b-c") "aBC"))

(defstaged react-attribute
  "Return js (react) key for key string."
  (fn [s]
    (cond (identical? s "for") "htmlFor"
          (identical? s "class") "className"
          (or (str/starts-with? s "data-")
              (str/starts-with? s "aria-")) s
          :else (camel-case s))))

(comment
  (= (react-attribute "a-b") "aB"))


(defstaged class-str
  (fn class-str
    ([out s]
     (str out " " s))
    ([s]
     (cond (string? s) s
           (keyword? s) (name s)
           (vector? s) (reduce class-str s)
           :else s))))

(comment (= (class-str ["a" "b"]) "a b"))

(comment

  (macros/deftime
    (defmacro compile [x]
      (dots->classes x)))

  (comment

    (macroexpand '(compile "a.b"))
    (macroexpand '(compile a))

    (= (compile "a.b")
       (let [a "a.b"]
         (compile a))
       "a b"))

  (defn process-prop [js-props k v]
    #?(:cljs
       (let [^string js-key (react-attribute (name k))]
         (j/!set js-props
                 js-key (case js-key
                          ("style" "dangerouslySetInnerHTML") (perf/to-obj v (comp camel-case name) identity)
                          "className" (if-some [js-class (j/!get js-props :className)]
                                        (str js-class " " (class-str v))
                                        (class-str v))
                          v)))))

  (def ^:dynamic *wrap-props* nil)

  (defn merge-js-props [js-props clj-props]
    (reduce-kv
      (fn [js-props k v]
        (cond-> js-props (not (qualified-keyword? k))
                (process-prop k v)))
      js-props
      clj-props))

  (defn tag->js-props [keyname]
    #?(:cljs (j/let [^js [tag id tag-classes] (parse-key keyname)]
               (cond-> #js{:tag tag}
                       tag-classes (j/!set :className (dots->classes tag-classes))
                       id (j/!set :id id)))))



  #?(:cljs
     (j/defn props->js
       "Returns a React-conformant javascript object. An alternative to clj->js,
       allowing for key renaming without an extra loop through every prop map."
       ([props]
        (merge-js-props #js{} props))
       ([tag props]
        (merge-js-props (tag->js-props (name tag)) props))))

  (macros/deftime
    (defmacro << [form]
      form))

  #?(:clj (def EL 'element))
  #?(:clj (def obj 'applied-science.js-interop/obj))
  #?(:clj (declare compile*))

  (def interpret-props
    #?(:clj  're-view.hiccup/interpret-props
       :cljs (fn interpret-props [props] props)))

  #?(:clj
     (defn tag->props [tag]
       (let [[tag id classes] (parse-key (name tag))]
         (cond-> {:tag tag}
                 id (assoc :id id)
                 classes (assoc :className classes)))))

  '(defn process-prop [js-props k v]
     #?(:cljs
        (let [^string js-key (react-attribute (name k))]
          (j/!set js-props
                  js-key (case js-key
                           ("style" "dangerouslySetInnerHTML") (perf/to-obj v (comp camel-case name) identity)
                           "className" (if-some [js-class (j/!get js-props :className)]
                                         (str js-class " " (class-str v))
                                         (class-str v))
                           v)))))

  (defn unevaluated? [x] (and (list? x) (not= 'quote (first x))))

  ;; different scenarios
  ;;
  ;;

  (comment



    (m/defcompile add-props
                  :compile? (fn [])
                  :compile (fn [])
                  :defer-to-interpret (fn [])
                  :interpret (fn []))

    :compile-when map?
    :compile (fn [m] `(~obj ~@(mapcat identity m)))
    :defer-to-interpret (fn [m] `(~'app/interpret-here ~m))
    :interpret (fn [m] (into {} (map identity) m))

    )

  #?(:clj
     (defn add-props [p1 p2]
       (reduce-kv (fn [out k v]
                    (if (qualified-keyword? k)
                      out
                      (let [js-key (react-attribute (name k))]
                        (case js-key
                          ("style" "dangerouslySetInnerHTML") `(~obj ~@(mapcat (fn [k v] (camel-case (name k)) v) v))
                          "className" (if-some [prev-class (:className p1)]

                                        (str prev-class " " (class-str v))
                                        (class-str v))
                          v)))) p1 p2)))

  #?(:clj
     (defn compile-props [{:keys [tag props children]}]
       (let [children (when children
                        `(cljs.core/array ~@(map compile* children)))
             tag-props `(~obj ~@(mapcat (fn [[k v]] (when v [k v])) (tag->props tag)))]
         `(~EL ~(if (map? props)
                  ()))
         (if (map? props)
           ;; convert props via clj
           `()
           ))))

  #?(:clj
     (defn compile-vec [x]
       (let [[tag props & children] x
             primitive-element? (some-> tag inf/infer-tags inf/is-keyword?)]
         (if primitive-element?
           (let [props? (some-> props inf/infer-tags inf/is-map?)
                 children (if props? children (cons props children))]
             `(~EL ~(compile-props {:tag tag
                                    :props (when props? props)
                                    :children children})))
           `(~tag ~@(rest x))))))

  #?(:clj
     (defn compile* [form]
       (cond-> form
               (vector? form) (compile-vec))))

  #?(:clj
     (defmacro compile [form]
       (binding [inf/*&env* &env]
         (compile* form))))

  #?(:cljs
     (h/compile [:div
                 nil
                 {}
                 (array-map :a 1)
                 ]))

  (comment
    ;; cases

    ;; literal element with clj props
    [:div {:x 1}]

    ;; ... js props
    ;[:div #js{:x 1}]

    ;; ... dynamic prop value
    [:div {:style my-styles}]


    ;; function with props
    [my-component {}]

    (is-map? {})))