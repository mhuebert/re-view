(ns re-view-hiccup.spec
  (:require [clojure.spec :as s :include-macros true]
            [cljs.spec.impl.gen :as gen]
            [cljs.spec.test :as t]
            [clojure.test.check.generators]
            [clojure.string :as string]
            [re-view-hiccup.core :as hiccup]
            [re-view-hiccup.react.attrs :as attrs]))


(defn gen-wrap
  "Wraps return values of generator of expr with f"
  [expr f]
  (s/with-gen expr #(gen/fmap f (s/gen expr))))

(defn gen-set
  "With generator from set"
  [expr set]
  (s/with-gen expr #(s/gen set)))

(s/def ::primitive (s/or :string string?
                         :number number?
                         :nil nil?
                         :boolean boolean?))

(s/def ::fn (-> fn?
                (gen-set #{identity})))

(s/def ::tag (-> keyword?
                 (gen-set #{:div :span :a})))

(s/def ::not-camelCase #(not (re-find #"[a-z][A-Z][a-z]" (str %))))

(s/def ::style-key (-> (s/and keyword?
                              ::not-camelCase)
                       (gen-set #{:font-size :color :background-color})))

(s/def ::style-map (s/or :map (s/map-of ::style-key ::primitive :gen-max 5 :conform-keys true)))

(s/def ::prop-key (-> keyword?
                      (gen-set #{:class :width :height})))

(s/def ::listener-key (-> (s/and keyword?
                                 #(string/starts-with? (name %) "on-"))
                          (gen-set #{:on-click})))

(s/def ::prop-map (s/every (s/or :nil (s/tuple keyword? nil?)
                                 :style-prop (s/tuple #{:style} ::style-map)
                                 :classes (s/tuple #{:classes} (s/or :vec vector?
                                                                     :set set?
                                                                     :seq seq?
                                                                     :list list?))
                                 :ref (s/tuple #{:ref} fn?)
                                 :key (s/tuple #{:key} (s/or :string string?
                                                             :number number?))
                                 :dangerous-html (s/tuple #{:dangerouslySetInnerHTML} (s/map-of #{:__html} string?))
                                 :listener (s/tuple ::listener-key ::fn)
                                 :prop (s/tuple ::prop-key ::primitive))
                           :kind map?
                           :into {}
                           :gen-max 10))

(defn is-react-element? [x]
  (and (object? x)
       (or (boolean (aget x "re$view"))
           (js/React.isValidElement x))))

(s/def ::native-element (-> is-react-element?
                            (gen-set #{#js {"re$view" #js {}}})))

(s/def ::element (s/or :element ::native-element
                       :hiccup ::hiccup-element
                       :primitive ::primitive
                       :element-list (s/coll-of ::element :into () :gen-max 5)))

(s/def ::hiccup-element
  (-> (s/and vector?
             (s/cat :tag ::tag
                    :props (s/? ::prop-map)
                    :body (s/* ::element)))
      (gen-wrap vec)))


(s/fdef hiccup/element
        :args (s/cat :body ::element :opts (s/? (s/keys :opt-un [::fn])))
        :ret (s/or :element ::native-element
                   :primitive ::primitive))

#_(doall (->>
           ;(s/exercise ::element 6)
           (s/exercise-fn re-view-hiccup.core/element)
           (map prn)))


