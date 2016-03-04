(ns re-view.core
  (:require-macros [re-view.core :refer [defcomponent]])
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [sablono.core :refer-macros [html]]
            [goog.object :as gobj]))

(def ^:dynamic *trigger-state-render* true)

(defn mounted? [c]
  (.isMounted c))

(defn children [this]
  (some-> this .-props .-children))

;; from om
(defn react-ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (.-refs component) (gobj/get name)))

(def render-count (atom 0))

(defn has-forced-props?
  "Determines if we are in a forceUpdate call"
  [this]
  (= (some-> this .-state .-cljs$forcedProps .-render$count) @render-count))

(defn forced-props [this]
  (aget this "state" "cljs$forcedProps" "cljs$props"))

(defn props
  "React only supports supplying props to a root component. To enable .forceUpdate
   on sub-components with new props, we store .forceUpdate props in state and
   do a check here to determine which props are currently fresh."
  [this]
  (if (has-forced-props? this)
    (forced-props this)
    (some-> this .-props .-cljs$props)))

(defn render-component
  "Force render a component with supplied props"
  ([component] (render-component component nil))
  ([component props]
   (when props
     (set! (.. component -state -cljs$forcedProps) #js {:render$count (swap! render-count inc)
                                                        :cljs$props   props}))
   (.forceUpdate component (fn []))))

(defn parse-props [props]
  (.-cljs$props props))

(defn parse-state [state]
  (.-cljs$state state))

(defn state [this]
  (some-> this .-state .-cljs$state))

(defn prev-props [this]
  (some-> this .-state .-cljs$previousProps))

(defn prev-state [this]
  (some-> this .-state .-cljs$previousState))

(defn next-state [this]
  (some-> this .-state .-cljs$nextState))

(defn advance-state
  [this]
  (let [prev-state (state this)
        next-state (.. this -state -cljs$nextState)
        prev-props (props this)]
    (set! (.. this -state -cljs$previousProps) prev-props)
    (set! (.. this -state -cljs$state) next-state)
    (set! (.. this -state -cljs$previousState) prev-state)))

(defn update-state! [this f & args]
  (let [new-state (apply f (cons (state this) args))]
    (when (not= new-state (state this))
      (set! (.. this -state -cljs$previousState) (state this))
      (set! (.. this -state -cljs$nextState) new-state)
      (if (and *trigger-state-render* (mounted? this))
        (.forceUpdate this)
        (advance-state this)))))

(defn set-state! [this state]
  (update-state! this #(do state)))

;; https://github.com/omcljs/om/blob/master/src/main/om/util.cljs#L3
#_(defn force-children [x]
  (if-not (seq? x) x
                   (into [] (map force-children x))))

(defn factory
  [class]
  (fn [props & children]
    (let [props? (map? props)
          children (if props? children (cons props children))
          {:keys [ref key] :as props} (when props? props)
          element (js/React.createElement
                    class
                    #js {:key        (if-let [keyfn (.. class -prototype -reactKey)]
                                       (keyfn props) key)
                         :ref        ref
                         :cljs$props (dissoc props :keyfn :ref :key)}
                    children)]
      (set! (.-reactClass element) class)
      element)))