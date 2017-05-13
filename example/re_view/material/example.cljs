(ns re-view.material.example
  (:require [re-view.core :as v :refer-macros [defview view]]
            [re-view.material :as ui]
            [re-view.material.ext :as ui-ext]
            [re-view.material.persisted.core :as ui-fire]
            [re-view.material.util :as util]
            [re-view.example.helpers :as h :refer [atom-as]]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [re-view.hiccup :as hiccup]
            [re-view.material.icons :as icons]))

(defview Frame
  {:did-mount    (fn [this content]
                   (.renderFrame this content))
   :will-unmount (fn [this]
                   (.unmountComponentAtNode js/ReactDOM (-> (v/dom-node this)
                                                            (aget "contentDocument" "body"))))
   :render-frame (fn [this content]
                   (v/render-to-element (hiccup/element
                                          [:div
                                           [:link {:type "text/css"
                                                   :rel  "stylesheet"
                                                   :href "/app.css"}]
                                           content]) (-> (v/dom-node this)
                                                         (aget "contentDocument" "body"))))}
  [{:keys [view/props]}]
  [:iframe.bn.shadow-2 props])

(def examples-data
  [{:component ui/ToolbarWithContent
    :wrap      #(Frame {:height 400} %)
    :prop-atom (atom [{:waterfall?                 false
                       :fixed?                     false
                       :fixed-lastrow-only?        false
                       :flexible?                  true
                       :flexible-default-behavior? true
                       :rtl                        false}
                      (list (ui/ToolbarRow
                              (ui/ToolbarSection nil
                                                 (ui/ToolbarTitle "Row 1")))
                            (ui/ToolbarRow
                              (ui/ToolbarSection nil
                                                 icons/FormatSize)))
                      [:.bg-light-blue.flex.items-center.tc {:style {:height 500}} "Lower element"]])}
   {:component ui/Button
    :prop-atom (atom [{:icon    icons/FormatQuote
                       :label   "Button"
                       :primary true
                       :accent  false
                       :raised  true
                       :compact false
                       :dense   false}])}
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
   (let [prop-atom (atom)]
     {:component      ui/Dialog
      :wrap-component #(do ui/DialogWithTrigger)
      :prop-atom      (atom [{:content/header "Sample dialog"
                              :label/accept   "Okay"
                              :label/cancel   "Cancel"
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

                                        ;:align-end true
                                        ;:rtl       false
                                        ;:label     "Switch me"
                                        }])}
   {:component ui/Text
    :prop-atom (atom-as example-props [{:value                ""
                                        :dense                false
                                        :disabled             false
                                        :multi-line           false
                                        :full-width           false
                                        :help-text-persistent true
                                        :on-change            #(swap! example-props assoc-in [0 :value] (.. % -target -value))
                                        :label                "Your Name"
                                        :placeholder          "John Doe"
                                        :id                   "name"
                                        :hint                 "You can omit your middle name(s)"
                                        :on-save              (fn [] (println :on-save!))
                                        :error                [""]}])}

   {:component ui/ListItem
    :prop-atom (atom [{:key            1
                       :text/primary   "List Item 1"
                       :text/secondary "With secondary text, detail-start, and ripple."
                       :ripple         true
                       :detail/start   icons/Code}])
    :wrap      #(ui/List %)}
   ;; TODO wait for this merge: https://github.com/material-components/material-components-web/pull/530/files
   ;; then drawer has :open?, :onOpen, and :onClose callbacks.



   {:component ui/PermanentDrawer
    :wrap      #(do [:.shadow-4.dib %])
    :prop-atom (atom [{} (ui/List (ui/ListItem {:text/primary "Menu item 1"
                                                :ripple       true})
                                  (ui/ListItem {:text/primary "Menu item 2"
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
                           (ui/SimpleMenuItem {:text/primary "Menu item 1"
                                               :ripple       true})
                           (ui/SimpleMenuItem {:text/primary "Menu item 2"
                                               :ripple       true})])}

   {:component      ui/TemporaryDrawer
    :wrap-component #(do ui/TemporaryDrawerWithTrigger)
    :prop-atom      (atom [{}
                           (ui/Button {:key    "button"
                                       :raised true
                                       :label  "Show Drawer"})
                           (ui/List (ui/ListItem {:text/primary "Menu item 1"
                                                  :href         "/"
                                                  :ripple       true})
                                    (ui/ListItem {:text/primary "Menu item 2"
                                                  :ripple       true})
                                    (ui/ListItem {:text/primary (ui/SimpleMenu
                                                                  (ui/Button {:label "Show Menu"})
                                                                  (ui/SimpleMenuItem {:text/primary "Item"}))}))])}

   ])