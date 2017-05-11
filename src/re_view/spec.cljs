(ns re-view.spec
  (:require [clojure.spec :as s :include-macros true]
            [cljs.spec.impl.gen :as gen]
            [cljs.spec.test :as t]
            [clojure.test.check.generators]
            [clojure.string :as string]
            [re-view.hiccup.spec]))


(s/fdef re-view.core/defview
        :args (s/cat :name symbol?
                     :docstring (s/? string?)
                     :methods (s/? (s/map-of keyword? any?))
                     :args vector?
                     :body (s/cat :side-effects (s/* any?)
                                  :render-body :re-view-hiccup.spec/element)))

