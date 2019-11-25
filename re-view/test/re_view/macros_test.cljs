(ns re-view.macros-test
  (:require [re-view.hiccup :as hiccup]))

(defn two-arity
  ([one]
   (two-arity one 2))

  ([one two]
   (str one "-" two)))


(defn fn-that-returns-string
  []
  "Fn that returns string")
(comment

  (defn a-component
    []
    (<< [:span "Some react component"]))

  (defn ^string string-lookup
    [x]
    (:a-string x))


  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  (<< (let [foo 1] foo))

  (<< (string-lookup {:x "hello"}))

  (<< (pr-str []))

  (two-arity 1))













