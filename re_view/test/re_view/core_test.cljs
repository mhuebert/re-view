(ns re-view.core-test
  (:require [cljs.test :refer [deftest is are testing]]
            [re-view.core :as v :refer [defview]]
            ["react-dom" :as react-dom]))


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
                                      :view/state (some-> (:view/state this) (deref))))
  true)

(def initial-state {:eaten? false})

(defview apple
  {:view/initial-state      (fn [this]
                              (log-args :view/initial-state this)
                              initial-state)

   :view/will-mount         #(log-args :view/will-mount %1)

   :view/did-mount          #(log-args :view/did-mount %1)

   :view/will-receive-props #(log-args :view/will-receive-props %1)

   :view/will-receive-state #(log-args :view/will-receive-state %1)

   :view/should-update      #(log-args :view/should-update %1)

   :view/will-update        #(log-args :view/will-update %1)

   :view/did-update         #(log-args :view/did-update %1)

   :view/will-unmount       #(log-args :view/will-unmount %1)
   :pRef                    (fn [& args]
                              (println "I am a ref that was called!" args))}
  [{:keys [view/state] :as this} _]
  (log-args :view/render this)
  (swap! render-count inc)
  [:div "I am an apple."
   (when-not (:eaten? @state)
     [:p {:ref   #(when % (swap! state assoc :p %))
          :style {:font-weight "bold"}} " ...and I am brave and alive."])])


;; a heavily logged component

(def util (.. react-dom -__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED -ReactTestUtils))
(def init-props {:color "red"})
(def init-child [:div {:style {:width         100
                               :height        100
                               :background    "red"
                               :border-radius 100}}])

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))


(deftest basic
  (let [el (append-el)
        render #(v/render-to-dom (apple %1 %2) el)
        c (render init-props init-child)]


    (testing "initial state"
      (is (false? (:eaten? @(:view/state c))))
      (is (= 1 @render-count))
      (is (= "red" (get-in @lifecycle-log [:view/initial-state :view/props :color]))
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
      (is (= "green" (:color c))
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
        render #(react-dom/render (apple %1 nil) el)
        initial-props {:color "purple"}
        this (render initial-props)]


    (testing "prop transitions"

      (render {:color "pink"})
      (render {:color "blue"})

      (is (= "pink" (get-in @lifecycle-log [:view/did-update :view/prev-props :color])))
      (is (= "blue" (:color this)))


      (render {:color "yellow"})
      (render {:color "mink"})

      (is (= "yellow" (get-in @lifecycle-log [:view/did-update :view/prev-props :color])))
      (is (= "mink" (:color this)))

      (render {:color "bear"})
      (is (= "mink" (get-in @lifecycle-log [:view/will-update :view/prev-props :color])))
      (is (= "bear" (:color this))))

    (testing "state transition"

      (reset! (:view/state this) {:shiny? false})
      (v/flush!)

      (is (false? (:shiny? @(:view/state this))))

      (swap! (:view/state this) assoc :shiny? true)
      (v/flush!)

      (is (false? (get-in @lifecycle-log [:view/will-receive-state :view/prev-state :shiny?]))
          "Prev-state recalls previous state")
      (is (true? (get-in @lifecycle-log [:view/will-receive-state :view/state :shiny?]))
          "State has updated")

      (render {:color "violet"})

      (is (= @(:view/state this)
             (:view/prev-state this))
          "After a component lifecycle, prev-state and state are the same."))

    (testing "Multiple arity defview/view"

      (let [log (atom [])]))

    ))


;; test react-key
;; test expand-props
;; - should this be called every time the component is rendered?
;;   can we cache anything?