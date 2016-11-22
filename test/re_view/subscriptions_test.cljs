(ns re-view.subscriptions-test
  (:require [cljs.test :refer [deftest is are testing]]
            [re-db.d :as d]
            [re-view.core :as v :refer [defcomponent]]
            [re-view.subscriptions :as subs :include-macros true]))


(def append-el #(js/document.body.appendChild (js/document.createElement "div")))

(def log (atom []))

(defcomponent test-c
  :subscriptions {:name (subs/db [this] (d/get (get-in this [:props :id]) :name))}
  :render
  (fn [this]
    (swap! log conj (get-in this [:state :name]))
    [:div "hello"]))

(d/transact! [{:id         1
               :name       "Herbert"
               :occupation "Chimney Sweep"}])

(deftest reactive-sub

  (testing "Reactive subscription"

    (binding [re-view.core/*use-render-loop* false]
      (let [el (append-el)
            render-to-dom #(js/ReactDOM.render (test-c {:id %}) el)]

        (render-to-dom 1)
        (is (= 1 (count @log)))

        (d/transact! [[:db/add 1 :name "Frank"]])

        (is (= 2 (count @log)))
        (is (= "Frank" (last @log)))

        (d/transact! [{:id 2 :name "Gertrude"}])

        (is (= 2 (count @log)))

        (render-to-dom 2)

        (is (= 3 (count @log)))
        (is (= "Gertrude" (last @log)))))))