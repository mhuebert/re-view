(ns re-view.core
  (:require-macros [re-view.core :refer [defview]])
  (:require [re-db.core :as d]
            [goog.object :as gobj]))

(def ^:dynamic *trigger-state-render* true)

(defn mounted? [c]
  (.isMounted c))

(defn register-cell-mount [db eid]
  (when-let [html-node (d/get @db eid :cell/html-node)]
    (when-let [f (d/get @db eid :cell/on-mount)]
      (f html-node))))

(defn children [this]
  (some-> this .-props .-children))

(defn react-ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (.-refs component) (gobj/get name)))

(defn components-by-view-id [db id]
  (when id
    (->> (d/entities @db [:view/id id])
         (map :view/component)
         (filter mounted?))))

(defn components-by-e [db e]
  (when e (components-by-view-id db (d/get @db e :id))))

(defn register-view
  "Keep index to components based on id"
  ([db this indexes]
   (d/transact! db [(merge indexes
                        {:db/id          -1
                         :view/component this})])))

(defn deregister-view
  "Discard index"
  [db this]
  (js/setTimeout
    #(do
      (d/transact! db [[:db/retract-entity (:db/id (d/entity @db [:view/component this]))]])) 0))

(def render-count (atom 0))

(defn has-forced-props? [this]
  (= (some-> this .-state .-cljs$forcedProps .-render$count) @render-count))

(defn forced-props [this]
  (aget this "state" "cljs$forcedProps" "cljs$props"))

(defn props
  "React only supports supplying props to the root component.
  To enable forceUpdate with new props, we put props in state along with the current
  value of render-counter. We only use the force$value if its render$count is equal to
  the current render."
  [this]
  (if (has-forced-props? this)
    (forced-props this)
    (some-> this .-props .-cljs$props)))

(defn render-component
  "Force render a single component with supplied props"
  ([component] (render-component component nil))
  ([component props]
   (try
     (when props
       (set! (.. component -state -cljs$forcedProps) #js {:render$count (swap! render-count inc)
                                                          :cljs$props   props}))
     (.forceUpdate component (fn []))
     (catch js/Error e
       ;; occasional weird ReferenceError here, maybe related to figwheel reloading?
       ;; stacktrace: https://gist.github.com/mhuebert/63f7e61293bd6001800f
       (.debug js/console #_(cells.meta/cell->label (:id props))
               "React .forceUpdate error, re-rendering whole page" e)
       #_(aset js/window "e" e)
       #_(.error js/console e)
       #_(render-all)))))

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

(defn advance-state [this]
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
(defn force-children [x]
  (if-not (seq? x) x
                   (into [] (map force-children x))))


(defn factory [class]
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
                    (force-children children))]
      (set! (.-reactClass element) class)
      element)))