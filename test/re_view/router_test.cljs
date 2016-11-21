(ns re-view.router-test
  (:require [cljs.test :refer [deftest is are testing]]
            [re-view.routing :as r :refer [router]]
            [re-view.core :as view :refer [defcomponent]]))

(def append-el #(js/document.body.appendChild (js/document.createElement "div")))
(def log (atom []))

(defcomponent index
  (fn []
    (swap! log conj [:index])
    [:div]))

(defcomponent not-found
  (fn []
    (swap! log conj [:not-found])
    [:div]))

(defcomponent page
  (fn [{props :props}]
    (swap! log conj [:page props])
    [:div]))

(defcomponent main
  :subscriptions {:main-view (router "/" index
                                     "/page/:page-id" page
                                     not-found)}
  :render
  (fn [{{:keys [main-view]} :state
        props :props}]
    (main-view props)))

(deftest routing-test

  (testing "Basic routing"
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

      (render-to-dom (main {:id 2002}))

      (is (= (last @log) [:page {:page-id "1001"
                                 :id      2002}])
          "Router params are partially applied to view"))))