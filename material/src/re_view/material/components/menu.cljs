(ns re-view.material.components.menu
  (:require [re-view.core :as v]

            [re-view.material.mdc :as mdc]
            [re-view.material.util :as util]

            ["@material/menu/simple/foundation" :as foundation]
            ["@material/menu/util" :refer [getTransformPropertyName]]

            [re-view.material.components.list :refer [ListItem]]

            [goog.object :as gobj]
            [goog.dom :as gdom]
            [goog.dom.classes :as classes]
            [re-view.material.ext :as ext]))

(defn index-of
  "Index of x in js-coll, where js-coll is an array-like object that does not implement .indexOf (eg. HTMLCollection)"
  [^js js-coll x]
  (let [length (.-length js-coll)]
    (loop [i 0]
      (cond (= i length) -1
            (= x (aget js-coll i)) i
            :else (recur (inc i))))))

(def transform-property (getTransformPropertyName mdc/Window))

(mdc/defadapter SimpleMenuAdapter
  foundation/default
  [{:keys [view/state] :as component}]
  (let [^js root (v/dom-node component)
        get-container #(util/find-node root (fn [el] (classes/has el "mdc-simple-menu__items")))
        ^js menuItemContainer (get-container)]
    {:menuItemContainer                menuItemContainer
     :hasNecessaryDom                  #(do true)
     :getInnerDimensions               #(do #js {"width"  (.-offsetWidth menuItemContainer)
                                                 "height" (.-offsetHeight menuItemContainer)})
     :hasAnchor                        #(this-as this (some-> (gobj/get this "root")
                                                              (gobj/get "parentElement")
                                                              (classes/has "mdc-menu-anchor")))
     :getAnchorDimensions              #(this-as this (some-> (gobj/get this "root")
                                                              (gobj/get "parentElement")
                                                              (.getBoundingClientRect)))
     :getWindowDimensions              #(do #js {"width"  (.-innerWidth mdc/Window)
                                                 "height" (.-innerHeight mdc/Window)})
     :setScale                         (fn [x y]
                                         (swap! state assoc-in [:mdc/root-styles transform-property] (str "scale(" x ", " y ")")))
     :setInnerScale                    (fn [x y]
                                         (swap! state assoc-in [:mdc/inner-styles transform-property] (str "scale(" x ", " y ")")))
     :getNumberOfItems                 #(aget (gdom/getChildren (get-container)) "length")
     :getYParamsForItemAtIndex         (fn [index]
                                         (let [^js child (aget (gdom/getChildren (get-container)) index)]
                                           #js {"top"    (.-offsetTop child)
                                                "height" (.-offsetHeight child)}))
     :setTransitionDelayForItemAtIndex (fn [index value]
                                         (let [^js style (-> (aget (gdom/getChildren (get-container)) index)
                                                             (gobj/get "style"))]
                                           (.setProperty style "transition-delay" value)))
     :getIndexForEventTarget           (fn [target]
                                         (index-of (gdom/getChildren (get-container)) target))
     :getAttributeForEventTarget       (fn [^js target attr]
                                         (.getAttribute target attr))
     :notifySelected                   (fn [evtData]
                                         (when-let [f (get component :on-selected)]
                                           (f evtData)))
     :notifyCancel                     (fn [evtData]
                                         (when-let [f (get component :on-cancel)]
                                           (f evtData)))
     :saveFocus                        #(this-as this (aset this "previousFocus" (.-activeElement mdc/Document)))
     :restoreFocus                     #(this-as this (when-let [^js prev-focus (aget this "previousFocus")]
                                                        (.focus prev-focus)))
     :isFocused                        #(= (.-activeElement mdc/Document) root)
     :focus                            #(.focus root)
     :getFocusedItemIndex              #(index-of (gdom/getChildren (get-container)) (.-activeElement mdc/Document))
     :focusItemAtIndex                 #(let [^js el (gobj/get (gdom/getChildren (get-container)) %)]
                                          (.focus el))
     :isRtl                            #(get component :rtl)
     :setTransformOrigin               #(swap! state assoc-in [:mdc/root-styles (str transform-property "-origin")] %)
     :setPosition                      (fn [pos]
                                         (swap! state update :mdc/root-styles merge (reduce (fn [m k]
                                                                                              (assoc m k (or (aget pos k) nil))) {} ["left" "right" "top" "bottom"])))
     :getAccurateTime                  #(.. js/window -performance (now))
     :registerBodyClickHandler         #(.addEventListener mdc/Body "click" %)
     :deregisterBodyClickHandler       #(.removeEventListener mdc/Body "click" %)}))

(v/defview SimpleMenu
  "Menus appear above all other in-app UI elements, and appear on top of the triggering element. [More](https://material.io/guidelines/components/menus.html#menus-behavior)"
  {:key               :id
   :spec/props        {:on-cancel   :Function
                       :on-selected :Function
                       :open-from   #{:bottom-right :bottom-left :top-right :top-left}}
   :spec/children     [:& :Element]
   :view/did-mount    (fn [this] (mdc/init this SimpleMenuAdapter))
   :view/will-unmount (fn [this] (mdc/destroy this SimpleMenuAdapter))
   :open              (fn [this] (.open (gobj/get this "mdcSimpleMenu")))
   :view/did-update   [(mdc/mdc-style-update :SimpleMenu :menuItemContainer)
                       (mdc/mdc-style-update :SimpleMenu :root)]}
  [{:keys [view/state view/props classes open-from] :as this} & items]
  [:.mdc-simple-menu (merge (v/pass-props this)
                            {:tab-index -1
                             :class     (when open-from (str "mdc-simple-menu--open-from-" (name open-from)))
                             :classes   (into classes (:mdc/SimpleMenu-classes @state))})
   (into [:.mdc-simple-menu__items.mdc-list
          {:role        "menu"
           :aria-hidden true}]
         items)])

(def SimpleMenuWithTrigger (ext/with-trigger SimpleMenu {:container-classes ["mdc-menu-anchor"]}))

(def SimpleMenuItem (v/partial ListItem
                               {:role      "menuitem"
                                :tab-index 0}))
