(ns re-view.routing-test
  (:require [goog.dom :as gdom]
            [cljs.test :refer [deftest is are testing]]
            [re-db.d :as d]
            [re-view.routing :as routing]
            [cljs.core.match :refer-macros [match]]
            [re-view.core :as v :refer [defview]]))

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
         (routing/listen (fn [{:keys [segments] :as location}]
                           ;; log segments
                           (swap! segments-log conj segments)
                           ;; write location to re-db
                           ;; (triggers render of views that reference this data)
                           (d/transact! [(assoc location :db/id :router/location)]))))

(defview index
  [this]
  (swap! view-log conj :index)
  [:div])

(defview not-found [this]
  (swap! view-log conj :not-found)
  [:div])

(defview page [{:keys [page-id]}]
  (swap! view-log conj [:page page-id])
  [:div])

(defview root [_]
  ;; using core.match to pattern-match on location segments.
  (match (d/get :router/location :segments)
         [] (index)
         ["page" page-id] (page {:page-id page-id})
         :else (not-found)))


(deftest routing-test
  (testing "Basic routing"
    (let [render #(v/render-to-dom % test-element)]

      ;; set initial route to root
      (routing/nav! "/")
      ;; first render
      (render (root))

      ;; route changes should trigger re-render
      (routing/nav! "/non-existing-route")
      (v/flush!)
      (routing/nav! "/page/1")
      (v/flush!)
      (routing/nav! "/page/2")
      (v/flush!)

      (is (= (drop 1 @segments-log) '([]
                                       ["non-existing-route"]
                                       ["page" "1"]
                                       ["page" "2"])))

      (is (= @view-log [:index
                        :not-found
                        [:page "1"]
                        [:page "2"]])
          "Views render on route change"))))