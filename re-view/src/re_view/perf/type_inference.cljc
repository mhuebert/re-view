(ns re-view.perf.type-inference
  (:require [cljs.analyzer :as ana]
            [cljs.env :as env]
            [clojure.string :as str]))

(defn- resolve-tag [tag]
  (or (#{'js} tag)
      (ana/resolve-symbol tag)))

(defn tag-def [tag]
  (when (symbol? tag)
    (let [tag (resolve-tag tag)]
      (get-in @env/*compiler* [:cljs.analyzer/namespaces (symbol (namespace tag)) :defs (symbol (name tag))]))))

(defn- record-basis
  [tag]
  (let [tag (resolve-tag tag)
        positional-factory (symbol (namespace tag) (str "->" (str/replace (name tag) #"^^" "")))]
    (set (-> (tag-def positional-factory)
             :method-params
             first))))

(defn record-field? [tag field-sym]
  (contains? (record-basis (resolve-tag tag)) field-sym))