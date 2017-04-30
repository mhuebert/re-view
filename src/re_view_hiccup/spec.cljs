(ns re-view-hiccup.spec
  (:require [clojure.spec :as s :include-macros true]
            [cljs.spec.impl.gen :as gen]
            [cljs.spec.test :as t]
            [clojure.test.check.generators]
            [clojure.string :as string]))

;; canonical source of supported attributes:
;; https://facebook.github.io/react/docs/dom-elements.html

(def pattern #"(?:data|aria)\-.+")

(def html-attrs
  [:accept :accept-charset :access-key :action :allow-full-screen :allow-transparency :alt
   :async :auto-complete :auto-focus :auto-play :capture :cell-padding :cell-spacing :challenge
   :char-set :checked :cite :classID :class-name :col-span :cols :content :content-editable
   :context-menu :controls :coords :cross-origin :data :date-time :default :defer :dir
   :disabled :download :draggable :enc-type :form :form-action :form-enc-type :form-method
   :form-no-validate :form-target :frame-border :headers :height :hidden :high :href :href-lang
   :html-for :http-equiv :icon :id :input-mode :integrity :is :key-params :key-type :kind :label
   :lang :list :loop :low :manifest :margin-height :margin-width :max :max-length :media
   :media-group :method :min :min-length :multiple :muted :name :no-validate :nonce :open
   :optimum :pattern :placeholder :poster :preload :profile :radio-group :read-only :rel
   :required :reversed :role :row-span :rows :sandbox :scope :scoped :scrolling :seamless
   :selected :shape :size :sizes :span :spell-check :src :src-doc :src-lang :src-set :start :step
   :style :summary :tab-index :target :title :type :use-map :value :width :wmode :wrap])

(def RDFa-attrs
  [:about :datatype :inlist :prefix :property :resource :typeof :vocab])

(def non-standard-attrs
  [:auto-capitalize :auto-correct :color :item-prop :item-scope :item-type :item-ref :itemID
   :security :unselectable :results :auto-save])

(def svg-attrs
  [:accent-height :accumulate :additive :alignment-baseline :allow-reorder :alphabetic
   :amplitude :arabic-form :ascent :attribute-name :attribute-type :auto-reverse :azimuth
   :base-frequency :base-profile :baseline-shift :bbox :begin :bias :by :calc-mode :cap-height
   :clip :clip-path :clip-path-units :clip-rule :color-interpolation
   :color-interpolation-filters :color-profile :color-rendering :content-script-type
   :content-style-type :cursor :cx :cy :d :decelerate :descent :diffuse-constant :direction
   :display :divisor :dominant-baseline :dur :dx :dy :edge-mode :elevation :enable-background
   :end :exponent :external-resources-required :fill :fill-opacity :fill-rule :filter
   :filter-res :filter-units :flood-color :flood-opacity :focusable :font-family :font-size
   :font-size-adjust :font-stretch :font-style :font-variant :font-weight :format :from :fx :fy
   :g1 :g2 :glyph-name :glyph-orientation-horizontal :glyph-orientation-vertical :glyph-ref
   :gradient-transform :gradient-units :hanging :horiz-advX :horiz-originX :ideographic
   :image-rendering :in :in2 :intercept :k :k1 :k2 :k3 :k4 :kernel-matrix :kernel-unit-length
   :kerning :key-points :key-splines :key-times :length-adjust :letter-spacing :lighting-color
   :limiting-cone-angle :local :marker-end :marker-height :marker-mid :marker-start
   :marker-units :marker-width :mask :mask-content-units :mask-units :mathematical :mode
   :num-octaves :offset :opacity :operator :order :orient :orientation :origin :overflow
   :overline-position :overline-thickness :paint-order :panose1 :path-length
   :pattern-content-units :pattern-transform :pattern-units :pointer-events :points
   :points-atX :points-atY :points-atZ :preserve-alpha :preserve-aspect-ratio :primitive-units
   :r :radius :refX :refY :rendering-intent :repeat-count :repeat-dur :required-extensions
   :required-features :restart :result :rotate :rx :ry :scale :seed :shape-rendering :slope
   :spacing :specular-constant :specular-exponent :speed :spread-method :start-offset
   :std-deviation :stemh :stemv :stitch-tiles :stop-color :stop-opacity
   :strikethrough-position :strikethrough-thickness :string :stroke :stroke-dasharray
   :stroke-dashoffset :stroke-linecap :stroke-linejoin :stroke-miterlimit :stroke-opacity
   :stroke-width :surface-scale :system-language :table-values :targetX :targetY :text-anchor
   :text-decoration :text-length :text-rendering :to :transform :u1 :u2 :underline-position
   :underline-thickness :unicode :unicode-bidi :unicode-range :units-per-em :v-alphabetic
   :v-hanging :v-ideographic :v-mathematical :values :vector-effect :version :vert-advY
   :vert-originX :vert-originY :view-box :view-target :visibility :widths :word-spacing
   :writing-mode :x :x1 :x2 :x-channel-selector :x-height :xlink-actuate :xlink-arcrole
   :xlink-href :xlink-role :xlink-show :xlink-title :xlink-type :xmlns :xmlns-xlink :xml-base
   :xml-lang :xml-space :y :y1 :y2 :y-channel-selector :z :zoom-and-pan])

(def re-view-hiccup
  [:class :classes])

(def prop-keys (->> [html-attrs
                     RDFa-attrs
                     non-standard-attrs
                     svg-attrs]
                    (apply concat)
                    (set)))

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

(s/def ::prop-map (s/every (s/or :style-prop (s/tuple #{:style} ::style-map)
                                 :listener (s/tuple (-> (s/and keyword?
                                                               #(-> (name %)
                                                                    (string/starts-with? "on-")))
                                                        (gen-set #{:on-click})) ::fn)
                                 :prop (s/tuple prop-keys ::primitive))
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


