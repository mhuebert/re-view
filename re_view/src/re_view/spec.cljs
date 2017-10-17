(ns re-view.spec
  (:require [cljs.spec.alpha :as s :include-macros true]
            [cljs.spec.gen.alpha :as gen]
            [re-view.hiccup.spec]
            [re-view.core :include-macros true]))


(s/fdef re-view.core/defview
        :args (s/cat :name symbol?
                     :docstring (s/? string?)
                     :methods (s/? (s/map-of keyword? any?))
                     :args vector?
                     :body (s/cat :side-effects (s/* any?)
                                  :render-body :re-view.hiccup.spec/element)))

