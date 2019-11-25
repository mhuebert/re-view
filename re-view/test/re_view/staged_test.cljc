(ns re-view.staged-test
  (:require [re-view.staged :refer [defstaged]]
            [re-view.macros :as m]
            [re-view.hiccup :as hiccup]
            #?(:cljs [cljs.test :refer [deftest is testing]]))
  #?(:cljs (:require-macros re-view.staged-test)))

;; simplest usage - defines a macro called `upper-case` with
;; `clojure.string/upper-case` as its implementation function,
;; which will run at compile-time if the input is static.
(defstaged upper-case
  clojure.string/upper-case)

#?(:cljs
   (deftest -upper-case
     (is (= "A"
            (macroexpand '(upper-case "a"))))

     (is (= '(re-view.staged-test/upper-case-impl x)
            (macroexpand '(upper-case x))))))


;; more complicated example
(defstaged to-string
  {:skip-tags '#{string}}                                   ; skips runtime interpretation for these inferred tags
  (fn [x]
    ;; implementation can use reader conditionals to target both JVM and CLJS
    ;; so that it works in self-host environment
    #?(:cljs (.toString x)
       :clj  (str x))))

(m/defmacro consuming-macro [x]
  ;; a helper function is also defined that can be used from other macros
  (to-string-fn x))

#?(:cljs
   (deftest -staged
     (is (= (macroexpand '(to-string 1))
            (macroexpand '(consuming-macro 1))
            (to-string 1)
            (consuming-macro 1)
            (let [x 1] (to-string x))
            "1")
         "static value is precomputed")

     (is (= (macroexpand '(to-string x))
            (macroexpand '(consuming-macro x))
            '(re-view.staged-test/to-string-impl x))
         "dynamic value is interpreted")

     (is (and (= (let [x 1] (to-string ^string x)) 1)
              (= (macroexpand-1 '(to-string ^string x)) 'x))
         "dynamic value with type hint matching :skip-tags is not interpreted")

     (is (= "a b c" (hiccup/class-str [\a \b \c])))
     (is (let [x "c"] (= "a b c" (hiccup/class-str [\a \b x]))))



     (is (= (apply hiccup/dots->classes ["x.a"])
            (macroexpand '(hiccup/dots->classes "x.a"))
            (let [a "x.a"] (hiccup/dots->classes a))
            "x a"))


     (is (= (macroexpand '(re-view.hiccup/dots->classes a))
            `(hiccup/dots->classes-impl ~'a)))))

