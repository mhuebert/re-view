(ns re-view-material.mdc
  (:require [re-view.core :as v]
            [goog.dom :as gdom]
            [goog.dom.classes :as classes]
            [goog.dom.dataset :as dataset]
            [goog.events :as events]
            [goog.object :as gobj]
            [goog.style :as gstyle]
            [re-view-material.util :as util])
  (:require-macros [re-view-material.mdc :refer [defadapter]]))

(set! *warn-on-infer* true)

(def mdc (gobj/get js/window "mdc"))
(def event-name (gobj/get mdc "getCorrectEventName"))
(def supportsCssVariables (gobj/get mdc "supportsCssVariables"))
(def getMatchesProperty (gobj/get mdc "getMatchesProperty"))
(def getTransformPropertyName (gobj/get mdc "getTransformPropertyName"))
(def supportsCssCustomProperties (gobj/get mdc "supportsCssCustomProperties"))
(def applyPassive (gobj/get mdc "applyPassive"))
(def remapEvent (gobj/get mdc "remapEvent"))

(def MatchesProperty (getMatchesProperty (.-prototype js/HTMLElement)))
(def conj-set (fnil conj #{}))
(def disj-set (fnil disj #{}))

(defn foundation-class
  "Look up foundation class by name. (this may change when we get :npm-deps to work with mdc)"
  [name]
  (gobj/get mdc (str "MDC" name "Foundation")))

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
      (.destroy foundation))))

(defn current-target? [^js/Event e]
  (= (.-target e) (.-currentTarget e)))

(def adapter-base
  {:addClass                         #(this-as this
                                        (swap! (gobj/get this "state") update (keyword (str "mdc" (gobj/get this "name") "-classes")) conj-set %))
   :removeClass                      #(this-as this
                                        (swap! (gobj/get this "state") update (keyword (str "mdc" (gobj/get this "name") "-classes")) disj-set %))
   :hasClass                         #(this-as this
                                        (classes/has (gobj/get this "root") %))
   :registerInteractionHandler       (fn [event-type handler]
                                       (this-as this
                                         (some-> (gobj/get this "root") (util/listen (remapEvent event-type) handler (applyPassive)))))
   :deregisterInteractionHandler     (fn [event-type handler]
                                       (this-as this
                                         (some-> (gobj/get this "root") (util/unlisten (remapEvent event-type) handler (applyPassive)))))
   :registerDocumentKeydownHandler   #(util/listen js/document "keydown" %)
   :deregisterDocumentKeydownHandler #(util/unlisten js/document "keydown" %)
   :isRtl                            #(this-as this
                                        (let [^js/Element root (gobj/get this "root")
                                              ^js/CSSStyleDeclaration styles (js/getComputedStyle root)]
                                          (= "rtl" (.getPropertyValue styles "direction"))))
   :updateCssVariable                (fn [attr val]
                                       (this-as this
                                         (swap! (gobj/get this "state") assoc-in [:mdc-styles attr] val)))})

