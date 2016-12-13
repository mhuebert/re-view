(ns re-view.subscriptions-test
  (:require [cljs.test :refer [deftest is are testing]]
            [re-db.d :as d]
            [re-view.core :as v :refer [defview]]
            [re-view.subscriptions :as subs :include-macros true]))


(def append-el #(js/document.body.appendChild (js/document.createElement "div")))

(def log (atom []))

(defview test-c
         (fn [{{:keys [db/id]} :props}]
           (swap! log conj (d/get id :name))
           [:div "hello"]))

(d/transact! [{:db/id         1
               :name       "Herbert"
               :occupation "Chimney Sweep"}])

(deftest reactive-sub

  (testing "Reactive subscription"

    (let [el (append-el)
          render-to-dom #(js/ReactDOM.render (test-c {:db/id %}) el)]

      (render-to-dom 1)
      (is (= 1 (count @log)))

      (d/transact! [[:db/add 1 :name "Frank"]])
      (v/flush!)

      (is (= 2 (count @log)))
      (is (= "Frank" (last @log)))

      (d/transact! [{:db/id 2 :name "Gertrude"}])
      (v/flush!)

      (is (= 2 (count @log)))

      (render-to-dom 2)

      (is (= 3 (count @log)))
      (is (= "Gertrude" (last @log))))))