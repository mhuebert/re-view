(ns re-view.core-test
  (:require [cljs.test :refer-macros [deftest is are]]
            [re-view.core :as view :refer-macros [defcomponent]]))



(def render-count (atom 0))

(def lifecycle-log (atom {}))

(defn log-lifecycle-states [this method & other-states]
  (doto lifecycle-log
    (swap! assoc-in [method :props] (view/props this))
    (swap! assoc-in [method :state] (view/state this)))

  (doseq [[attr val] (partition 2 other-states)]
    (swap! lifecycle-log assoc-in [method attr] val)))

(def initial-state {:eaten? false})


;; a heavily logged component

(defcomponent apple

  :get-initial-state
  (fn [this]
    (log-lifecycle-states this :get-initial-state)
    initial-state)

  :component-will-mount
  (fn [this]
    (log-lifecycle-states this :component-will-mount))

  :component-will-receive-props
  (fn [this prev-props next-props]
    (log-lifecycle-states this :component-will-receive-props
                          :next-props next-props))


  :should-component-update
  (fn [this _ _ next-props next-state]
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




  :component-did-update
  (fn [this _ _ prev-props prev-state]
    (log-lifecycle-states this :component-did-update
                          :prev-props prev-props
                          :prev-state prev-state))
  :render
  (fn [this]
    (log-lifecycle-states this :render)
    (swap! render-count inc)

    [:div "I am an apple."
     (when-not (:eaten (view/state this))
       [:p {:ref   "apple-statement-of-courage"
            :style {:font-weight "bold"}} " ...and I am brave and alive."])]))

(def util js/React.addons.TestUtils)
(def init-props {:color "red"})
(def init-child [:div {:style {:width         100
                               :height        100
                               :background    "red"
                               :border-radius 100}}])

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))

(deftest basic
  (let [el (append-el)
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
    (is (= 1 (count (view/children c)))
        "Has one child")
    (is (= :div (ffirst (view/children c)))
        "Read child")

    (view/render-component c {} [:p "Paragraph"])
    (is (= :p (ffirst (view/children c)))
        "New child - force render")

    ;; Children with ordinary render

    (render nil [:span "Span"])
    (is (= :span (ffirst (view/children c)))
        "New child - normal render")


    ;; Ref
    (is (= "bold" (-> c
                      (view/react-ref "apple-statement-of-courage")
                      .-style
                      .-fontWeight))
        "Read react ref")))

(defn validate-transition
  "Ensure state/props transition from old to new value correctly"
  [log props-or-state [initial-val before-val after-val]]
  (let [_ nil]
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
               :should-component-update [before-val _ after-val]
               :component-will-update [before-val _ after-val]
               :render [after-val _ _]
               :component-did-update [after-val before-val _]))))

(deftest lifecycle-transitions
  (let [el (js/document.body.appendChild (doto (js/document.createElement "div")
                                           (.setAttribute "id" "apple")))
        render #(js/ReactDOM.render (apple %1 nil) el)
        this (render {:color "purple"})]

    ;; Prop transition, ordinary render

    (render {:color "pink"})
    (render {:color "blue"})

    ;; in all instances of prev-props, color was "pink"
    (validate-transition @lifecycle-log :props
                         [{:color "purple"}
                          {:color "pink"}
                          {:color "blue"}])

    ;; state has not changed
    (validate-transition @lifecycle-log :state
                         [initial-state
                          initial-state
                          initial-state])

    ;; Prop transition, view/render-component (with .forceUpdate)
    (reset! lifecycle-log {})
    (view/render-component this {:color "yellow"})
    (view/render-component this {:color "mink"})

    (validate-transition @lifecycle-log :props
                         [nil
                          {:color "yellow"}
                          {:color "mink"}])

    ;; Two force-renders in a row

    (view/render-component this {:color "fox"})

    (validate-transition @lifecycle-log :props
                         [nil
                          {:color "mink"}
                          {:color "fox"}])


    ;; Force-render followed by normal render

    (render {:color "bear"})

    (validate-transition @lifecycle-log :props
                         [nil
                          {:color "fox"}
                          {:color "bear"}])

    ;; State transition
    (reset! lifecycle-log {})
    (view/set-state! this {:shiny? false})
    (is (false? (-> this view/state :shiny?)))

    (view/update-state! this update :shiny? not)

    (validate-transition @lifecycle-log :state
                         [nil
                          {:shiny? false}
                          {:shiny? true}])))


;; test react-key
;; test expand-props
;; - should this be called every time the component is rendered?
;;   can we cache anything?