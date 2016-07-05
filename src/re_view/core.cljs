(ns re-view.core
  (:require-macros re-view.core)
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [sablono.core :refer-macros [html]]
            [goog.object :as gobj]))

(def ^:dynamic *trigger-state-render* true)
(def ^:dynamic *use-prior* false)
(def ^:dynamic *use-prior-props* false)

;; convenience access methods

(defn mounted? [c]
  (.isMounted c))

(defn force-update! [this]
  (when (mounted? this)
    (try (.forceUpdate this)
         (catch js/Error e
           (if-let [on-error (.-onError this)]
             (on-error e)
             (do (.debug js/console "No :on-error method in component" this)
                 (.error js/console e)))))))

(def to-render (atom #{}))
(defn render-loop
  []
  (let [components @to-render]
    (when-not (empty? components)
      (reset! to-render #{})
      (doseq [c components] (force-update! c))))
  (js/requestAnimationFrame render-loop))

(defonce _ (render-loop))

(defn force-update [this]
  (swap! to-render conj this))

;; https://github.com/omcljs/om/blob/master/src/main/om/next.cljs#L745
(defn react-ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (.-refs component) (gobj/get name)))

(defn component-state (atom {}))

;; self-management of cljs props and state

(defn props
  "React complains if we mutate props, so we always read from state.
  (this is set in componentWillReceiveProps)"
  [this]
  (if (or *use-prior* *use-prior-props*)
    (.. this -state -cljs$previousProps)
    (.. this -state -cljs$props)))

(defn children [this]
  (:view$children (props this))
  #_(some-> this .-props .-children))

(defn parse-props [props]
  (.-cljs$props props))

(defn state [this]
  (if *use-prior*
    (some-> this .-state .-cljs$previousState)
    (some-> this .-state .-cljs$state)))

;; State manipulation

(declare update-state!)

(defn set-state! [this new-state]
  ;; set-state! always triggers render, unless shouldComponentUpdate returns false.
  ;; if we assume that if state hasn't changed we don't re-render,
  ;; controlled inputs break.
  (set! (.. this -state -cljs$previousState)
        (.. this -state -cljs$state))
  (set! (.. this -state -cljs$state)
        new-state)
  (when-let [will-receive-state (.-componentWillReceiveState this)]
    (.call will-receive-state this))
  (when (and *trigger-state-render*
             (mounted? this)
             (.call (.-shouldComponentUpdate this) this (.-props this) nil))
    (force-update this)))

(defn update-state! [this f & args]
  (set-state! this (apply f (cons (state this) args))))

(defn render-component
  "Force render a component with supplied props, even if not a root component."
  ([this] (render-component this (props this)))
  ([this props] (render-component this props nil))
  ([this props & children]
   (let [clj-props (cond-> props
                           (not= '(nil) children) (assoc :view$children children))
         js-props (js-obj "cljs$props" clj-props)]
     ;; manually invoke componentWillReceiveProps
     (when-let [will-receive-props (.-componentWillReceiveProps this)]
       (.call will-receive-props this js-props))

     ;; only render if shouldComponentUpdate returns true (emulate ordinary React lifecycle)
     (when (.call (.-shouldComponentUpdate this) this clj-props (state this))
       (force-update this)))))

;; TODO - include render loop

;; Lifecycle method handling

(defn expand-props [this props]
  (cond-> props
          (.-expandProps this) ((.-expandProps this) props)))

(defn initial-subscription-data
  "If component has specified subscriptions, initialize them"
  [this initial-props]
  (reduce-kv (fn [m k sub-fn]
               (let [{:keys [default] :as sub} (sub-fn this initial-props #(update-state! this assoc k %))]
                 (cond-> m
                         sub (assoc-in [:subscriptions k] sub)
                         default (assoc k (default)))))
             {}
             (.-subscriptions this)))

(defn begin-subscriptions [this]
  (doseq [{:keys [subscribe]} (vals (:subscriptions (state this)))]
    (subscribe)))

(defn end-subscriptions [this]
  (doseq [{:keys [unsubscribe]} (vals (:subscriptions (state this)))]
    (unsubscribe)))

(defn update-subscriptions [this prev-props next-props]
  (when (seq (keep identity (filter (fn [{:keys [should-update]}] (and should-update (should-update prev-props next-props))) (vals (:subscriptions (state this))))))
    (update-state! this merge (initial-subscription-data this next-props))
    (begin-subscriptions this)))

(def lifecycle-wrap-fns
  {"getInitialState"
   (fn [get-initial-state-fn]
     (fn []
       (this-as this
         (let [initial-props (expand-props this (.. this -props -cljs$props))
               initial-state-obj (set! (.-state this) #js {:cljs$props initial-props})
               state (merge (when get-initial-state-fn (get-initial-state-fn this initial-props))
                            (initial-subscription-data this initial-props))]

           (doto initial-state-obj
             (gobj/set "cljs$state" state)
             (gobj/set "cljs$previousState" state))))))

   "componentDidMount"
   (fn [f]
     (fn []
       (this-as this
         (binding [*use-prior-props* false
                   *use-prior* false]
           (when f (f this (.. this -state -cljs$props) (.. this -state -cljs$state)))))))

   "componentWillMount"
   (fn [f]
     (fn []
       (this-as this
         (binding [*trigger-state-render* false]
           (begin-subscriptions this)
           (when f (f this))))))

   "componentWillUnmount"
   (fn [f]
     (fn []
       (this-as this
         (end-subscriptions this)
         (when f (f this)))))

   "componentWillReceiveState"
   (fn [f]
     (fn []
       (this-as this
         (let [props (.. this -state -cljs$props)]
           (update-subscriptions this props props)
           (when f
             (f this
                props
                (.. this -state -cljs$state)
                (.. this -state -cljs$previousState)))))))

   "componentWillReceiveProps"
   (fn [f]
     (fn [next-props]
       (this-as this
         (let [next-props (expand-props this (parse-props next-props))
               prev-props (.. this -state -cljs$props)]
           (doto (.-state this)
             (gobj/set "cljs$previousProps" prev-props)
             (gobj/set "cljs$props" next-props))
           (binding [*trigger-state-render* false
                     *use-prior-props* true]
             (update-subscriptions this prev-props next-props)
             (when f (f this prev-props next-props)))))))

   "shouldComponentUpdate"
   (fn [f]
     (fn [_ _]
       (this-as this
         (let [update? (if f (binding [*use-prior* true
                                       *trigger-state-render* false]
                               (f this
                                  (.. this -state -cljs$props)
                                  (.. this -state -cljs$state)
                                  (.. this -state -cljs$previousProps)
                                  (.. this -state -cljs$previousState)))
                             ;; by default, update if props or state have changed
                             true)]
           update?))))

   "componentWillUpdate"
   (fn [f]
     (fn [_ _]
       (this-as this
         (when f (binding [*use-prior* true
                           *trigger-state-render* false]
                   (f this
                      (.. this -state -cljs$props)
                      (.. this -state -cljs$state)
                      (.. this -state -cljs$previousProps)
                      (.. this -state -cljs$previousState)))))))

   "componentDidUpdate"
   (fn [f]
     (fn [_ _]
       (this-as this
         (f this
            (.. this -state -cljs$props)
            (.. this -state -cljs$state)
            (.. this -state -cljs$previousProps)
            (.. this -state -cljs$previousState)))))

   "render"
   (fn [f]
     (fn []
       (this-as this
         (let [element (f this
                          (.. this -state -cljs$props)
                          (.. this -state -cljs$state))]
           ;; wrap in sablono.core/html if not already a valid React element
           (if (js/React.isValidElement element)
             element
             (html element))))))})

(defn camelCase
  "Convert dash-ed and name/spaced-keywords to strings: dashEd and name_spacedKeywords"
  [s]
  (clojure.string/replace s #"([^\\-])-([^\\-])"
                          (fn [[_ m1 m2]] (str m1 (clojure.string/upper-case m2)))))

(defn update-keys
  "Update keys of map m with function f"
  [update-key-f m]
  (reduce-kv (fn [m key val] (assoc m (update-key-f key) val)) {} m))

(def default-lifecycle-methods #{"shouldComponentUpdate"
                                 "componentWillUpdate"
                                 "getInitialState"
                                 "componentWillMount"
                                 "componentWillUnmount"
                                 "componentWillReceiveProps"
                                 "componentWillReceiveState"})

(defn wrap-lifecycle-methods
  "Lifecycle methods are wrapped to manage CLJS props and state
   and provide default behaviour."
  [methods]
  (let [methods (update-keys (comp camelCase name) methods)]
    (reduce (fn [m name]
              (let [method (get methods name)
                    wrap-f (if (or (fn? method)
                                   (keyword? method)
                                   (default-lifecycle-methods name))
                             (get lifecycle-wrap-fns name (fn [f]
                                                            (fn [& args]
                                                              (this-as this
                                                                (apply f (cons this args))))))
                             identity)]
                (assoc m name (wrap-f method))))
            {}
            (into default-lifecycle-methods
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
                    #js {:key        (or key
                                         (if-let [keyfn (.. class -prototype -reactKey)]
                                           (keyfn props) key)
                                         (.-displayName class))
                         :ref        ref
                         :cljs$props (cond->
                                       (dissoc props :keyfn :ref :key)
                                       (not= '(nil) children) (assoc :view$children children))}
                    children)]
      ;;
      #_(set! (.-reactClass element) class)
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
  ([& methods]
   (let [methods (if (= 1 (count methods)) (cons :render methods) methods)
         methods (apply hash-map methods)]
     (-> methods
         react-class
         factory))))

(comment

  ;; example of component with controlled input

  (ns my-app.core
    (:require [re-view.core :as view :refer-macros [defcomponent]]))


  (defcomponent greeting

                :get-initial-state
                (fn [this] {:first-name "Herbert"})

                :render
                (fn [this _ {:keys [first-name]}]
                  [:div
                   [:p (str "Hello, " first-name "!")]
                   [:input {:value     first-name
                            :on-change #(view/update-state!
                                         this assoc :first-name (-> % .-target .-value))}]])))