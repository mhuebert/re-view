(ns {{name}}.core
  (:require
    [re-view.core :as v :refer [defview]]
    [cljsjs.react]
    [cljsjs.react.dom]))


(enable-console-print!)

(defview example-text [this]
         [:div {:style {:margin 10}}
          "Hello, and welcome to " (:name this) "!"])

(defview example-counter [this]
         [:div
          {:on-click #(swap! (:view/state this) inc)
           :style    {:padding    10
                      :margin     10
                      :background "#eee"
                      :cursor     "pointer"}}
          "I have been clicked " (or @(:view/state this) 0) " times."])

(defview example-local-state
         {:view/initial-state    {:mouse-position [150 50]}
          :view/did-mount        (fn [this]
                                   (let [element (v/dom-node this)
                                         position (.getBoundingClientRect element)]
                                     (swap! (:view/state this) assoc :base-position [(.-left position) (.-top position)])))
          :update-mouse-position (fn [{:keys [view/state]} e]
                                   (let [[left-offset top-offset] (:base-position @state)]
                                     (swap! state assoc :mouse-position [(- (.-clientX e) left-offset)
                                                                         (- (.-clientY e) top-offset)])))}
         [this]
         (let [{[mouse-left mouse-top] :mouse-position} @(:view/state this)]
           [:div
            {:on-mouse-move #(.updateMousePosition this %)
             :style         {:background-color "#4bc57e"
                             :position         "relative"
                             :width            300
                             :height           100
                             :margin           10
                             :cursor           "none"
                             :overflow         "hidden"}}
            [:div {:style {:position  "absolute"
                           :left      (- mouse-left 25)
                           :top       (- mouse-top 25)
                           :transform "scale(2)"
                           :width     0
                           :height    0}} "😀"]]))


(defview layout [this]
         [:div
          (example-text)
          (example-counter)
          (example-local-state)])

(v/render-to-dom (layout {:name "{{name}}"}) "{{name}}")
