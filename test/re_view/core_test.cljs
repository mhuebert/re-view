(ns re-view.core-test
  (:require [cljs.test :refer-macros [deftest is are testing]]
            [re-view.core :as view :refer-macros [defcomponent]]))


(def render-count (atom 0))

(def lifecycle-log (atom {}))

(defn log-args
  [& args]
  (reset! lifecycle-log args))

(defn log-args [method & arguments]
  (swap! lifecycle-log assoc method {:args  arguments
                                     :props (view/props (first arguments))
                                     :state (view/state (first arguments))}))

(def initial-state {:eaten? false})

(defcomponent apple
  :get-initial-state
  (fn [& args]
    (apply (partial log-args :get-initial-state) args)
    initial-state)

  :component-will-mount (partial log-args :component-will-mount)

  :component-did-mount (partial log-args :component-did-mount)

  :component-will-receive-props (partial log-args :component-will-receive-props)

  :should-component-update (partial log-args :should-component-update)

  :component-will-update (partial log-args :component-will-update)

  :should-component-update (fn [& args] (apply (partial log-args :should-component-update) args) true)

  :component-did-update (partial log-args :component-did-update)

  :component-will-unmount (partial log-args :component-will-unmount)

  :render
  (fn [& args]
    (apply (partial log-args :render) args)
    (let [this (first args)]
      (swap! render-count inc)
      [:div "I am an apple."
       (when-not (:eaten (view/state this))
         [:p {:ref   "apple-statement-of-courage"
              :style {:font-weight "bold"}} " ...and I am brave and alive."])])))


;; a heavily logged component

(def util js/React.addons.TestUtils)
(def init-props {:color "red"})
(def init-child [:div {:style {:width         100
                               :height        100
                               :background    "red"
                               :border-radius 100}}])

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))


(deftest basic
  (binding [re-view.core/*use-render-loop* false]
    (let [el (append-el)
          render-to-dom #(js/ReactDOM.render (apple %1 %2) el)
          c (render-to-dom init-props init-child)]

      (testing "initial state"
        (is (= {:eaten? false} (view/state c)))
        (is (= 1 @render-count))
        (is (= "red" (get-in @lifecycle-log [:get-initial-state :props :color]))
            "Read props from GetInitialState")
        (is (= "red" (:color (view/props c)))
            "Read props"))

      (testing "update state"

        ;; Update State
        (view/update-state! c update :eaten? not)
        (is (true? (:eaten? (view/state c)))
            "State has changed")
        (is (= 2 @render-count)
            "Component was rendered"))

      (testing "render with new props"

        (view/render-component c {:color "green"})
        (is (= "green" (:color (view/props c)))
            "Update Props")
        (is (= 3 @render-count)
            "Force rendered"))


      (testing "children"

        (view/render-component c {} [:div "div"])
        (is (= 1 (count (view/children c)))
            "Has one child")
        (is (= :div (ffirst (view/children c)))
            "Read child")

        (view/render-component c {} [:p "Paragraph"])
        (is (= :p (ffirst (view/children c)))
            "New child - force render")

        (render-to-dom nil [:span "Span"])
        (is (= :span (ffirst (view/children c)))
            "New child - normal render"))


      (testing "refs"
        (is (= "bold" (-> c
                          (view/react-ref "apple-statement-of-courage")
                          .-style
                          .-fontWeight))
            "Read react ref")))))

(defn validate-args
  [log [initial-state before-state after-state] [initial-props before-props after-props]]

  (are [method-name val]
    (= val (rest (get-in log [method-name :args])))

    :get-initial-state [initial-props]

    :component-will-mount [initial-props initial-state]

    :component-did-mount [initial-props initial-state]

    :component-will-receive-props [after-props before-props]

    :should-component-update [after-props after-state before-props before-state]

    :component-will-update [after-props after-state before-props before-state]

    :component-did-update [before-props before-state after-props after-state]

    ;:component-will-unmount [after-props after-state]

    :render [after-props after-state]))



(deftest lifecycle-transitions
  (binding [re-view.core/*use-render-loop* false]
    (let [el (js/document.body.appendChild (doto (js/document.createElement "div")
                                             (.setAttribute "id" "apple")))
          render #(js/ReactDOM.render (apple %1 nil) el)
          initial-props {:color "purple"}
          this (render initial-props)]


      (testing "prop transitions"

        (render {:color "pink"})
        (render {:color "blue"})

        (validate-args @lifecycle-log
                       [initial-state initial-state initial-state]
                       [initial-props {:color "pink"} {:color "blue"}])

        (view/render-component this {:color "yellow"})
        (view/render-component this {:color "mink"})

        (validate-args @lifecycle-log
                       [initial-state initial-state initial-state]
                       [initial-props
                        {:color "yellow"}
                        {:color "mink"}])

        (view/render-component this {:color "fox"})

        (validate-args @lifecycle-log
                       [initial-state initial-state initial-state]
                       [initial-props
                        {:color "mink"}
                        {:color "fox"}])


        ;; Force-render followed by normal render

        (render {:color "bear"})

        (validate-args @lifecycle-log
                       [initial-state initial-state initial-state]
                       [initial-props
                        {:color "fox"}
                        {:color "bear"}]))

      (testing "state transition"

        (view/set-state! this {:shiny? false})

        (is (false? (-> this view/state :shiny?)))

        (view/update-state! this update :shiny? not)

        (validate-args @lifecycle-log
                       [initial-state
                        {:shiny? false}
                        {:shiny? true}]
                       [initial-props
                        {:color "fox"}
                        {:color "bear"}])

        (render {:color "violet"})
        (is (= (view/state this)
               (view/prev-state this))
            "After a component lifecycle, prev-state and state are the same.")))))


;; test react-key
;; test expand-props
;; - should this be called every time the component is rendered?
;;   can we cache anything?