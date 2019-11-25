(ns cljc-macros.macros-test
  (:require #?(:cljs [cljs.test :as t :refer [deftest is]])
            [net.cgrand.macrovich :as mv]
            [clojure.pprint :refer [pprint]]
            [chivorcam.core :as ch]))


(ch/defmacro A [] :macro)
(ch/defmacro B [] :macro)


(defn A [] :function)
(defn B [] :function)

(println :USETIME)

(defonce x "")
#?(:cljs (deftest x

           ;; currently true
           (is (= (A) :macro))                              ;; A is explicitly referred in `ns`
           (is (= (B) :macro))                              ;; B macro is not visible
           (is (= (.call A) :function))
           (is (= (.call B) :function))

           ;; desired?
           ;(= (mt/A) (mt/B) :macro)
           ;; with :require-macros, could the :cljs namespace look up macros
           ;; with the same logic as other namespaces that consume this one?
           ))

