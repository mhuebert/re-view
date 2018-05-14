(ns re-style.read
  (:require [re-style.utils :as utils]
            [clojure.string :as str]))

(defn- expand-class-kw
  "Expands namespaced keywords with .-separated names

  :a/b.c => [:a/b :a/c]"
  [k]
  (if-let [the-ns (namespace k)]
    (reduce (fn [out the-name]
              (conj out (keyword the-ns the-name))) []
            (-> (name k)
                (str/split #"\.")))
    [k]))

(defn- expand-class-expr
  "Expands a class expression, which may be a keyword or vector
   prefaced by a registered class-fn."
  [expr]
  (cond-> expr
          (keyword? expr) (expand-class-kw)))

(defn ensure-class-exists [k]
  ;; verify that it has been registered?
  k)

(defn expand-class-vec [coll]
  (->> (mapcat expand-class-expr coll)
       (mapv ensure-class-exists)))

(comment
 (= (expand-class-vec [:a/b.c])
    [:a/b
     :a/c
     :y
     :x
     :something/here
     :something/there])
 (= [:a/b :a/c] (expand-class-kw :a/b.c)))