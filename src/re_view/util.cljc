(ns re-view.util
  (:require [clojure.string :as string]))

(defn camelCase
  "Return camelCased string, eg. hello-there to helloThere. Does not modify existing case."
  [s]
  (string/replace (name s) #"-(.)" (fn [[_ match]] (string/upper-case match))))