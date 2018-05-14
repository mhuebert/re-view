(ns re-style.examples
  (:require [re-style.core :as re]
            [re-style.utils :as utils]
            [clojure.test :refer [is are]]))

(def rem-scale [0 0.25 0.5 0.75 1 2 3 4 6 8])

(def zero-index (take 20 (range)))

(def color-adjust
  {[:text
    :bg
    :border] {:lighten
              {[0.1 0.2 0.3 0.5 0.75 0.9 0.95] (assoc utils/color-rules*
                                                 :re/index (rest zero-index)
                                                 :re/format-value #(str "rgba(255,255,255," % ")"))}
              :darken
              {[0.03, 0.05, 0.07, 0.1, 0.2 0.8 0.9] (assoc utils/color-rules*
                                                      :re/index (rest zero-index)
                                                      :re/format-value #(str "rgba(0,0,0," % ")"))}}})

(def text
  (let [text-scale [48 36 24 18 15 13 10 8]]
    {:text {:size {text-scale #:re {:rule :font-size
                                    :index (rest zero-index)
                                    :format-value re/px}}

            :height {text-scale #:re{:rule :line-height
                                     :index (rest zero-index)
                                     :format-value (comp re/px (partial * 1.2))}}

            :spacing {[-0.05 0.1 0.25] {:re/rule :letter-spacing
                                        :re/index (->> zero-index
                                                       (rest)
                                                       (cons "n1"))}}
            :italic :font-style

            [:pre
             :pre-wrap
             :nowrap] :white-space

            [:left
             :center
             :right] :text-align

            [:uppercase
             :capitalize
             :uppercase] :text-transform

            :small-caps :font-variant

            [:underline
             :strike
             :no-decoration] #:re {:rule :text-decoration
                                   :replace {:no-decoration :none}
                                   :pseudos [:hover]}

            :weight {[100 200 300 400 500 600 700 800] :font-weight}

            [:light :normal :semi-bold :bold] :font-weight

            :truncate #:re {:rule {:white-space "nowrap"
                                   :overflow "hidden"
                                   :text-overflow "ellipsis"}}}}))

(def cursor
  {:cursor {[:default
             :pointer] #:re {:rule (re/path-rule 0 1)
                             :pseudos #{:hover}}}})

(def spacing
  {[:pad
    :margin] {[:h
               :v
               :top
               :right
               :bottom
               :left] {rem-scale #:re {:rule (fn [{[space-type direction] :re/path
                                                   :keys [re/value]}]
                                               (case direction
                                                 :h {:margin-left value
                                                     :margin-right value}
                                                 :v {:margin-top value
                                                     :margin-bottom value}
                                                 {[space-type direction] value}))
                                       :format-value re/rem
                                       :index zero-index
                                       :replace {:pad :padding}}}}})

(def layout
  {:pos {[:block
          :inline-block
          :inline
          :flex
          :inline-flex
          :none
          :table
          :table-column
          :table-row
          :table-cell] :display

         [:absolute
          :relative
          :fixed] :position

         [:bottom
          :left
          :right
          :top] {:re/rule (fn [{:keys [re/value]}]
                            {value (re/px 0)})}

         :z {(-> [:unset
                  :inherit
                  :initial]
                 (into
                  (range 0 20))
                 (conj 99
                       999
                       9999)) :z-index

             :max {:re/rule {:z-index 2147483647}}}}

   :outline {{:none 0} :outline}

   :align {[:base
            :bottom
            :mid
            :top] :vertical-align
           [:left
            :right
            :center] :text-align}
   :clear {[:both
            :left
            :right
            :none] :clear}

   :float {[:left
            :right
            :none] :float}

   :center {:re/rule {:margin-left "auto"
                      :margin-right "auto"}}

   :visibility {[:hidden
                 :clip
                 :visible] :visibility}

   :overflow (let [opts [:auto
                         :hidden
                         :visible
                         :scroll]]
               {:x {opts :overflow-x}
                :y {opts :overflow-y}
                opts :overflow})})

(def flex
  {:flex {:auto {:re/rule {:flex "1 1 auto"
                           :min-width 0
                           :min-height 0}}
          :none :flex

          [:column
           :column-reverse
           :row
           :row-reverse] {:re/rule (fn [{:keys [re/path]}]
                                     {:display :flex
                                      :flex-direction (second path)})}

          :inline {:re/rule {:display :inline-flex}}

          [:wrap
           :nowrap
           :wrap-reverse] :flex-wrap

          [:items
           :self] {[:baseline
                    :stretch] {:re/rule
                               (fn [{:keys [re/path re/value]}]
                                 {[:align (second path)] value})}}

          [:items
           :self
           :content                                         ;; see https://medium.com/@wendersyang/what-the-flex-is-the-difference-between-justify-content-align-items-and-align-content-5fd3694f5259
           :justify] {[:start
                       :end
                       :center
                       :between
                       :around] {:re/replace {:start :flex-start
                                              :end :flex-end
                                              :around :space-around
                                              :between :space-between}
                                 :re/rule (fn [{:keys [re/path re/value]}]
                                            {(case (second path)
                                               :justify :justify-content
                                               [:align (second path)]) value})}}

          :order {(range 10) :order
                  {:last 999} :order}}})

(def background
  {:bg {[:contain :cover] :background-size

        :center :background-position

        [:repeat
         :no-repeat
         :repeat-x
         :repeat-y] :background-repeat}})

(def opacity
  {:opacity {(range 0 110 10) #:re{:rule :opacity
                                   :format-value (partial * 0.01)}}})

(def percent-scale
  (partial utils/->map {:key #(str % \p)
                        :value #(str % \%)}))

(def border
  {:border {:width {(range 11) #:re{:rule :border-width
                                    :format-value re/px}}

            :radius {(range 11) #:re{:rule :border-radius
                                     :format-value re/px}
                     (percent-scale [50 100]) :border-radius}
            [:dashed
             :dotted
             :solid] :border-style}})

(def size
  {:size {[:width
           :height] {rem-scale #:re {:rule (re/path-rule 1)
                                     :format-value re/rem
                                     :index zero-index}
                     (-> (percent-scale [25 50 75 100])
                         (merge {:third "33.3#%"
                                 :two-thirds "66.66%"})) #:re {:rule (re/path-rule 1)}}}})

(def transform
  {:transform {:rotate {(->> [45 90 135 180
                              -45 -90 -135 -180]
                             (utils/->map
                              {:key #(if (neg? %)
                                       (str \n (- %))
                                       %)
                               :value #(str "rotate(" (re/deg %) ")")}))
                        :transform}}})

(def rules
  {:text text
   :cursor cursor
   :spacing spacing
   :flex flex
   :layout layout
   :background background
   :color-adjust color-adjust
   :opacity opacity
   :border border
   :size size
   :transform transform})

(do
  (apply re/compile-rules (vals rules)))


(comment

 ;; shadows
 :shadow/box-1
 :shadow/inner-1
 :shadow/text-1)

;; TODO
;;
;; - reference table (all classes & their output)
;;
;; - examples, derived from inline functions
;;   :re/example (fn [class] [:div {:class class} "A"])
;;
;; - documentation, derived from inline docs
;;   :re/doc "Set text size"
;;
;; - hook into re-view, transform classes
;;   - warn on unknown class
;; - output CSS (via garden)
;; - later: precompile :classes in a macro


