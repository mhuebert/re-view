(ns re-view.core
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [sablono.core :refer-macros [html]]
            [goog.object :as gobj]))

(def ^:dynamic *trigger-state-render* true)
(def ^:dynamic *user-prior-state* false)

;; convenience access methods

(defn mounted? [c]
  (.isMounted c))

;; https://github.com/omcljs/om/blob/master/src/main/om/next.cljs#L745
(defn react-ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (.-refs component) (gobj/get name)))


;; self-management of cljs props and state

(defn props
  "React complains if we mutate props, so we always read from state.
  (this is set in componentWillReceiveProps)"
  [this]
  (if *user-prior-state*
    (.. this -state -cljs$previousProps)
    (.. this -state -cljs$props)))

(defn children [this]
  (:view$children (props this))
  #_(some-> this .-props .-children))

(defn parse-props [props]
  (.-cljs$props props))

(defn state [this]
  (if *user-prior-state*
    (some-> this .-state .-cljs$previousState)
    (some-> this .-state .-cljs$state)))

;; State manipulation

(defn set-state! [this new-state]
  (when (not= new-state (state this))
    (set! (.. this -state -cljs$previousState)
          (.. this -state -cljs$state))
    (set! (.. this -state -cljs$state) new-state)

    (if (and *trigger-state-render*
             (mounted? this)
             (.call (.-shouldComponentUpdate this) this (.-props this) nil))
      (.forceUpdate this))))

(defn update-state! [this f & args]
  (set-state! this (apply f (cons (state this) args))))

(defn render-component
  "Force render a component with supplied props, even if not a root component."
  ([this] (render-component this (props this)))
  ([this props] (render-component this props nil))
  ([this props & children]
   (let [js-props (js-obj "cljs$props" (cond-> props
                                               (not= '(nil) children) (assoc :view$children children)))]
     ;; manually invoke componentWillReceiveProps
     (when-let [will-receive-props (.-componentWillReceiveProps this)]
       (.call will-receive-props this js-props))

     ;; only render if shouldComponentUpdate returns true (emulate ordinary React lifecycle)
     (when (.call (.-shouldComponentUpdate this) this js-props nil)
       (.forceUpdate this (fn []))))))

;; TODO - include render loop

;; Lifecycle method handling

(defn expand-props [this props]
  (cond-> props
          (.-expandProps this) ((.-expandProps this) props)))

(def lifecycle-wrap-fns
  {"getInitialState"
   (fn [f]
     (fn []

       (this-as this
         (let [initial-state-obj (set! (.-state this)
                                       #js {:cljs$props (expand-props this (.. this -props -cljs$props))})
               state (if f (f this) nil)]
           (doto initial-state-obj
             (gobj/set "cljs$state" state)
             (gobj/set "cljs$previousState" state))))))

   "componentWillMount"
   (fn [f]
     (fn []
       (this-as this
         (binding [*trigger-state-render* false] (f this)))))

   "componentWillReceiveProps"
   (fn [f]
     (fn [next-props]
       (this-as this
         (let [expanded-props (expand-props this (parse-props next-props))]
           (doto (.-state this)
             (gobj/set "cljs$previousProps" (.. this -state -cljs$props))
             (gobj/set "cljs$props" expanded-props))
           (binding [*trigger-state-render* false
                     *user-prior-state* true]
             (when f (f this expanded-props)))))))

   "shouldComponentUpdate"
   (fn [f]
     (fn [_ _]
       (this-as this
         (let [update? (if f (binding [*user-prior-state* true]
                               (f this
                                  (.. this -state -cljs$props)
                                  (.. this -state -cljs$state)))
                             ;; by default, update if props or state have changed
                             true)]
           update?))))

   "componentWillUpdate"
   (fn [f]
     (fn [_ _]
       (this-as this
         (when f (binding [*user-prior-state* true]
                   (f this
                      (.. this -state -cljs$props)
                      (.. this -state -cljs$state)))))))

   "componentDidUpdate"
   (fn [f]
     (fn [_ _]
       (this-as this
         (f this
            (.. this -state -cljs$previousProps)
            (.. this -state -cljs$previousState)))))

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
                                                          (fn [& args]
                                                            (this-as this
                                                              (apply f (cons this args))))))]
                (assoc m name (wrap-f (get methods name)))))
            {}
            (into #{"shouldComponentUpdate" "componentWillUpdate"
                    "getInitialState" "componentWillReceiveProps"}
                  ;; these three methods ^^ have default behaviours so we always "wrap" them
                  (keys methods)))))

(defn factory
  [class]
  (fn [props & children]
    (let [props? (or (nil? props) (map? props))
          children (if props? children (cons props children))
          {:keys [ref key] :as props} (when props? props)
          element (js/React.createElement
                    class
                    #js {:key        (or key (if-let [keyfn (.. class -prototype -reactKey)]
                                               (keyfn props) key))
                         :ref        ref
                         :cljs$props (cond->
                                       (dissoc props :keyfn :ref :key)
                                       (not= '(nil) children) (assoc :view$children children))}
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