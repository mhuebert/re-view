(ns re-view.core-test
  (:require [cljs.test :refer [deftest is are testing]]
            [cljsjs.react]
            [cljsjs.react.dom]
            [re-view.core :as v :refer [defview]]))


(def render-count (atom 0))

(def lifecycle-log (atom {}))

#_(defn log-args
    [& args]
    (reset! lifecycle-log args))

(defn log-args [method this]
  (swap! lifecycle-log assoc method (assoc (select-keys this [:view/props
                                                              :view/prev-props
                                                              :view/prev-state
                                                              :view/children])
                                      :view/state @(:view/state this)))
  true)

(def initial-state {:eaten? false})

(defview apple
  {:initial-state      (fn [& args]
                         (apply (partial log-args :get-initial-state) args)
                         initial-state)

   :will-mount         (partial log-args :will-mount)

   :did-mount          (partial log-args :did-mount)

   :will-receive-props (partial log-args :will-receive-props)

   :will-receive-state (partial log-args :will-receive-state)

   :should-update      (partial log-args :should-update)

   :will-update        (partial log-args :will-update)

   :did-update         (partial log-args :did-update)

   :will-unmount       (partial log-args :will-unmount)
   :pRef               (fn [& args]
                         (println "I am a ref that was called!" args))}
  [{:keys [view/state] :as this}]
  (log-args :render this)
  (swap! render-count inc)
  [:div "I am an apple."
   (when-not (:eaten? @state)
     [:p {:ref   #(when % (swap! state assoc :p %))
          :style {:fontWeight "bold"}} " ...and I am brave and alive."])])


;; a heavily logged component

(def util js/React.addons.TestUtils)
(def init-props {:color "red"})
(def init-child [:div {:style {:width        100
                               :height       100
                               :background   "red"
                               :borderRadius 100}}])

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))


(deftest basic
  (let [el (append-el)
        render #(v/render-to-node (apple %1 %2) el)
        c (render init-props init-child)]

    (testing "initial state"
      (is (false? (:eaten? @(:view/state c))))
      (is (= 1 @render-count))
      (is (= "red" (get-in @lifecycle-log [:get-initial-state :view/props :color]))
          "Read props from GetInitialState")
      (is (= "red" (get-in c [:view/props :color]))
          "Read props"))

    (testing "update state"

      ;; Update State
      (swap! (:view/state c) update :eaten? not)
      (v/flush!)

      (is (true? (:eaten? @(:view/state c)))
          "State has changed")
      (is (= 2 @render-count)
          "Component was rendered"))

    (testing "render with new props"

      (render {:color "green"} nil)
      (is (= "green" (get-in c [:view/props :color]))
          "Update Props")
      (is (= 3 @render-count)
          "Force rendered"))

    (testing "children"

      (render {} [:div "div"])
      (is (= 1 (count (:view/children c)))
          "Has one child")
      (is (= :div (ffirst (:view/children c)))
          "Read child")

      (render {} [:p "Paragraph"])
      (is (= :p (ffirst (:view/children c)))
          "New child - force render")

      (render nil [:span "Span"])
      (is (= :span (ffirst (:view/children c)))
          "New child - normal render"))

    (testing "refs"
      (is (= "bold" (-> (:p @(:view/state c))
                        .-style
                        .-fontWeight))
          "Read react ref"))))


(deftest lifecycle-transitions
  (let [el (js/document.body.appendChild (doto (js/document.createElement "div")
                                           (.setAttribute "id" "apple")))
        render #(js/ReactDOM.render (apple %1 nil) el)
        initial-props {:color "purple"}
        this (render initial-props)]


    (testing "prop transitions"

      (render {:color "pink"})
      (render {:color "blue"})

      (is (= "pink" (get-in this [:view/prev-props :color])))
      (is (= "blue" (get-in this [:view/props :color])))


      (render {:color "yellow"})
      (render {:color "mink"})

      (is (= "yellow" (get-in this [:view/prev-props :color])))
      (is (= "mink" (get-in this [:view/props :color])))

      (render {:color "bear"})
      (is (= "mink" (get-in this [:view/prev-props :color])))
      (is (= "bear" (get-in this [:view/props :color]))))

    (testing "state transition"

      (reset! (:view/state this) {:shiny? false})
      (v/flush!)

      (is (false? (:shiny? @(:view/state this))))

      (swap! (:view/state this) update :shiny? not)
      (v/flush!)

      (is (false? (get-in @lifecycle-log [:will-receive-state :view/prev-state :shiny?]))
          "Prev-state recalls previous state")
      (is (true? (get-in @lifecycle-log [:will-receive-state :view/state :shiny?]))
          "State has updated")

      (render {:color "violet"})
      (is (= @(:view/state this)
             (:view/prev-state this))
          "After a component lifecycle, prev-state and state are the same."))))


;; test react-key
;; test expand-props
;; - should this be called every time the component is rendered?
;;   can we cache anything?