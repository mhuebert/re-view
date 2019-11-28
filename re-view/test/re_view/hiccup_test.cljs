(ns re-view.hiccup-test
  (:require [cljs.test :as t :refer [is]]
            [re-view.hiccup :as h]))

(t/deftest hiccup-utils

  #_#_#_(is (= (h/dots->classes "a.b")
         (macroexpand '(h/dots->classes "a.b"))
         (let [x "a.b"] (h/dots->classes x))
         "a b"))

  (is (= (h/class-str ["a" "b"])
         (macroexpand '(h/class-str ["a" "b"]))
         (let [x "b"] (h/class-str ["a" x]))
         "a b"))

  (is (= (vec (h/parse-key :div#hello.a.b))
         ["div" "hello" "a b"]))


  (is (= (h/camel-case "a-b-c") "aBC"))
  #_(is (= (h/react-attribute "a-b") "aB")))















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













