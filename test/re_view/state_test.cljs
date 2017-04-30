(ns re-view.state-test
  (:require
    [cljs.test :refer [deftest is are testing]]
    [re-db.d :as d]
    [re-view.core :as v :refer [defview]]))

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))

(deftest state-atom
  (testing "React to local state atom"

    (let [log (atom [])
          local-state (atom nil)
          el (append-el)
          view (v/view
                 {:initial-state 0
                  :did-mount     #(reset! local-state (:view/state %))}
                 [{:keys [view/state]}]
                 (swap! log conj @state)
                 [:div "hello"])
          render #(v/render-to-element (view) el)]

      (render)
      (is (= @log [0]))
      (swap! @local-state inc)
      (v/flush!)

      (is (= @log [0 1]))
      (reset! @local-state "x")
      (v/flush!)
      (is (= @log [0 1 "x"])))))

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
          render #(v/render-to-element (view {:db/id %}) el)]

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

