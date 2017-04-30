(ns re-view-router.core-test
  (:require [cljsjs.react.dom]
            [goog.dom :as gdom]
            [cljs.test :refer [deftest is are testing]]
            [re-db.d :as d]
            [re-view-routing.core :as routing]
            [cljs.core.match :refer-macros [match]]
            [re-view.core :as view :refer [defview]]))

(enable-console-print!)

;; log segments -- tests usage of routing by itself
(def segments-log (atom []))

;; log view renders -- tests usage of routing in combination with re-view and re-db
(def view-log (atom []))

(def test-element (let [child (gdom/createDom "div")]
                    (gdom/appendChild (.-body js/document) child)
                    child))

(defonce _
         ;; set up listener for route changes
         (routing/on-location-change (fn [{:keys [segments] :as location}]
                                       ;; log segments
                                       (swap! segments-log conj segments)
                                       ;; write location to re-db
                                       ;; (triggers render of views that reference this data)
                                       (d/transact! [(assoc location :db/id :router/location)]))))

(defview index
         [{:keys [view/props]}]
         ;; side effect: log view name & props
         (swap! view-log conj [:index props])
         [:div])

(defview not-found [{:keys [view/props]}]
         (swap! view-log conj [:not-found props])
         [:div])

(defview page [{:keys [view/props]}]
         (swap! view-log conj [:page props])
         [:div])

(defview root [_]
         ;; using core.match to pattern-match on location segments.
         (match (d/get :router/location :segments)
                [] (index)
                ["page" page-id] (page {:page-id page-id})
                :else (not-found)))


(deftest routing-test
  (testing "Basic routing"
    (let [render #(view/render-to-element % test-element)]

      ;; set initial route to root
      (routing/nav! "/")
      (view/flush!)

      ;; first render
      (render (root))

      ;; route changes should trigger re-render
      (routing/nav! "/non-existing-route")
      (view/flush!)
      (routing/nav! "/page/1")
      (view/flush!)
      (routing/nav! "/page/2")
      (view/flush!)

      (is (= @segments-log [[]
                            ["non-existing-route"]
                            ["page" "1"]
                            ["page" "2"]]))

      (is (= @view-log [[:index nil]
                        [:not-found nil]
                        [:page {:page-id "1"}]
                        [:page {:page-id "2"}]])
          "Views render on route change"))))