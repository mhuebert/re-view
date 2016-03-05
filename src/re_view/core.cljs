(ns re-view.core
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [sablono.core :refer-macros [html]]
            [goog.object :as gobj]))

(def ^:dynamic *trigger-state-render* true)

;; convenience access methods

(defn mounted? [c]
  (.isMounted c))

(defn children [this]
  (some-> this .-props .-children))


(defn react-ref                                             ;; https://github.com/omcljs/om/blob/master/src/main/om/next.cljs#L745
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (.-refs component) (gobj/get name)))


;; self-management of cljs props and state

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
    (if (.. this -state -cljs$forcedProps -active)
      (forced-props this)
      (some-> this .-state .-cljs$lastProps))
    (some-> this .-props .-cljs$props)))

(defn parse-props [props]
  (.-cljs$props props))

(defn state [this]
  (some-> this .-state .-cljs$state))

(defn next-state [this]
  (if (.hasOwnProperty (.-state this) "cljs$nextState")
    (aget this "state" "cljs$nextState")
    (state this)))

(defn advance-state
  "Copy 'next-state' to 'state' once during each component lifecycle."
  [this]
  (when (has-forced-props? this)
    (gobj/set (.. this -state -cljs$forcedProps) "active" true))
  (gobj/set (.-state this) "cljs$lastProps" (props this))
  (doto (.-state this)
    (gobj/set "cljs$state" (next-state this))))

;; State manipulation

(defn set-state! [this new-state]
  (when (not= new-state (state this))
    (set! (.. this -state -cljs$nextState) new-state)
    (if (and *trigger-state-render*
             (mounted? this)
             (.call (.-shouldComponentUpdate this) this (.-props this) nil))
      (.forceUpdate this)
      ;; if *trigger-state-render* is false, we skip the component lifecycle
      ;; & therefore advance-state manually here
      (advance-state this))))

(defn update-state! [this f & args]
  (set-state! this (apply f (cons (state this) args))))

;; Render

(defn render-component
  "Force render a component with supplied props, even if not a root component."
  ([this] (render-component this nil))
  ([this props] (render-component this props false))
  ([this props force?]
   (let [js-props #js {:render$count (swap! render-count inc)
                       :cljs$props   props}]

     (doto (.-state this)
       (gobj/set "cljs$forcedProps" js-props))

     ;; manually invoke componentWillReceiveProps
     (when-let [will-receive-props (.-componentWillReceiveProps this)]
       (.call will-receive-props this js-props))

     ;; only render if shouldComponentUpdate returns true (emulate ordinary React lifecycle)
     (when (or force? (.call (.-shouldComponentUpdate this) this js-props nil))
       (.forceUpdate this (fn []))))))

; the thing is
; props
; until after :component-will-update,
; we only get access to new props via

;; TODO - include render loop

;; Lifecycle method handling

(def lifecycle-wrap-fns
  {"getInitialState"
   (fn [f]
     (fn []
       (this-as this
         (js-obj "cljs$state" (if f (f this) nil)))))

   "componentWillMount"
   (fn [f]
     (fn []
       (this-as this
         (binding [*trigger-state-render* false] (f this)))))

   "componentWillReceiveProps"
   (fn [f]
     (fn [next-props]
       (this-as this
         (binding [*trigger-state-render* false]
           (f this (parse-props next-props))))))

   "shouldComponentUpdate"
   (fn [f]
     (fn [next-props _]
       (this-as this
         (let [next-props (parse-props next-props)
               next-state (next-state this)
               update? (if f (f this next-props next-state)
                             ;; by default, update if props or state have changed
                             true
                             #_(or (not= next-props (props this))
                                   (not= next-state (state this))))]
           (when-not update? (advance-state this))
           update?))))

   "componentWillUpdate"
   (fn [f]
     (fn [next-props _]
       (this-as this
         (when f (let [next-props (if (has-forced-props? this) (forced-props this)
                                                               (parse-props next-props))]
                   (f this next-props (next-state this))))
         (advance-state this))))

   "componentDidUpdate"
   (fn [f]
     (fn [_ _]
       (this-as this
         (f this (.. this -state -cljs$previousProps) (.. this -state -cljs$previousState))
         (set! (.. this -state -cljs$previousProps) (props this))
         (set! (.. this -state -cljs$previousState) (state this)))))

   "render"
   (fn [f]
     (fn []
       (this-as this
         (let [element (f this)]
           ;; wrap in sablono.core/html if not already a valid React element
           (if (js/React.isValidElement element)
             element
             (html element))))))})

(defn camelCase
  "Convert dash-ed and name/spaced-keywords to dashEd and name_spacedKeywords"
  [s]
  (clojure.string/replace s #"([^\\-])-([^\\-])"
                          (fn [[_ m1 m2]] (str m1 (clojure.string/upper-case m2)))))

(defn update-keys
  "Update keys of map m with function f"
  [update-key-f m]
  (reduce-kv (fn [m key val] (assoc m (update-key-f key) val)) {} m))

(defn wrap-lifecycle-methods
  "Lifecycle methods are wrapped to manage CLJS props and state
   and provide default behaviour."
  [methods]
  (let [methods (update-keys (comp camelCase name) methods)]
    (reduce (fn [m name]
              (let [wrap-f (get lifecycle-wrap-fns name (fn [f]
                                                          (fn [] (this-as this (f this)))))]
                (assoc m name (wrap-f (get methods name)))))
            {}
            (into #{"shouldComponentUpdate" "componentWillUpdate"
                    "getInitialState" "componentWillReceiveProps"}
                  ;; these three methods ^^ have default behaviours so we always "wrap" them
                  (keys methods)))))

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

(defn react-class [methods]
  (js/React.createClass
    (apply js-obj (mapcat identity (wrap-lifecycle-methods methods)))))

(defn component
  "Returns a React component factory for supplied lifecycle methods.
   Expects a single map of functions, or any number of key-function pairs,

   (component {:render (fn [this] [:div ...])})

   -or-

   (component

     :get-initial-state
     (fn [this] {:apple-state :ripe})

     :render
     (fn [this] [:div ...]))

   See other functions in this namespace for how to work with props and state.
   Result of :render function is automatically wrapped in sablono.core/html,
   unless it is already a valid React element.
   "
  [& methods]
  (let [methods (if (and (= 1 (count methods)) (map? (first methods)))
                  (first methods)
                  (apply hash-map methods))]
    (-> methods
        react-class
        factory)))

(comment

  ;; example of component with controlled input

  (ns my-app.core
    (:require [re-view.core :as view :refer [component]]))

  (def greeting
    (component

      :get-initial-state
      (fn [this] {:first-name "Herbert"})

      :render
      (fn [this]
        (let [{:keys [first-name]} (view/state this)]
          [:div
           [:p (str "Hello, " first-name "!")]
           [:input {:value     first-name
                    :on-change #(view/update-state!
                                 this assoc :first-name (-> % .-target .-value))}]])))))