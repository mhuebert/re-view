(ns re-view.material.mdc
  (:require [re-view.core :as v]
            [re-view.view-spec :as s]
            [goog.dom.classes :as classes]
            [goog.object :as gobj]
            [re-view.material.util :as util]

            ["@material/animation" :refer [getCorrectEventName]]
            ["@material/drawer/util" :as mdc-util]

            [clojure.string :as string])
  (:require-macros re-view.material.mdc))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Environment
;;

(def ^js browser? (exists? js/window))
(def ^js Document (when browser? js/document))
(def ^js Body (when Document (.-body Document)))
(def ^js Window (when browser? js/window))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Functions to be called from a component's lifecycle methods
;;

(defn init
  "Initialize an adapter with a re-view component (should be called in componentDidMount).

  Adapters are written to the component on a property of the form `mdc{ComponentName}`"
  [component & adapters]
  (doseq [{:keys [name adapter]} adapters]
    (let [foundation (adapter component)]
      (gobj/set component (str "mdc" name) foundation)
      (.init foundation))))

(defn destroy
  "Destroy mdc foundation instances for component (should be called in componentWillUnmount)."
  [component & adapters]
  (doseq [{:keys [name]} adapters]
    (let [foundation (gobj/get component (str "mdc" name))]
      (when-let [onDestroy (aget foundation "adapter_" "onDestroy")]
        (onDestroy))
      (.destroy foundation))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Handling of adapter state.
;;
;; Adapters are stored as plain javascript properties of re-view components.
;; When an adapter initializes, it stores references to relevant DOM nodes to
;; as properties on itself.
;;
;; State that changes while a component is mounted is stored in the component's
;; `:view/state` atom.
;;
;; Property names and state keys should be predictably named after the official
;; MDC component name and/or the name that a DOM node is given during `init`.
;;


(defn adapter
  "Returns the adapter for `mdc-component-name` attached to `component`."
  [component mdc-component-name]
  (gobj/getValueByKeys component (str "mdc" (name mdc-component-name)) "adapter_"))

(defn element
  "Returns the element (stored in `init`) stored under `property-name`."
  [adapter element-key]
  (gobj/get adapter (name element-key)))

(defn styles-key
  "Returns keyword under which styles should be stored in state, given an element key"
  [element-key]
  (keyword "mdc" (str (name element-key) "-styles")))

(defn classes-key
  "Returns keyword under which classes should be stored in state, given an element key"
  [element-key]
  (keyword "mdc" (str (name element-key) "-classes")))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Adapter implementation helpers
;;

(defn interaction-handler
  "Adds or removes an event handler of `event-type` to `element`.

  `kind` may be `:listen` or `:unlisten`."
  ([kind element event-type]
   (fn [handler]
     (this-as this
       (let [^js target (cond->> element
                                 (string? element)
                                 (gobj/get this))
             event (mdc-util/remapEvent event-type)]
         (condp = kind
           :listen (.addEventListener target event handler (mdc-util/applyPassive))
           :unlisten (.removeEventListener target event handler (mdc-util/applyPassive)))))))
  ([kind element]
   (fn [event-type handler]
     (this-as this
       (let [^js target (cond->> element
                                 (string? element)
                                 (gobj/get this))
             event-type (mdc-util/remapEvent event-type)]
         (condp = kind
           :listen (.addEventListener target event-type handler (mdc-util/applyPassive))
           :unlisten (.removeEventListener target event-type handler (mdc-util/applyPassive))))))))

(defn style-handler
  "Returns a function which adds the given CSS attribute-value pair to `element`"
  [element]
  (fn [attribute value]
    (util/add-styles element {attribute value})))

(defn mdc-style-update
  "Returns a function which updates styles for the given component-name / element-key pair.

  Adapters follow a convention of keeping styles for a particular element under
  a `:mdc/{element-key}-styles` key in the component's state.

  Eg. (mdc-style-update :Ripple :root) will sync the styles stored under :mdc/Ripple-styles with
  the element stored under the :root key in the adapter."
  [mdc-component-name element-key]
  (fn [{:keys [view/state
               view/prev-state] :as this}]
    (let [target (element (adapter this mdc-component-name) element-key)
          style-key (styles-key element-key)]
      (util/add-styles target (get @state style-key) (get prev-state style-key)))))

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
  "Adds or removes a class from the current adapter/component.

  `prefix` may be specified when classes must be handled for more than one element
  of a component.

  javascript `this` is used to look up current component."
  ([action]
   (class-handler action nil))
  ([action prefix]
   (let [f (condp = action :add (fnil conj #{}) :remove (fnil disj #{}))]
     (fn [class-name]
       (this-as this
         (let [state-atom (aget this "state")
               state-key (keyword "mdc"
                                  (str (gobj/get this "name")
                                       (some-> prefix (str "-"))
                                       "-classes"))]
           (swap! state-atom update state-key f class-name)))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Adapter construction
;;

(def adapter-base
  "Common adapter implementations which are used by multiple components."
  {:addClass                         (class-handler :add)
   :removeClass                      (class-handler :remove)
   :hasClass                         #(this-as this
                                        (classes/has (gobj/get this "root") %))
   :registerInteractionHandler       (interaction-handler :listen "root")
   :deregisterInteractionHandler     (interaction-handler :unlisten "root")
   :registerDocumentKeydownHandler   (interaction-handler :listen Document "keydown")
   :deregisterDocumentKeydownHandler (interaction-handler :unlisten Document "keydown")
   :registerDocumentClickHandler     (interaction-handler :listen Document "click")
   :deregisterDocumentClickHandler   (interaction-handler :unlisten Document "click")
   :isRtl                            #(this-as this
                                        (let [^js root (gobj/get this "root")
                                              ^js styles (js/getComputedStyle root)]
                                          (= "rtl" (.getPropertyValue styles "direction"))))
   :addBodyClass                     #(classes/add Body %)
   :removeBodyClass                  #(classes/remove Body %)})

(defn bind-adapter
  "Return methods that bind an adapter to a specific component instance"
  [{:keys [view/state] :as this}]
  (let [root-node (v/dom-node this)]
    {:root        root-node
     :nativeInput (util/find-tag root-node #"INPUT|TEXTAREA")
     :state       state
     :component   this}))

(defn make-foundation
  "Extends adapter with base adapter methods, and wraps with Foundation class."
  [name foundation-class methods]
  (fn [this]
    (foundation-class. (->> (merge (bind-adapter this)
                                   adapter-base
                                   (if (fn? methods) (methods this) methods)
                                   {:name name})
                            (clj->js)))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; View Specs
;;
;; See: https://re-view.io/docs/re-view/view-specs
;;

(s/defspecs {::color         {:spec #{:primary :accent}
                              :doc  "Specifies color variable from theme."}
             ::raised        {:spec :Boolean
                              :doc  "Raised buttons gain elevation, and color is applied to background instead of text."}
             ::ripple        {:spec    :Boolean
                              :doc     "Enables ripple effect on click/tap"
                              :default true}
             ::compact       {:spec :Boolean
                              :doc  "Reduces horizontal padding"}
             ::auto-focus    {:spec :Boolean
                              :doc  "If true, focuses element on mount"}


             ::id            :String
             ::dirty         {:spec :Boolean
                              :doc  "If true, field should display validation errors"}
             ::dense         {:spec :Boolean
                              :doc  "Reduces text size and vertical padding"}
             ::disabled      {:spec         :Boolean
                              :doc          "Disables input element or button"
                              :pass-through true}
             ::label         {:spec :Element
                              :doc  "Label for input element or button"}
             ::on-change     :Function
             ::rtl           {:spec :Boolean
                              :doc  "Show content in right to left."}
             ::value         {:spec :String
                              :doc  "Providing a value causes an input component to be 'controlled'"}
             ::default-value {:spec :String
                              :doc  "For an uncontrolled component, sets the initial value"}})