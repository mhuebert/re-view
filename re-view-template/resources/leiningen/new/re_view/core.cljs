(ns {{name}}.core
  (:require
    [re-view.core :as v :refer [defview]]
    [{{name}}.examples :as examples]))

(enable-console-print!)

(defview text
         "A simple view element with text, using the :name prop."
         [this]
         [:div {:style {:font-size  30
                        :text-align "center"}}
          "Welcome to " (:name this) "!"])

(defview counter
         "Example of using the :view/state atom to keep local state."
         [this]
         [:div
          {:on-click #(swap! (:view/state this) inc)
           :style    {:padding    10
                      :background "#eee"
                      :cursor     "pointer"}}
          "I have been clicked " (or @(:view/state this) 0) " times."])


(def spacer [:div {:style {:height 10}}])

(defview layout [this]
         [:div
          {:style {:max-width 300
                   :margin    "50px auto"
                   :font-size 16}}
          (text {:name "{{name}}"})
          spacer
          (counter)
          spacer
          (examples/custom-cursor)
          spacer
          (examples/todo-list)])

(v/render-to-dom (layout) "{{name}}")
