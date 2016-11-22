(ns re-view.shared
  (:refer-clojure :exclude [partial]))

(def ^:dynamic *lookup-log*)

(defn partial
  "Partially apply props to a component"
  [component partial-props]
  (fn [& args]
    (let [[props & children] (cond->> args
                                      (not (map? (first args))) (cons {}))]
      (apply component (cons (merge partial-props props) children)))))