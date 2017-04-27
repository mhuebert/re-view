(ns re-view-example.helpers
  (:require [clojure.walk :as walk]))

(defmacro atom-as
  "Wraps expr in the body of an a new atom, which is bound to the name provided."
  [name expr]
  (let [atom-sym (gensym "atom-name")]
    `(let [~atom-sym (atom {})]
       (reset! ~atom-sym ~(walk/postwalk-replace {name atom-sym} expr))
       ~atom-sym)))