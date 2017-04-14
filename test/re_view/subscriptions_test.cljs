(ns re-view.subscriptions-test
  (:require
    [cljs.test :refer [deftest is are testing]]
    [re-db.d :as d]
    [re-view.core :as v :refer [defview]]
    [re-view.subscriptions :as subs :include-macros true]))


(def append-el #(js/document.body.appendChild (js/document.createElement "div")))

(def log (atom []))

(defview test-c
  [{:keys [db/id]}]
  (swap! log conj (d/get id :name))
  [:div "hello"])

(d/transact! [{:db/id      1
               :name       "Herbert"
               :occupation "Chimney Sweep"}])

(deftest reactive-sub

  (testing "Reactive subscription"

    (let [el (append-el)
          render #(v/render-to-node (test-c {:db/id %}) el)]

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