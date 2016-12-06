(ns re-view.router-test
  (:require [cljs.test :refer [deftest is are testing]]
            [re-db.d :as d]
            [re-view.routing :as r :refer [router]]
            [re-view.core :as view :refer [defview]]))

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))
(def log (atom []))

(defonce _ (r/on-route #(d/transact! [[:db/add ::state :route (r/get-token)]])))

(defview index
  (fn []
    (swap! log conj [:index])
    [:div]))

(defview not-found
  (fn []
    (swap! log conj [:not-found])
    [:div]))

(defview page
  (fn [{props :props}]
    (swap! log conj [:page props])
    [:div]))

(defview main
  (fn [_]
    (r/router (d/get ::state :route)
              "/" index
              "/page/:page-id" page
              not-found)))

(deftest routing-test
  (testing "Basic routing"
    (binding [re-view.core/*use-render-loop* false]
      (let [dom-el (append-el)
            render-to-dom #(js/ReactDOM.render % dom-el)]

        ;; set initial route to root
        (r/nav! "/")

        ;; first render
        (render-to-dom (main))

        ;; route changes should trigger re-render
        (r/nav! "/jkdljfsljflskdjflksjls--obviously-this-route-does-not-exist")
        (r/nav! "/page/1001")

        (is (= @log [[:index]
                     [:not-found]
                     [:page {:page-id "1001"}]])
            "Router render on pushstate change")

        (render-to-dom (main {:db/id 2002}))

        (is (= (last @log) [:page {:page-id "1001"}])
            "Router params are partially applied to view")))))