(ns re-view.core-test
  (:require [cljs.test :refer [deftest is are testing]]
            [re-view.core :as v :refer [defview]]))


(def render-count (atom 0))

(def lifecycle-log (atom {}))

#_(defn log-args
    [& args]
    (reset! lifecycle-log args))

(defn log-args [method this]
  (swap! lifecycle-log assoc method (select-keys this [:props
                                                       :state
                                                       :prev-props
                                                       :prev-state
                                                       :children]))
  true)

(def initial-state {:eaten? false})

(defview apple
  {:initial-state
                       (fn [& args]
                         (apply (partial log-args :get-initial-state) args)
                         initial-state)

   :will-mount         (partial log-args :will-mount)

   :did-mount          (partial log-args :did-mount)

   :will-receive-props (partial log-args :will-receive-props)

   :will-receive-state (partial log-args :will-receive-state)

   :should-update      (partial log-args :should-update)

   :will-update        (partial log-args :will-update)

   :did-update         (partial log-args :did-update)

   :will-unmount       (partial log-args :will-unmount)}
  (fn [this]
    (log-args :render this)
    (swap! render-count inc)
    [:div "I am an apple."
     (when-not (get-in this [:state :eaten])
       [:p {:ref   "apple-statement-of-courage"
            :style {:font-weight "bold"}} " ...and I am brave and alive."])]))


;; a heavily logged component

(def util js/React.addons.TestUtils)
(def init-props {:color "red"})
(def init-child [:div {:style {:width         100
                               :height        100
                               :background    "red"
                               :border-radius 100}}])

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))


(deftest basic
  (let [el (append-el)
        render-to-dom #(js/ReactDOM.render (apple %1 %2) el)
        c (render-to-dom init-props init-child)]

    (testing "initial state"
      (is (= {:eaten? false} (:state c)))
      (is (= 1 @render-count))
      (is (= "red" (get-in @lifecycle-log [:get-initial-state :props :color]))
          "Read props from GetInitialState")
      (is (= "red" (get-in c [:props :color]))
          "Read props"))

    (testing "update state"

      ;; Update State
      (v/swap-state! c update :eaten? not)
      (v/flush!)

      (is (true? (get-in c [:state :eaten?]))
          "State has changed")
      (is (= 2 @render-count)
          "Component was rendered"))

    (testing "render with new props"

      (render-to-dom {:color "green"} nil)
      (is (= "green" (get-in c [:props :color]))
          "Update Props")
      (is (= 3 @render-count)
          "Force rendered"))

    (testing "children"

      (render-to-dom {} [:div "div"])
      (is (= 1 (count (:children c)))
          "Has one child")
      (is (= :div (ffirst (:children c)))
          "Read child")

      (render-to-dom {} [:p "Paragraph"])
      (is (= :p (ffirst (:children c)))
          "New child - force render")

      (render-to-dom nil [:span "Span"])
      (is (= :span (ffirst (:children c)))
          "New child - normal render"))

    (testing "refs"
      (is (= "bold" (-> c
                        (v/ref "apple-statement-of-courage")
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

      (is (= "pink" (get-in this [:prev-props :color])))
      (is (= "blue" (get-in this [:props :color])))


      (render {:color "yellow"})
      (render {:color "mink"})

      (is (= "yellow" (get-in this [:prev-props :color])))
      (is (= "mink" (get-in this [:props :color])))

      (render {:color "bear"})
      (is (= "mink" (get-in this [:prev-props :color])))
      (is (= "bear" (get-in this [:props :color]))))

    (testing "state transition"

      (reset! this {:shiny? false})
      (v/flush!)

      (is (false? (-> this :state :shiny?)))

      (v/swap-state! this update :shiny? not)
      (v/flush!)

      (is (false? (get-in @lifecycle-log [:will-receive-state :prev-state :shiny?])))
      (is (true? (get-in @lifecycle-log [:will-receive-state :state :shiny?])))

      (render {:color "violet"})
      (is (= (:state this)
             (:prev-state this))
          "After a component lifecycle, prev-state and state are the same."))))


;; test react-key
;; test expand-props
;; - should this be called every time the component is rendered?
;;   can we cache anything?