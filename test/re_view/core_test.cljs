(ns re-view.core-test
  (:require [cljs.test :refer-macros [deftest is are]]
            [re-view.core :as view :refer [component]]))

(def lifecycle-signatures
  ;; React lifecycle method signatures
  {:get-initial-state            #{}
   :component-will-mount         #{}
   :component-will-receive-props #{:next-props}
   :should-component-update      #{:next-props :next-state}
   :component-will-update        #{:next-props :next-state}
   :render                       #{}
   :component-did-update         #{:prev-props :prev-state}})

(def render-count (atom 0))

(def lifecycle-log (atom {}))

(defn log-lifecycle-states [this method & other-states]
  (doto lifecycle-log
    (swap! assoc-in [method :props] (view/props this))
    (swap! assoc-in [method :state] (view/state this)))

  (doseq [[attr val] (partition 2 other-states)]
    (swap! lifecycle-log assoc-in [method attr] val)))

(def apple
  ;; a heavily logged component

  (component

    :get-initial-state
    (fn [this]
      (log-lifecycle-states this :get-initial-state)
      {:eaten? false})

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

    :component-did-update
    (fn [this prev-props prev-state]
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
              :style {:font-weight "bold"}} " ...and I am brave and alive."])])))

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


    ;; * note, Force Rendering does not currently support supplying new/changed children


    ;; Children
    (is (= 1 (count (view/children c))))
    (is (= :div (ffirst (view/children c))))


    ;; Ref
    (is (= "bold" (-> c
                      (view/react-ref "apple-statement-of-courage")
                      .-style
                      .-fontWeight)))))

(def lifecycle-arg-index
  ;; which args should be provided where
  {:next-props #{:component-will-receive-props
                 :should-component-update
                 :component-will-update}
   :next-state #{:should-component-update
                 :component-will-update}
   :prev-props #{:component-did-update}
   :prev-state #{:component-did-update}})

(defn validate-lifecycle-args
  "Ensures the correct arguments were logged for every lifecycle method."
  [log-atom]
  (doseq [[method logged-args] @log-atom]
    (is (= (set (keys logged-args))
           (into (get lifecycle-signatures method) #{:props :state})))))

(defn verify-lifecycle-args
  "Ensures the correct lifecycle methods were called for every arg."
  ([log-atom] (verify-lifecycle-args log-atom false))
  ([log-atom state?]
   (validate-lifecycle-args log-atom)
   (let [index (cond-> lifecycle-arg-index
                       state? (update :next-props disj :component-will-receive-props))]
     (doseq [method (keys index)]
       (is (= (get index method)
              (->> @log-atom
                   (filter (fn [[_ state-and-prop-logs]] (get state-and-prop-logs method)))
                   keys
                   set)))))))



(deftest lifecycle
  (let [el (js/document.body.appendChild (doto (js/document.createElement "div")
                                           (.setAttribute "id" "apple")))
        render #(js/ReactDOM.render (apple %1 %2) el)
        this (render {:color "purple"} nil)]

    (is (= {:color "purple"}
           (get-in @lifecycle-log [:component-will-mount :props])
           (get-in @lifecycle-log [:get-initial-state :props])))

    ;; Prop transition, ordinary render
    (reset! lifecycle-log {})
    (render {:color "pink"} nil)
    (render {:color "blue"} nil)

    ;; ensure that the correct state and prop objects
    ;; were passed to the correct lifecycle methods
    (verify-lifecycle-args lifecycle-log)

    ;; in all instances of prev-props, color was "pink"
    (is (= #{"pink"} (->> @lifecycle-log
                          vals
                          (keep :prev-props)
                          (map :color)
                          set))
        "prev-props accurate in ordinary render")
    ;; in all instances of next-props, color was "blue"
    (is (= #{"blue"} (->> @lifecycle-log
                          vals
                          (keep :next-props)
                          (map :color)
                          set))
        "next-props accurate in ordinary render")

    ;; test that value of `props` transitions at correct time
    (are [method-name color]
      (= color (get-in @lifecycle-log [method-name :props :color]))

      :component-will-receive-props "pink"
      :should-component-update "pink"
      :component-will-update "pink"
      :render "blue"
      :component-did-update "blue")

    ;; Prop transition, view/render-component (with .forceUpdate)
    (reset! lifecycle-log {})
    (view/render-component this {:color "yellow"})
    (view/render-component this {:color "mink"})

    ;; all prev-props were "yellow"
    (is (= #{"yellow"} (->> @lifecycle-log
                            vals
                            (keep :prev-props)
                            (map :color)
                            set))
        "prev-props accurate in force render")

    ;; all next-props were "mink"
    (is (= #{"mink"} (->> @lifecycle-log
                          vals
                          (keep :next-props)
                          (map :color)
                          set))
        "next-props accurate in force render")

    (verify-lifecycle-args lifecycle-log)


    ;; test that value of `props` transitions at correct time
    (are [method-name color]
      (= color (get-in @lifecycle-log [method-name :props :color]))

      :component-will-receive-props "yellow"
      :should-component-update "yellow"
      :component-will-update "yellow"
      :render "mink"
      :component-did-update "mink")

    ;; State transition
    (reset! lifecycle-log {})
    (view/update-state! this assoc :shiny? false)
    (view/update-state! this update :shiny? not)

    (is (= #{false} (->> @lifecycle-log
                         vals
                         (keep :prev-state)
                         (map :shiny?)
                         set))
        "prev-state correct throughout state transition")

    (is (= #{true} (->> @lifecycle-log
                        vals
                        (keep :next-state)
                        (map :shiny?)
                        set))
        "next-state correct throughout state transition")

    (verify-lifecycle-args lifecycle-log true)))