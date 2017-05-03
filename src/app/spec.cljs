(ns re-view-material.spec
  (:require [clojure.spec :as s :include-macros true]
            [re-view-hiccup.spec]
            [cljs.spec.impl.gen :as gen]
            [clojure.test.check.generators]))


(enable-console-print!)

#_(let [sample [:div {:style {:font-size 10}} "Wow" (list "You There")]
        conformed (s/conform ::element sample)]
    (println conformed)
    (println (s/unform ::element conformed)))

(s/def ::href string?)
(s/def ::on-click fn?)

(s/def ::label (s/or :primitive ::primitive
                     :element ::element))

(s/def ::icon ::hiccup)
(s/def ::icon-end ::hiccup)

(s/def ::disabled boolean?)
(s/def ::dense boolean?)
(s/def ::raised boolean?)
(s/def ::compact boolean?)
(s/def ::accent boolean?)
(s/def ::primary boolean?)
(s/def ::ripple boolean?)

(s/def ::id ::primitive)



