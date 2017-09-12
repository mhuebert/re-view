(ns re-view.state-test
  (:require
    [cljs.test :refer [deftest is are testing]]
    [re-db.d :as d]
    [re-view.core :as v :refer [defview]]))

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))

(deftest local-state
  (let [el (append-el)
        render #(v/render-to-dom % el)]
    (testing "atom from initial-state"
      (let [log (atom [])
            local-state (atom nil)
            view (v/view
                   {:view/initial-state 0
                    :view/did-mount     #(reset! local-state (:view/state %))}
                   [{:keys [view/state]}]
                   (swap! log conj @state)
                   [:div "hello"])]

        (render (view))
        (is (= @log [0]))
        (swap! @local-state inc)
        (v/flush!)

        (is (= @log [0 1]))
        (reset! @local-state "x")
        (v/flush!)
        (is (= @log [0 1 "x"]))))

    (testing "passing an atom as state"
      (let [the-atom (atom 111)
            log (atom [])
            view (v/view
                   {:view/state the-atom}
                   [this]
                   (swap! log conj @(:view/state this))
                   nil)]
        (render (view))
        (v/flush!)
        (swap! the-atom inc)
        (v/flush!)
        (is (= @log [111 112]))))

    (testing "passing an atom as :view/state prop"
      (let [the-atom (atom 111)
            log (atom [])
            view (v/view
                   [this]
                   (swap! log conj @(:view/state this))
                   nil)]
        (render (view {:view/state the-atom}))
        (v/flush!)
        (swap! the-atom inc)
        (v/flush!)
        (is (= @log [111 112]))))))



(deftest re-db

  (testing "React to global state (re-db)"

    (d/transact! [{:db/id      1
                   :name       "Herbert"
                   :occupation "Chimney Sweep"}])

    (let [log (atom [])
          el (append-el)
          view (v/view [{:keys [db/id]}]
                       (swap! log conj (d/get id :name))
                       [:div "hello"])
          render #(v/render-to-dom (view {:db/id %}) el)]

      (render 1)
      (is (= 1 (count @log)))

      (d/transact! [[:db/add 1 :name "Frank"]])
      (v/flush!)

      (is (= 2 (count @log)))
      (is (= "Frank" (last @log)))

      (d/transact! [{:db/id 2 :name "Gertrude"}])
      (v/flush!)

      (is (= 2 (count @log)))

      (render 2)

      (is (= 3 (count @log)))
      (is (= "Gertrude" (last @log))))))

