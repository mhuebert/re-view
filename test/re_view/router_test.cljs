(ns re-view.router-test
  (:require [cljs.test :refer [deftest is are testing]]
            [re-db.d :as d]
            [re-view.routing :as r]
            [cljs.core.match :refer-macros [match]]
            [re-view.core :as view :refer [defview]]))

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))
(def log (atom []))

(defonce _ (r/on-location-change #(d/transact! [[:db/add ::state :route (:tokens %)]])))

(defview index []
  (swap! log conj [:index])
  [:div])

(defview not-found []
  (swap! log conj [:not-found])
  [:div])

(defview page [{props :view/props}]
  (swap! log conj [:page props])
  [:div])

(defview main [_]
  (match (d/get ::state :route)
         [] (index)
         ["page" page-id] (page {:page-id page-id})
         :else (not-found)))

(deftest routing-test
  (testing "Basic routing"
    (let [dom-el (append-el)
          render #(view/render-to-node % dom-el)]

      ;; set initial route to root
      (r/nav! "/")
      (view/flush!)

      ;; first render
      (render (main))

      ;; route changes should trigger re-render
      (r/nav! "/jkdljfsljflskdjflksjls--obviously-this-route-does-not-exist")
      (view/flush!)
      (r/nav! "/page/1001")
      (view/flush!)

      (is (= @log [[:index]
                   [:not-found]
                   [:page {:page-id "1001"}]])
          "Router render on pushstate change")

      (render (main {:db/id 2002}))

      (is (= (last @log) [:page {:page-id "1001"}])
          "Router params are partially applied to view"))))