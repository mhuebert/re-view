(ns re-view-hiccup.spec
  (:require [clojure.spec :as s :include-macros true]
            [cljs.spec.impl.gen :as gen]
            [cljs.spec.test :as t]
            [clojure.test.check.generators]
            [clojure.string :as string]
            [re-view-hiccup.react-html :as attrs]))


(defn gen-wrap
  "Wraps return values of generator of expr with f"
  [expr f]
  (s/with-gen expr #(gen/fmap f (s/gen expr))))

(defn gen-set
  "With generator from set"
  [expr set]
  (s/with-gen expr #(s/gen set)))

(s/def ::primitive (s/or :string string?
                         :number number?))

(s/def ::fn (-> fn?
                (gen-set #{identity})))

(s/def ::tag (-> keyword?
                 (gen-set #{:div :span :a})))

(s/def ::style-key (-> keyword?
                       (gen-set #{:font-size :color :background-color})))

(s/def ::style-map (s/map-of ::style-key ::primitive :gen-max 5 :conform-keys true))

(s/def ::prop-key (-> keyword?
                      (gen-set #{:class :width :height})))

(s/def ::prop-map (s/every (s/or :style-prop (s/tuple #{:style} ::style-map)
                                 :listener (s/tuple (-> (s/and keyword?
                                                               #(-> (name %)
                                                                    (string/starts-with? "on-")))
                                                        (gen-set #{:on-click})) ::fn)
                                 :prop (s/tuple ::prop-key ::primitive))
                           :kind map?
                           :into {}
                           :gen-max 10))

(s/def ::hiccup-children
  (s/* (s/or :element ::element
             :nil nil?
             :element-list (s/and seq?
                                  (s/coll-of ::element :into () :gen-max 5)))))

(s/def ::hiccup
  (-> (s/cat :tag ::tag
             :props (s/? ::prop-map)
             :body ::hiccup-children)
      (gen-wrap vec)))

(defn is-react-element? [x]
  (and (object? x)
       (or (boolean (aget x "re$view"))
           (js/React.isValidElement x))))

(s/def ::native-element (-> is-react-element?
                            (gen-set #{#js {"re$view" #js {}}})))

(s/def ::element (s/or :element ::native-element
                       :primitive ::primitive
                       :hiccup ::hiccup))



(s/fdef re-view-hiccup.core/element
        :args (s/cat :body ::element :opts (s/? (s/keys :opt-un [::fn])))
        :ret (s/or :element ::native-element
                   :primitive ::primitive))

#_(doall (->>
           ;(s/exercise ::element 6)
           (s/exercise-fn re-view-hiccup.core/element)
           (map prn)))


