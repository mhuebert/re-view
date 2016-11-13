(ns re-view.router-test
  (:require [cljs.test :refer-macros [deftest is are testing]]
            [re-view.routing :as r :refer [router]]
            [re-view.core :as view :refer-macros [defcomponent]]))




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
  (fn [_ props]
    (swap! log conj [:page props])
    [:div]))

(defcomponent main
  :subscriptions {:main-view (router "/" index
                                     "/404" not-found
                                     "/page/:page-id" page)}
  :render
  (fn [_ props {:keys [main-view]}]
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
      (r/nav! "/404")
      (r/nav! "/page/1001")

      (is (= @log [[:index]
                   [:not-found]
                   [:page {:page-id "1001"}]])
          "Router render on pushstate change")

      (render-to-dom (main {:id 2002}))

      (is (= (last @log) [:page {:page-id "1001"
                                 :id      2002}])
          "Router params are partially applied to view"))))