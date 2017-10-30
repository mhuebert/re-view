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

(def browser? (exists? js/window))
(def Document (when browser? js/document))
(def ^js Body (when Document (.-body Document)))
(def Window (when browser? js/window))

(defn init
  "Instantiate mdc foundations for a re-view component
   (should be called in componentDidMount)."
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

(defn current-target? [^js e]
  (= (.-target e) (.-currentTarget e)))

(defn wrap-log [msg f]
  (fn [& args]
    (prn msg)
    (apply f args)))

(defn interaction-handler
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

(defn adapter [component mdc-key]
  (gobj/getValueByKeys component (str "mdc" (name mdc-key)) "adapter_"))

(defn element [adapter k]
  (gobj/get adapter (name k)))

(defn styles-key
  "Returns keyword under which styles should be stored in state, given an element key"
  [element-key]
  (keyword "mdc" (str (some-> element-key (name) (str "-")) "styles")))

(defn classes-key
  "Returns keyword under which classes should be stored in state, given an element key"
  [element-key]
  (keyword "mdc" (str (some-> element-key (name) (str "-")) "classes")))

(defn style-handler
  [element]
  (fn [attr val]
    (util/add-styles element {attr val})))

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
   (let [f (condp = action :add (fnil conj #{}) :remove (fnil disj #{}))]
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
   :registerDocumentKeydownHandler   (interaction-handler :listen Document "keydown")
   :deregisterDocumentKeydownHandler (interaction-handler :unlisten Document "keydown")
   :registerDocumentClickHandler     (interaction-handler :listen Document "click")
   :deregisterDocumentClickHandler   (interaction-handler :unlisten Document "click")
   :isRtl                            #(this-as this
                                        (let [^js root (gobj/get this "root")
                                              ^js styles (js/getComputedStyle root)]
                                          (= "rtl" (.getPropertyValue styles "direction"))))
   :addBodyClass                     #(classes/add (gobj/get Document "body") %)
   :removeBodyClass                  #(classes/remove (gobj/get Document "body") %)})

(defn bind-adapter
  "Return methods that bind an adapter to a specific component instance"
  [{:keys [view/state] :as this}]
  (let [root-node (v/dom-node this)]
    {:root        root-node
     :nativeInput (util/find-tag root-node #"INPUT|TEXTAREA")
     :state       (get this :view/state)
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

(defn log-ret [msg x]
  (.log js/console msg x)
  x)


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