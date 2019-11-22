(ns re-view.inf
  (:require [cljs.analyzer :as ana]
            [clojure.string :as str]
            [clojure.set :as set]))

(def ^:dynamic *&env*
  "Bind to &env value within a macro definition"
  nil)

(defn normalize-tag [tag]
  (if (set? tag)
    (let [tags (into #{} (keep normalize-tag) tag)]
      (if (<= 1 (count tags)) (first tags) tags))
    ({'Array 'array} tag tag)))

(defn as-set [x] (if (set? x) x #{x}))

(defn ignore-nil [tags]
  (-> (as-set tags)
      (disj 'clj-nil)
      (normalize-tag)))

(defn infer-tags
  "Infers type of expr"
  ([expr]
   (infer-tags *&env* expr))
  ([env expr]
   (->> (ana/analyze env expr)
        ana/no-warn
        (ana/infer-tag env)
        (normalize-tag))))

(defn is-map? [tags]
  (some-> (ignore-nil tags)
          (str/ends-with? "Map")))

(defn is-keyword? [tags]
  (some-> (ignore-nil tags)
          (str/ends-with? "Keyword")))

(defn is-object? [tags]
  (some-> (ignore-nil tags)
          (= 'object)))

(defn is-primitive? [tags]
  (set/superset? '#{string
                    number
                    clj-nil
                    js/undefined
                    react-element} (as-set tags)))

(defn is-sym? [tags]
  (some-> (ignore-nil tags)
          (str/ends-with? "Symbol")))