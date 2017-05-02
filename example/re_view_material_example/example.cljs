(ns re-view-material-example.example
  (:require [re-view.core :as v :refer-macros [defview view]]
            [re-view-material.core :as ui]
            [re-view-material-persisted.core :as ui-fire]
            [re-view-material.util :as util]
            [re-view-example.helpers :as h :refer [atom-as]]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [re-view-material.icons :as icons]))

(def examples-data
  [{:component ui/Button
    :prop-atom (atom {:id      "button"
                      :icon    icons/FormatQuote
                      :label   "Button"
                      :primary true
                      :accent  false
                      :raised  true
                      :compact false
                      :dense   false})}
   {:component ui/Checkbox
    :prop-atom (atom-as example-props {:id        "checkbox"
                                       :checked   false
                                       :label     "Check me"
                                       :disabled  false
                                       :dense     false

                                       :align-end false
                                       :rtl       false
                                       :value     "enabled"
                                       :on-change #(swap! example-props assoc :checked (.. % -target -checked))})}
   {:component ui/Switch
    :prop-atom (atom-as example-props {:id        "switch"
                                       :disabled  false
                                       :value     "enabled"
                                       :checked   false
                                       :on-change #(swap! example-props assoc :checked (.. % -target -checked))

                                       :align-end true
                                       :rtl       false
                                       :label     "Switch me"})}
   {:component ui/Text
    :prop-atom (atom-as example-props {:value               ""
                                       :dense               false
                                       :disabled            false
                                       :multi-line           false
                                       :full-width          false
                                       :helpText-persistent true
                                       :on-change           #(swap! example-props assoc :value (.. % -target -value))
                                       :label               "Your Name"
                                       :placeholder         "John Doe"
                                       :id                  "name"
                                       :hint                "You can omit your middle name(s)"
                                       :on-save             (fn [] (println :on-save!))
                                       :error               [""]})}

   {:component ui/ListItem
    :prop-atom (atom-as example-props {:key            1
                                       :text-primary   "List Item 1"
                                       :text-secondary "With secondary text, detail-start, and ripple."
                                       :ripple         true
                                       :detail-start   icons/Code})
    :wrap      #(ui/List %)}
   ;; TODO wait for this merge: https://github.com/material-components/material-components-web/pull/530/files
   ;; then drawer has :open?, :onOpen, and :onClose callbacks.

   {:component   ui/TemporaryDrawer
    :custom-view (view [{:keys [view/state]}]
                       [:.dib

                        (ui/TemporaryDrawer
                          {:key "drawer"
                           :ref #(when % (swap! state assoc :drawer %))}
                          (ui/List (ui/ListItem {:text-primary "Menu item 1"
                                                 :href         "/"
                                                 :ripple       true})
                                   (ui/ListItem {:text-primary "Menu item 2"
                                                 :on-click     #(println :clicked)
                                                 :ripple       true})))
                        (ui/Button {:key      "button"
                                    :raised   true
                                    :label    "Show Drawer"
                                    :on-click #(.open (:drawer @state))})])}

   {:component ui/PermanentDrawer
    :wrap      #(do [:.shadow-4.dib %])
    :children  (list (ui/List (ui/ListItem {:text-primary "Menu item 1"
                                            :ripple       true})
                              (ui/ListItem {:text-primary "Menu item 2"
                                            :ripple       true})))}])