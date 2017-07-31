(ns re-view-material.example
  (:require [re-view.core :as v :refer-macros [defview view]]
            [re-view-material.core :as ui]
            [re-view-material.ext :as ui-ext]
            [re-view-material.persisted.core :as ui-fire]
            [re-view-material.util :as util]
            [re-view.hoc :as hoc]
            [re-view.example.helpers :as h :refer [atom-as]]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [re-view-hiccup.core :as hiccup]
            [re-view-material.icons :as icons]))

(def examples-data
  [{:component ui/Button
    :prop-atom (atom [{:icon   icons/FormatQuote
                       :raised true
                       :label  "Button"
                       :color  :accent}])}
   {:component ui/Checkbox
    :prop-atom (atom-as example-props [{:id        "checkbox"
                                        :checked   false
                                        :label     "Check me"
                                        :disabled  false
                                        :dense     false
                                        :align-end false
                                        :rtl       false
                                        :value     "enabled"
                                        :on-change #(swap! example-props assoc-in [0 :checked] (.. % -target -checked))}])}
   (let [prop-atom (atom nil)]
     {:component      ui/Dialog
      :wrap-component #(do ui/DialogWithTrigger)
      :prop-atom      (atom [{:content-header "Sample dialog"
                              :label-accept   "Okay"
                              :label-cancel   "Cancel"
                              :on-cancel      #(println "Canceled")
                              :on-accept      #(println "Accepted")}
                             (ui/Button {:label  "Show Dialog"
                                         :raised true})
                             "Description of dialog"])})
   {:component ui/Switch
    :prop-atom (atom-as example-props [{:id        "switch"
                                        :disabled  false
                                        :value     "enabled"
                                        :checked   false
                                        :on-change #(swap! example-props assoc-in [0 :checked] (.. % -target -checked))

                                        }])}
   {:component ui/Text
    :prop-atom (atom-as example-props [{:value                ""
                                        :on-change            #(swap! example-props assoc-in [0 :value] (.. % -target -value))
                                        :label                "Your Name"
                                        :placeholder          "John Doe"
                                        :id                   "name"
                                        :help-text-persistent false
                                        :hint                 "You can omit your middle name(s)"}])}

   {:component ui/ToolbarWithContent
    :wrap      #(hoc/Frame {:height 350
                          :class  "bg-light-blue"} %)
    :prop-atom (atom [{:waterfall true
                       :fixed     :lastrow-only
                       :flexible  :default-behavior}
                      (ui/ToolbarRow
                        (ui/ToolbarSection {:align :start}
                                           [:a.mr3 {:href "/"} icons/Menu]
                                           (ui/ToolbarTitle "Row 1")))
                      [:.tc.pa3 "Lower element"
                       (map (fn [i] [:.pa3 (inc i)]) (range 10))]])}

   {:component ui/ListItem
    :prop-atom (atom [{:key            1
                       :text-primary   "List Item 1"
                       :text-secondary "Secondary text"
                       :detail-start   icons/Code}])
    :wrap      #(ui/List %)}
   ;; TODO wait for this merge: https://github.com/material-components/material-components-web/pull/530/files
   ;; then drawer has :open?, :onOpen, and :onClose callbacks.



   {:component ui/PermanentDrawer
    :wrap      #(do [:.shadow-4.dib %])
    :prop-atom (atom [{} (ui/List (ui/ListItem {:text-primary "Menu item 1"
                                                :ripple       true})
                                  (ui/ListItem {:text-primary "Menu item 2"
                                                :ripple       true}))])}

   {:component ui/Select
    :prop-atom (atom-as example-props [{:value     "2"
                                        :on-change #(swap! example-props assoc-in [0 :value] (.. % -target -value))}
                                       [:option {:value "Apples"} "Apples"]
                                       [:option {:value "Oranges"} "Oranges"]
                                       [:option {:value "Bananas"} "Bananas"]])}

   {:component      ui/SimpleMenu
    :wrap-component #(do ui/SimpleMenuWithTrigger)
    :prop-atom      (atom [{}
                           (ui/SimpleMenuItem {:text-primary "Menu item 1"
                                               :ripple       true})
                           (ui/SimpleMenuItem {:text-primary "Menu item 2"
                                               :ripple       true})])}

   {:component      ui/TemporaryDrawer
    :wrap-component #(do ui/TemporaryDrawerWithTrigger)
    :prop-atom      (atom [{}
                           (ui/Button {:key    "button"
                                       :raised true
                                       :label  "Show Drawer"})
                           (ui/List (ui/ListItem {:text-primary "Menu item 1"
                                                  :href         "/"
                                                  :ripple       true})
                                    (ui/ListItem {:text-primary "Menu item 2"
                                                  :ripple       true})
                                    (ui/ListItem {:text-primary (ui/SimpleMenu
                                                                  (ui/Button {:label "Show Menu"})
                                                                  (ui/SimpleMenuItem {:text-primary "Item"}))}))])}

   ])