(defn bind-adapter
  "Return methods that bind an adapter to a specific component instance"
  [{:keys [view/state] :as this}]
  (let [root-node (v/dom-node this)]
    {:root        root-node
     :nativeInput (util/find-tag root-node #"INPUT|TEXTAREA")
     :state       (get this :view/state)
     :reView      this}))

(defn make-foundation
  "Extends adapter with base adapter methods, and wraps with Foundation class"
  [name foundation-class methods]
  (fn [this]
    (foundation-class. (->> (merge (bind-adapter this)
                                   adapter-base
                                   (if (fn? methods) (methods this) methods)
                                   {:name name})
                            (clj->js)))))

(defadapter Textfield
  {:addClassToLabel               #(this-as this (swap! (gobj/get this "state") update :mdc-label-classes conj-set %))
   :removeClassFromLabel          #(this-as this (swap! (gobj/get this "state") update :mdc-label-classes disj-set %))
   :addClassToHelptext            #(this-as this (swap! (gobj/get this "state") update :mdc-helpText-classes conj-set %))
   :removeClassFromHelptext       #(this-as this (swap! (gobj/get this "state") update :mdc-helpText-classes disj-set %))
   :helptextHasClass              #(this-as this (contains? (:mdc-helpText-classes @(gobj/get this "state")) %))
   :registerInputFocusHandler     #(this-as this (util/listen (gobj/get this "nativeInput") "focus" %))
   :deregisterInputFocusHandler   #(this-as this (util/unlisten (gobj/get this "nativeInput") "focus" %))
   :registerInputBlurHandler      #(this-as this (util/listen (gobj/get this "nativeInput") "blur" %))
   :deregisterInputBlurHandler    #(this-as this (util/unlisten (gobj/get this "nativeInput") "blur" %))
   :registerInputInputHandler     #(this-as this (util/listen (gobj/get this "nativeInput") "input" %))
   :deregisterInputInputHandler   #(this-as this (util/unlisten (gobj/get this "nativeInput") "input" %))
   :registerInputKeydownHandler   #(this-as this (util/listen (gobj/get this "nativeInput") "keydown" %))
   :deregisterInputKeydownHandler #(this-as this (util/unlisten (gobj/get this "nativeInput") "keydown" %))
   :setHelptextAttr               #(this-as this (swap! (gobj/get this "state") update :mdc-helpText-attrs assoc (keyword %1) %2))
   :removeHelptextAttr            #(this-as this (swap! (gobj/get this "state") update :mdc-helpText-attrs dissoc %1))
   :getNativeInput                #(this-as this (gobj/get this "nativeInput"))})


(defadapter Checkbox (fn [component]
                       {:root                          (util/find-node (v/dom-node component) #(classes/has "mdc-checkbox"))
                        :registerAnimationEndHandler   #(this-as this (util/listen (gobj/get this "root") (event-name js/window "animationend") %))
                        :deregisterAnimationEndHandler #(this-as this (util/unlisten (gobj/get this "root") (event-name js/window "animationend") %))
                        :registerChangeHandler         #(this-as this (util/listen (gobj/get this "nativeInput") "change" %))
                        :deregisterChangeHandler       #(this-as this (util/unlisten (gobj/get this "nativeInput") "change" %))
                        :forceLayout                   #(this-as this (gobj/get (gobj/get this "nativeInput") "offsetWidth"))
                        :isAttachedToDOM               #(this-as this (boolean (gobj/get this "root")))
                        :getNativeControl              #(this-as this (gobj/get this "nativeInput"))}))
(defadapter Dialog)
(defadapter PersistentDrawer)
(defadapter TemporaryDrawer
  (fn [^js/React.Component {:keys [view/state] :as component}]
    (let [^js/Element drawer (util/find-node (v/dom-node component) (fn [el] (classes/has el "mdc-temporary-drawer__drawer")))]
      (cond-> {:drawer                             drawer
               :styleTarget                        drawer
               :hasNecessaryDom                    #(this-as this drawer)
               :registerDrawerInteractionHandler   (fn [evt handler]
                                                     (this-as this (util/guarded-listen #(= (gobj/get % "target")
                                                                                            (gobj/get % "currentTarget"))
                                                                                        drawer (remapEvent evt) handler (applyPassive))))
               :deregisterDrawerInteractionHandler (fn [evt handler]
                                                     (this-as this (util/guarded-unlisten drawer (remapEvent evt) handler (applyPassive))))
               :registerTransitionEndHandler       #(this-as this (util/listen drawer (event-name js/window "transitionend") %))
               :deregisterTransitionEndHandler     #(this-as this (util/unlisten drawer (event-name js/window "transitionend") %))
               :getDrawerWidth                     #(this-as this
                                                      (util/force-layout drawer))
               :setTranslateX                      (fn [n]
                                                     (this-as this (swap! state assoc-in [:mdc-styles (getTransformPropertyName)]
                                                                          (when n (str "translateX(" n "px)")))))
               :updateCssVariable                  (fn [value]
                                                     (this-as this (when (supportsCssCustomProperties)
                                                                     (swap! state assoc-in [:mdc-styles (aget mdc "MDCTemporaryDrawerFoundation" "strings" "OPACITY_VAR_NAME")] value))))
               :getFocusableElements               (fn []
                                                     (this-as this (.querySelectorAll drawer (aget mdc "MDCTemporaryDrawerFoundation" "strings" "FOCUSABLE_ELEMENTS"))))
               :saveElementTabState                (gobj/get mdc "saveElementTabState")
               :restoreElementTabState             (gobj/get mdc "restoreElementTabState")
               :makeElementUntabbable              #(.setAttribute ^js/Element % "tabindex" -1)
               :isDrawer                           #(this-as this (= % drawer))}
              (.-notifyOpen component) (assoc :notifyOpen (.-notifyOpen component))
              (.-notifyClose component) (assoc :notifyClose (.-notifyClose component))))))

(defadapter FormField
  (fn [component]
    {:root                  (util/find-tag (v/dom-node component) #"LABEL")

     :activateInputRipple   #(this-as this (let [^js/mdc.Foundation ripple (-> (gobj/get this "reView")
                                                                               (gobj/get "mdcRipple"))]
                                             (.activate ripple)))
     :deactivateInputRipple #(this-as this (let [^js/mdc.Foundation ripple (-> (gobj/get this "reView")
                                                                               (gobj/get "mdcRipple"))]
                                             (.deactivate ripple)))}))

(defadapter GridList)
(defadapter IconToggle)
(defadapter SimpleMenu)
(defadapter Radio)

(defadapter Ripple
  (fn [component]
    {:root                         (util/find-node (v/dom-node component) #(or (classes/has % "mdc-ripple-surface")
                                                                               (classes/has % "mdc-ripple-target")))
     :registerInteractionHandler   (fn [event-type handler]
                                     (this-as this (util/listen (gobj/get this "root") event-type handler)))
     :deregisterInteractionHandler (fn [event-type handler]
                                     (this-as this (util/unlisten (gobj/get this "root") event-type handler)))
     :browserSupportsCssVars       #(supportsCssVariables js/window)
     :isUnbounded                  #(this-as this (dataset/has (gobj/get this "root") "mdcRippleIsUnbounded"))
     :isSurfaceActive              #(this-as this (let [^js/Element root (gobj/get this "root")
                                                        ^js/Function f (gobj/get root MatchesProperty)]
                                                    (.call f root ":active")))
     :registerResizeHandler        #(util/listen js/window "resize" %)
     :deregisterResizeHandler      #(util/unlisten js/window "resize" %)
     :getWindowPageOffset          #(do #js {"x" (gobj/get js/window "pageXOffset")
                                             "y" (gobj/get js/window "pageYOffset")})
     :computeBoundingRect          (fn []
                                     (this-as this
                                       (.getBoundingClientRect ^js/Element (gobj/get this "root"))))}))
(defadapter Select)
(defadapter Snackbar)
