(ns re-view.core-test
  (:require [cljs.test :refer-macros [deftest is are]]
            [re-view.core :as view :refer [component]]))



(def render-count (atom 0))

(def lifecycle-log (atom {}))

(defn log-lifecycle-states [this method & other-states]
  (doto lifecycle-log
    (swap! assoc-in [method :props] (view/props this))
    (swap! assoc-in [method :state] (view/state this)))

  (doseq [[attr val] (partition 2 other-states)]
    (swap! lifecycle-log assoc-in [method attr] val)))

(def initial-state {:eaten? false})

(def apple
  ;; a heavily logged component

  (component

    :get-initial-state
    (fn [this]
      (log-lifecycle-states this :get-initial-state)
      initial-state)

    :component-will-mount
    (fn [this]
      (log-lifecycle-states this :component-will-mount))

    :component-will-receive-props
    (fn [this next-props]
      (log-lifecycle-states this :component-will-receive-props
                            :next-props next-props))


    :should-component-update
    (fn [this next-props next-state]
      (log-lifecycle-states this :should-component-update
                            :next-props next-props
                            :next-state next-state))

    :component-will-update
    (fn [this next-props next-state]
      (log-lifecycle-states this :component-will-update
                            :next-props next-props
                            :next-state next-state))

    :should-component-update
    (fn [this next-props next-state]
      (log-lifecycle-states this :should-component-update
                            :next-props next-props
                            :next-state next-state)
      true)

    :render
    (fn [this]
      (log-lifecycle-states this :render)
      (swap! render-count inc)

      [:div "I am an apple."
       (when-not (:eaten (view/state this))
         [:p {:ref   "apple-statement-of-courage"
              :style {:font-weight "bold"}} " ...and I am brave and alive."])])

    :component-did-update
    (fn [this prev-props prev-state]
      (log-lifecycle-states this :component-did-update
                            :prev-props prev-props
                            :prev-state prev-state))))

(def util js/React.addons.TestUtils)
(def init-props {:color "red"})
(def init-child [:div {:style {:width         100
                               :height        100
                               :background    "red"
                               :border-radius 100}}])

(deftest state
  (let [el (js/document.body.appendChild (doto (js/document.createElement "div")
                                           (.setAttribute "id" "apple")))
        render #(js/ReactDOM.render (apple %1 %2) el)
        c (render init-props init-child)]

    ;; Initial State and Render
    (is (= {:eaten? false} (view/state c)))
    (is (= 1 @render-count))
    (is (= "red" (get-in @lifecycle-log [:get-initial-state :props :color]))
        "Read props from GetInitialState")


    ;; Update State
    (view/update-state! c update :eaten? not)
    (is (true? (:eaten? (view/state c)))
        "State has changed")
    (is (= 2 @render-count)
        "Component was rendered")

    (is (= "red" (:color (view/props c)))
        "Read props")


    ;; Force Rendering
    (view/render-component c {:color "green"})
    (is (= "green" (:color (view/props c)))
        "Force render - props changed")
    (is (= 3 @render-count)
        "Force rendered")


   ;; Children with force-render

    (view/render-component c {} [:div "div"])
    (is (= 1 (count (view/children c))))
    (is (= :div (ffirst (view/children c))))

    (view/render-component c {} [:p "Paragraph"])
    (is (= :p (ffirst (view/children c))))

    ;; Children with ordinary render

    (render nil [:span "Span"])
    (is (= :span (ffirst (view/children c))))


    ;; Ref
    (is (= "bold" (-> c
                      (view/react-ref "apple-statement-of-courage")
                      .-style
                      .-fontWeight)))))

(defn validate-transition
  "Ensure state/props transition from old to new value correctly"
  [props-or-state
   initial-val
   before-val
   after-val
   log]
  (let [_ nil]
    ;; ensure props, prev-state, prev-props, next-state, next-props
    ;; are supplied accurately in correct locations
    (case props-or-state
      :props (are [method-name val]
               (= val [(get-in log [method-name :props])
                       (get-in log [method-name :prev-props])
                       (get-in log [method-name :next-props])])

               :get-initial-state [initial-val _ _]
               :component-will-mount [initial-val _ _]
               :component-will-receive-props [before-val _ after-val]
               :should-component-update [before-val _ after-val]
               :component-will-update [before-val _ after-val]
               :render [after-val _ _]
               :component-did-update [after-val before-val _])

      :state (are [method-name val]
               (= val [(get-in log [method-name :state])
                       (get-in log [method-name :prev-state])
                       (get-in log [method-name :next-state])])

               :get-initial-state [_ _ _]
               :component-will-mount [initial-val _ _]
               ;:component-will-receive-props [before-val _ _]
               :should-component-update [before-val _ after-val]
               :component-will-update [before-val _ after-val]
               :render [after-val _ _]
               :component-did-update [after-val before-val _]))))

(deftest lifecycle
  (let [el (js/document.body.appendChild (doto (js/document.createElement "div")
                                           (.setAttribute "id" "apple")))
        render #(js/ReactDOM.render (apple %1 nil) el)
        this (render {:color "purple"})]

    ;; Prop transition, ordinary render

    (render {:color "pink"})
    (render {:color "blue"})

    ;; in all instances of prev-props, color was "pink"
    (validate-transition :props
                         {:color "purple"}                  ;; initial
                         {:color "pink"}                    ;; before
                         {:color "blue"}                    ;; after
                         @lifecycle-log)

    ;; state has not changed
    (validate-transition :state
                         initial-state                      ;; initial
                         initial-state                      ;; before
                         initial-state                      ;; after
                         @lifecycle-log)

    ;; Prop transition, view/render-component (with .forceUpdate)
    (reset! lifecycle-log {})
    (view/render-component this {:color "yellow"})
    (view/render-component this {:color "mink"})

    (validate-transition :props
                         nil                                ;; initial
                         {:color "yellow"}                  ;; before
                         {:color "mink"}                    ;; after
                         @lifecycle-log)

    ;; Two force-renders in a row

    (view/render-component this {:color "fox"})

    (validate-transition :props
                         nil                                ;; initial
                         {:color "mink"}                    ;; before
                         {:color "fox"}                     ;; after
                         @lifecycle-log)


    ;; Force-render followed by normal render

    (render {:color "bear"})

    (validate-transition :props
                         nil                                ;; initial
                         {:color "fox"}                     ;; before
                         {:color "bear"}                    ;; after
                         @lifecycle-log)

    ;; State transition
    (reset! lifecycle-log {})
    (view/set-state! this {:shiny? false})
    (view/update-state! this update :shiny? not)

    (validate-transition :state
                         nil                                ;; initial
                         {:shiny? false}                    ;; before
                         {:shiny? true}                     ;; after
                         @lifecycle-log)))