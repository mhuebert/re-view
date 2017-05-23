(ns re-view-material.mdc
  (:require [pack.mdc]
            [re-view.core :as v]
            [goog.dom :as gdom]
            [goog.dom.classes :as classes]
            [goog.dom.dataset :as dataset]
            [goog.events :as events]
            [goog.object :as gobj]
            [goog.style :as gstyle]
            [goog.functions :as gfn]
            [re-view-material.util :as util]
            [re-view.util :as v-util])
  (:require-macros [re-view-material.mdc :refer [defadapter]]))

(set! *warn-on-infer* true)

(def mdc (gobj/get js/window "mdc"))
(def event-name (gobj/get mdc "getCorrectEventName"))
(def supportsCssVariables (gobj/get mdc "supportsCssVariables"))
(def getMatchesProperty (gobj/get mdc "getMatchesProperty"))
(def getTransformPropertyName (gobj/get mdc "getTransformPropertyName"))
(def supportsCssCustomProperties (gobj/get mdc "supportsCssCustomProperties"))
(def createFocusTrapInstance (gobj/get mdc "createFocusTrapInstance"))
(def applyPassive (gobj/get mdc "applyPassive"))
(def remapEvent (gobj/get mdc "remapEvent"))

(def MatchesProperty (getMatchesProperty (.-prototype js/HTMLElement)))
(def conj-set (fnil conj #{}))
(def disj-set (fnil disj #{}))

(defn foundation-class
  "Look up foundation class by name on MDC global var."
  [name]
  (gobj/get mdc (str "MDC" name "Foundation")))

(def throttle (memoize gfn/throttle))

(defn init
  "Instantiate mdc foundations for a re-view component
   (should be called in componentDidMount)."
  [component & adapters]
  (doseq [{:keys [name adapter]} adapters]
    (let [^js/mdc.Foundation foundation (adapter component)]
      (aset component (str "mdc" name) foundation)
      (.init foundation))))

(defn destroy
  "Destroy mdc foundation instances for component (should be called in componentWillUnmount)."
  [component & adapters]
  (doseq [{:keys [name]} adapters]
    (let [^js/mdc.Foundation foundation (gobj/get component (str "mdc" name))]
      (when-let [onDestroy (aget foundation "adapter_" "onDestroy")]
        (onDestroy))
      (.destroy foundation))))

(defn current-target? [^js/Event e]
  (= (.-target e) (.-currentTarget e)))

(defn wrap-log [msg f]
  (fn [& args]
    (prn msg)
    (apply f args)))

(defn interaction-handler
  ([kind element event-type]
   (fn [handler]
     (this-as this
       (let [target (cond->> element
                             (string? element)
                             (gobj/get this))
             event (remapEvent event-type)]

         (case kind :listen (.addEventListener target event handler (applyPassive))
                    :unlisten (.removeEventListener target event handler))))))
  ([kind element]
   (fn [event-type handler]
     (this-as this
       (let [target (cond->> element
                             (string? element)
                             (gobj/get this))
             event-type (remapEvent event-type)]
         (case kind :listen (.addEventListener target event-type handler (applyPassive))
                    :unlisten (.removeEventListener target event-type handler)))))))

(defn adapter [component mdc-key]
  (aget component (str "mdc" (name mdc-key)) "adapter_"))

(defn element [adapter k]
  (aget adapter (name k)))

(defn styles-key [element-key]
  (keyword "mdc" (str (some-> element-key (name) (str "-")) "styles")))

(defn classes-key [element-key]
  (keyword "mdc" (str (some-> element-key (name) (str "-")) "classes")))

(defn style-handler
  ([mdc-key]
   (style-handler mdc-key :root))
  ([mdc-key element-key]
   (fn [attr val]
     (this-as adapter
       (util/add-styles (element adapter element-key) {attr val})
       (v/swap-silently! (aget adapter "state") assoc-in [(styles-key element-key) attr] val)))))

(defn mdc-style-update
  ([mdc-key]
   (mdc-style-update mdc-key "root"))
  ([mdc-key element-key]
   (fn [{:keys [view/state
                view/prev-state] :as this}]
     (let [target (element (adapter this mdc-key) element-key)
           style-key (styles-key element-key)]
       (util/add-styles target (get @state style-key) (get prev-state style-key))))))

#_(defn mdc-classes-update
    ([mdc-key]
     (mdc-classes-update mdc-key "root"))
    ([mdc-key element-key]
     (fn [{:keys [view/state] :as this}]
       (when-let [mdc-classes (seq (get @state (classes-key mdc-key)))]
         (let [target (element (adapter this mdc-key) element-key)]
           (doseq [class mdc-classes]
             (classes/add target class)))))))

(defn class-handler
  ([action] (class-handler action nil))
  ([action prefix]
   (let [f (case action :add conj-set :remove disj-set)]
     (fn [class-name]
       (this-as this
         (swap! (aget this "state") update (keyword "mdc" (str (aget this "name") (some-> prefix (str "-")) "-classes")) f class-name))))))

(def adapter-base
  {:addClass                         (class-handler :add)
   :removeClass                      (class-handler :remove)
   :hasClass                         #(this-as this
                                        (classes/has (gobj/get this "root") %))
   :registerInteractionHandler       (interaction-handler :listen "root")
   :deregisterInteractionHandler     (interaction-handler :unlisten "root")
   :registerDocumentKeydownHandler   (interaction-handler :listen js/document "keydown")
   :deregisterDocumentKeydownHandler (interaction-handler :unlisten js/document "keydown")
   :registerDocumentClickHandler     (interaction-handler :listen js/document "click")
   :deregisterDocumentClickHandler   (interaction-handler :unlisten js/document "click")
   :isRtl                            #(this-as this
                                        (let [^js/Element root (gobj/get this "root")
                                              ^js/CSSStyleDeclaration styles (js/getComputedStyle root)]
                                          (= "rtl" (.getPropertyValue styles "direction"))))})

(defn bind-adapter
  "Return methods that bind an adapter to a specific component instance"
  [{:keys [view/state] :as this}]
  (let [root-node (v/dom-node this)]
    {:root        root-node
     :nativeInput (util/find-tag root-node #"INPUT|TEXTAREA")
     :state       (get this :view/state)
     :component   this}))

(defn make-foundation
  "Extends adapter with base adapter methods, and wraps with Foundation class"
  [name foundation-class methods]
  (fn [this]
    (foundation-class. (->> (merge (bind-adapter this)
                                   adapter-base
                                   (if (fn? methods) (methods this) methods)
                                   {:name name})
                            (clj->js)))))

(defadapter Textfield []
  {:addClassToLabel               (class-handler :add "label")
   :removeClassFromLabel          (class-handler :remove "label")
   :addClassToHelptext            (class-handler :add "help")
   :removeClassFromHelptext       (class-handler :remove "help")
   :helptextHasClass              #(this-as this (contains? (:mdc/help-classes @(gobj/get this "state")) %))
   :registerInputFocusHandler     (interaction-handler :listen "nativeInput" "focus")
   :deregisterInputFocusHandler   (interaction-handler :unlisten "nativeInput" "focus")
   :registerInputBlurHandler      (interaction-handler :listen "nativeInput" "blur")
   :deregisterInputBlurHandler    (interaction-handler :unlisten "nativeInput" "blur")
   :registerInputInputHandler     (interaction-handler :listen "nativeInput" "input")
   :deregisterInputInputHandler   (interaction-handler :unlisten "nativeInput" "input")
   :registerInputKeydownHandler   (interaction-handler :listen "nativeInput" "keydown")
   :deregisterInputKeydownHandler (interaction-handler :unlisten "nativeInput" "keydown")
   :setHelptextAttr               #(this-as this (swap! (gobj/get this "state") update :mdc/help-attrs assoc (keyword %1) %2))
   :removeHelptextAttr            #(this-as this (swap! (gobj/get this "state") update :mdc/help-attrs dissoc %1))
   :getNativeInput                #(this-as this (gobj/get this "nativeInput"))})


(defadapter Checkbox [component]
  {:root                          (util/find-node (v/dom-node component) #(classes/has "mdc-checkbox"))
   :registerAnimationEndHandler   (interaction-handler :listen "root" (event-name js/window "animationend"))
   :deregisterAnimationEndHandler (interaction-handler :unlisten "root" (event-name js/window "animationend"))
   :registerChangeHandler         (interaction-handler :listen "nativeInput" "change")
   :deregisterChangeHandler       (interaction-handler :unlisten "nativeInput" "change")
   :forceLayout                   #(this-as this (gobj/get (gobj/get this "nativeInput") "offsetWidth"))
   :isAttachedToDOM               #(this-as this (boolean (gobj/get this "root")))
   :getNativeControl              #(this-as this (gobj/get this "nativeInput"))})
(defadapter Dialog
  [{:keys [view/state on-accept on-cancel] :as ^js/React.Component component}]
  (let [root (v/dom-node component)
        accept-btn (gdom/findNode root #(classes/has % "mdc-dialog__footer__button--accept"))
        surface (gdom/findNode root #(classes/has % "mdc-dialog__surface"))
        focus-trap (createFocusTrapInstance surface accept-btn)]
    {:surface                             surface
     :acceptButton                        accept-btn
     :setStyle                            (style-handler :Dialog)
     :addBodyClass                        #(classes/add (gobj/get js/document "body") %)
     :removeBodyClass                     #(classes/remove (gobj/get js/document "body") %)
     :eventTargetHasClass                 (fn [target class-name]
                                            (util/closest target #(classes/has % class-name)))
     :registerSurfaceInteractionHandler   (interaction-handler :listen "surface")
     :deregisterSurfaceInteractionHandler (interaction-handler :unlisten "surface")
     :registerDocumentKeydownHandler      (interaction-handler :listen js/document "keydown")
     :deregisterDocumentKeydownHandler    (interaction-handler :unlisten js/document "keydown")
     :notifyAccept                        (or on-accept #(println :accept))
     :notifyCancel                        (or on-cancel #(println :cancel))
     :trapFocusOnSurface                  (fn [])
     :untrapFocusOnSurface                (fn [])
     }))


(defadapter PersistentDrawer [])


(defadapter TemporaryDrawer
  [{:keys [view/state] :as ^js/React.Component component}]
  (let [root (v/dom-node component)
        ^js/Element drawer (util/find-node root (fn [el] (classes/has el "mdc-temporary-drawer__drawer")))
        ;listener-key (events/listen root "click" #(when (= (.-target %) (.-currentTarget %)) (.close component)))
        ]
    (cond-> {:drawer                             drawer
             ;:onDestroy                          #(events/unlistenByKey listener-key)
             :hasNecessaryDom                    #(do drawer)
             ;:registerInteractionHandler         #(.log js/console "i" %1 %2)
             ;:deregisterInteractionHandler       #(.log js/console %1 %2)
             :registerDrawerInteractionHandler   (interaction-handler :listen "drawer")
             :deregisterDrawerInteractionHandler (interaction-handler :unlisten "drawer")
             :registerTransitionEndHandler       (interaction-handler :listen "drawer" (event-name js/window "transitionend"))
             :deregisterTransitionEndHandler     (interaction-handler :unlisten "drawer" (event-name js/window "transitionend"))
             :getDrawerWidth                     #(util/force-layout drawer)
             :setTranslateX                      (fn [n]
                                                   (swap! state assoc-in [:mdc/root-styles (getTransformPropertyName)]
                                                          (when n (str "translateX(" n "px)"))))
             :updateCssVariable                  (fn [value]
                                                   (when (supportsCssCustomProperties)
                                                     (swap! state assoc-in [:mdc/root-styles (aget mdc "MDCTemporaryDrawerFoundation" "strings" "OPACITY_VAR_NAME")] value)))
             :getFocusableElements               #(.querySelectorAll drawer (aget mdc "MDCTemporaryDrawerFoundation" "strings" "FOCUSABLE_ELEMENTS"))
             :saveElementTabState                (gobj/get mdc "saveElementTabState")
             :restoreElementTabState             (gobj/get mdc "restoreElementTabState")
             :makeElementUntabbable              #(.setAttribute ^js/Element % "tabindex" -1)
             :isDrawer                           #(= % drawer)}
            (.-notifyOpen component) (assoc :notifyOpen (.-notifyOpen component))
            (.-notifyClose component) (assoc :notifyClose (.-notifyClose component)))))

(defadapter FormField
  [component]
  {:root                  (util/find-tag (v/dom-node component) #"LABEL")

   :activateInputRipple   #(this-as this (let [^js/mdc.Foundation ripple (-> (gobj/get this "component")
                                                                             (gobj/get "mdcRipple"))]
                                           (.activate ripple)))
   :deactivateInputRipple #(this-as this (let [^js/mdc.Foundation ripple (-> (gobj/get this "component")
                                                                             (gobj/get "mdcRipple"))]
                                           (.deactivate ripple)))})

(defadapter GridList [])
(defadapter IconToggle [])

(defn index-of
  "Index of x in js-coll, where js-coll is an array-like object that does not implement .indexOf (eg. HTMLCollection)"
  [js-coll x]
  (let [length (.-length js-coll)]
    (loop [i 0]
      (cond (= i length) -1
            (= x (aget js-coll i)) i
            :else (recur (inc i))))))

(defadapter SimpleMenu
  [{:keys [view/state] :as component}]
  (let [^js/Element menuItemContainer (util/find-node (v/dom-node component) (fn [el] (classes/has el "mdc-simple-menu__items")))
        children (fn [this] (gdom/getChildren menuItemContainer))]
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
     :getWindowDimensions              #(do #js {"width"  (.-innerWidth js/window)
                                                 "height" (.-innerHeight js/window)})
     :setScale                         (fn [x y]
                                         (swap! state assoc-in [:mdc/root-styles (getTransformPropertyName)] (str "scale(" x ", " y ")")))
     :setInnerScale                    (fn [x y]
                                         (swap! state assoc-in [:mdc/inner-styles (getTransformPropertyName)] (str "scale(" x ", " y ")")))
     :getNumberOfItems                 #(count (v-util/flatten-seqs (:view/children component)))
     :getYParamsForItemAtIndex         (fn [index]
                                         (this-as this
                                           (let [child (aget (children this) index)]
                                             #js {"top"    (.-offsetTop child)
                                                  "height" (.-offsetHeight child)})))
     :setTransitionDelayForItemAtIndex (fn [index value]
                                         (this-as this
                                           (-> (aget (children this) index)
                                               (gobj/get "style")
                                               (.setProperty "transition-delay" value))))
     :getIndexForEventTarget           (fn [target]
                                         (this-as this
                                           (index-of (children this) target)))
     :notifySelected                   (fn [evtData]
                                         (when-let [f (get component :on-selected)]
                                           (f evtData)))
     :notifyCancel                     (fn [evtData]
                                         (when-let [f (get component :on-cancel)]
                                           (f evtData)))
     :saveFocus                        #(this-as this (aset this "previousFocus" (.-activeElement js/document)))
     :isFocused                        #(this-as this (= (.-activeElement js/document) (gobj/get this "root")))
     :focus                            #(this-as this (.focus (gobj/get this "root")))
     :getFocusedItemIndex              #(this-as this (index-of (children this) (.-activeElement js/document)))
     :focusItemAtIndex                 #(this-as this (.focus (aget (children this) %)))
     :isRtl                            #(get component :rtl)
     :setTransformOrigin               #(swap! state assoc-in [:mdc/root-styles (str (getTransformPropertyName) "-origin")] %)
     :setPosition                      (fn [pos]
                                         (swap! state update :mdc/root-styles merge (reduce (fn [m k]
                                                                                                    (assoc m k (or (aget pos k) nil))) {} ["left" "right" "top" "bottom"])))
     :getAccurateTime                  #(.. js/window -performance (now))}))

(defadapter Radio [])

(defadapter Ripple
  [component]
  (let [target (util/find-node (v/dom-node component) #(or (classes/has % "mdc-ripple-surface")
                                                           (classes/has % "mdc-ripple-target")))]
    {:root                         target
     :rippleTarget                 target
     :updateCssVariable            (style-handler :Ripple "rippleTarget")
     :registerInteractionHandler   (interaction-handler :listen "rippleTarget")
     :deregisterInteractionHandler (interaction-handler :unlisten "rippleTarget")
     :browserSupportsCssVars       #(supportsCssVariables js/window)
     :isUnbounded                  #(dataset/has target "mdcRippleIsUnbounded")
     :isSurfaceActive              #(let [^js/Function f (gobj/get target MatchesProperty)]
                                      (.call f target ":active"))
     :registerResizeHandler        #(.addEventListener js/window "resize" %)
     :deregisterResizeHandler      #(.removeEventListener js/window "resize" %)
     :getWindowPageOffset          #(do #js {"x" (gobj/get js/window "pageXOffset")
                                             "y" (gobj/get js/window "pageYOffset")})
     :computeBoundingRect          #(.getBoundingClientRect ^js/Element target)}))

(defadapter Select [])

(defadapter Snackbar [])

(defn log-ret [msg x]
  (.log js/console msg x)
  x)

(def wrap-log
  (memoize (fn [msg f]
             (fn [& args]
               (prn msg)
               (apply f args)))))

(defadapter Toolbar [{:keys [with-content] :as component}]
  (let [toolbar-element (cond-> (v/dom-node component)
                                with-content (gdom/getFirstElementChild))
        flexible-row-element (-> toolbar-element
                                 (gdom/findNode #(classes/has % "mdc-toolbar__row")))
        parent-window (or (some-> toolbar-element (aget "ownerDocument") (aget "defaultView"))
                          js/window)]
    (cond-> {:root                              toolbar-element
             :flexibleRowElement                flexible-row-element
             :titleElement                      (util/find-node toolbar-element #(classes/has % "mdc-toolbar__title"))
             :registerScrollHandler             (interaction-handler :listen parent-window "scroll")
             :deregisterScrollHandler           (interaction-handler :unlisten parent-window "scroll")
             :registerResizeHandler             (interaction-handler :listen parent-window "resize")
             :deregisterResizeHandler           (interaction-handler :unlisten parent-window "resize")
             :getViewportWidth                  #(.-innerWidth parent-window)
             :getViewportScrollY                #(.-pageYOffset parent-window)
             :getOffsetHeight                   #(.-offsetHeight toolbar-element)
             :getFlexibleRowElementOffsetHeight #(.-offsetHeight flexible-row-element)
             :notifyChange                      (fn [ratio])
             :setStyle                          (style-handler :Toolbar)
             :setStyleForTitleElement           (style-handler :Toolbar :titleElement)
             :setStyleForFlexibleRowElement     (style-handler :Toolbar :flexibleRowElement)}
            with-content (merge {:fixedAdjustElement            (gdom/getNextElementSibling toolbar-element)
                                 :setStyleForFixedAdjustElement (style-handler :Toolbar :fixedAdjustElement)
                                 }))))