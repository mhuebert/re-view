(ns re-view.core
  (:require-macros re-view.core)
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [re-db.core :as d]
            [sablono.core :refer-macros [html]]
            [goog.object :as gobj]))

(def ^:dynamic *trigger-state-render* true)
(def ^:dynamic *use-prior* false)
(def ^:dynamic *use-prior-props* false)

(def js-get gobj/get)

(defn js-call [target fname & args]
  (let [f (aget target fname)]
    (.apply f target (clj->js args))))

(def db (d/create {:view-id {:db/index true}}))

(defn by-id [id]
  (d/entity-ids @db [:view-id id]))

(def component-state (atom {}))

;; convenience access methods

(defn mounted? [c]
  (and c (.isMounted c)))

(defn force-update! [this]
  (when (mounted? this)
    (try (.forceUpdate this)
         (catch js/Error e
           (if-let [on-error (aget this "onError")]
             (on-error e)
             (do (.debug js/console "No :on-error method in component" this)
                 (.error js/console e)))))))

(def to-render (atom #{}))

(defn raf-polyfill []
  (if-not (js-get js/window "requestAnimationFrame")
    (aset js/window "requestAnimationFrame"
          (or
            (js-get js/window "webkitRequestAnimationFrame")
            (js-get js/window "mozRequestAnimationFrame")
            (js-get js/window "oRequestAnimationFrame")
            (js-get js/window "msRequestAnimationFrame")
            (fn [cb]
              (js-call js/window "setTimeout" cb (/ 1000 60)))))))

(raf-polyfill)

(defn render-loop
  []
  (let [components @to-render]
    (when-not (empty? components)
      (reset! to-render #{})
      (doseq [c components]
        (force-update! c))))
  (js/requestAnimationFrame render-loop))

(defonce _ (render-loop))

(defn force-update [this]
  (swap! to-render conj this))

;; https://github.com/omcljs/om/blob/master/src/main/om/next.cljs#L745
(defn react-ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (js-get component "refs") (js-get name)))



;; self-management of cljs props and state

(defn props
  "React complains if we mutate props, so we always read from state.
  (this is set in componentWillReceiveProps)"
  [this]
  (d/get @db this (if (or *use-prior* *use-prior-props*)
                    :prev-props :props)))

(defn children [this]
  (d/get @db this :children))

(defn prev-children [this]
  (d/get @db this :children))

(defn state [this]
  (d/get @db this (if *use-prior* :prev-state :state)))

;; State manipulation

(declare update-state!)

(defn set-state! [this new-state]
  ;; set-state! always triggers render, unless shouldComponentUpdate returns false.
  ;; if we assume that if state hasn't changed we don't re-render,
  ;; controlled inputs break.
  (d/transact! db [{:id         this
                    :prev-state (d/get @db this :state)
                    :state      new-state}])
  (when-let [will-receive-state (js-get this "componentWillReceiveState")]
    (.call will-receive-state this))
  (when (and *trigger-state-render*
             (mounted? this)
             (.call (js-get this "shouldComponentUpdate") this (d/get @db this :props) nil))
    (force-update this)))

(defn update-state! [this f & args]
  (set-state! this (apply f (cons (state this) args))))

(defn render-component
  "Force render a component with supplied props, even if not a root component."
  ([this] (render-component this (props this)))
  ([this props] (render-component this props nil))
  ([this props & children]
    ;; manually invoke componentWillReceiveProps
   (when-let [will-receive-props (js-get this "componentWillReceiveProps")]
     (.call will-receive-props this #js {:cljs$props    props
                                         :cljs$children children}))

    ;; only render if shouldComponentUpdate returns true (emulate ordinary React lifecycle)
   (when (.call (js-get this "shouldComponentUpdate") this props (state this))
     (force-update this))))

;; TODO - include render loop

;; Lifecycle method handling

(defn expand-props [this props]
  (cond->> props
           (js-get this "expandProps") (js-call this "expandProps")))

(defn initial-subscription-data
  "If component has specified subscriptions, initialize them"
  [this initial-props]
  (reduce-kv (fn [m k sub-fn]
               (let [{:keys [default] :as sub} (sub-fn this initial-props #(update-state! this assoc k %))]
                 (cond-> m
                         sub (assoc-in [:subscriptions k] sub)
                         default (assoc k (default)))))
             {}
             (js-get this "subscriptions")))

(defn begin-subscriptions [this next-props]
  (doseq [{:keys [subscribe]} (vals (:subscriptions (state this)))]
    (subscribe next-props)))

(defn end-subscriptions [this prev-props]
  (doseq [{:keys [unsubscribe]} (vals (:subscriptions (state this)))]
    (when unsubscribe (unsubscribe prev-props))))

(defn update-subscriptions [this prev-props next-props]
  (when (seq (keep identity (filter (fn [{:keys [should-update]}] (and should-update (should-update prev-props next-props))) (vals (:subscriptions (state this))))))
    (update-state! this merge (initial-subscription-data this next-props))
    (end-subscriptions this prev-props)
    (begin-subscriptions this next-props)))

(def lifecycle-wrap-fns
  {"getInitialState"
   (fn [get-initial-state-fn]
     (fn []
       (this-as this
         (let [initial-props (expand-props this (aget this "props" "cljs$props"))
               initial-state (merge (when get-initial-state-fn (get-initial-state-fn this initial-props))
                                    (initial-subscription-data this initial-props))]
           (d/transact! db [{:id         this
                             :props      initial-props
                             :view-id    (or (:view-id initial-props) (:id initial-props))
                             :children   (aget this "props" "cljs$children")
                             :state      initial-state
                             :prev-state initial-state}])))))

   "componentDidMount"
   (fn [f]
     (fn []
       (this-as this
         (binding [*use-prior-props* false
                   *use-prior* false]
           (when f (f this
                      (d/get @db this :props)
                      (d/get @db this :state)))))))

   "componentWillMount"
   (fn [f]
     (fn []
       (this-as this
         (binding [*trigger-state-render* false]
           (begin-subscriptions this (props this))
           (when f (f this))))))

   "componentWillUnmount"
   (fn [f]
     (fn []
       (this-as this
         (binding [*trigger-state-render* false]
           (end-subscriptions this (props this))
           (when f (f this))
           (d/transact! db [[:db/retract-entity this]])))))

   "componentWillReceiveState"
   (fn [f]
     (fn []
       (this-as this
         (let [props (d/get @db this :props)]
           (binding [*trigger-state-render* false]
             (update-subscriptions this props props))
           (when f
             (f this
                props
                (d/get @db this :state)
                (d/get @db this :prev-state)))))))

   "componentWillReceiveProps"
   (fn [f]
     (fn [props]

       (this-as this
         (let [{prev-props :props prev-children :children} (d/entity @db this)
               next-props (expand-props this (js-get props "cljs$props"))
               next-children (js-get props "cljs$children")]

           (d/transact! db [{:id            this
                             :prev-props    prev-props
                             :props         next-props
                             :view-id       (or (:view-id next-props) (:id next-props))
                             :prev-children prev-children
                             :children      next-children}])

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
                                  (d/get @db this :props)
                                  (d/get @db this :state)
                                  (d/get @db this :prev-props)
                                  (d/get @db this :prev-state)))
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
                      (d/get @db this :props)
                      (d/get @db this :state)
                      (d/get @db this :prev-props)
                      (d/get @db this :prev-state)))))))

   "componentDidUpdate"
   (fn [f]
     (fn [_ _]
       (this-as this
         (f this
            (d/get @db this :props)
            (d/get @db this :state)
            (d/get @db this :prev-props)
            (d/get @db this :prev-state)))))

   "render"
   (fn [f]
     (fn []
       (this-as this
         (let [element (f this
                          (d/get @db this :props)
                          (d/get @db this :state))]
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
    (let [props (js->clj props)
          props? (or (nil? props) (map? props))
          children (if props? children (cons props children))
          {:keys [ref key] :as props} (when props? props)
          element (js/React.createElement
                    class
                    #js {:key           (or key
                                            (if-let [keyfn (aget class "prototype" "reactKey")]
                                              (if (string? keyfn) keyfn (keyfn props)) key)
                                            (.-displayName class))
                         :ref           ref
                         :cljs$props    (dissoc props :keyfn :ref :key)
                         :cljs$children (when (not= '(nil) children) children)})]
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
   (let [methods (if (= 1 (count methods)) (cons :render methods) methods)]
     (->> methods
          (map #(if (vector? %) (fn [] %) %))
          (apply hash-map)
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