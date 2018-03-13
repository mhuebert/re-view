(ns re-view.core
  (:refer-clojure :exclude [partial])
  (:require-macros [re-view.core])
  (:require [re-db.d :as d]
            [re-db.patterns :as patterns :include-macros true]
            [re-view.render-loop :as render-loop]
            [re-view.hiccup.core :as hiccup]
            [goog.object :as gobj]
            [re-view.util :as v-util]
            [re-view.view-spec :as vspec]
            ["react-dom" :as react-dom]
            ["react" :as react]))

(def schedule! render-loop/schedule!)
(def force-update render-loop/force-update)
(def force-update! render-loop/force-update!)
(def flush! render-loop/flush!)

(def ^:dynamic *trigger-state-render* true)

(goog-define INSTRUMENT! false)

(defn dom-node
  "Return DOM node for component"
  [component]
  (react-dom/findDOMNode component))

(defn mounted?
  "Returns true if component is still mounted to the DOM.
  This is necessary to avoid updating unmounted components."
  [component]
  (not (true? (gobj/get component "unmounted"))))

(defn- wrap-props
  "Wraps :on-change handlers of text inputs to apply changes synchronously."
  [props tag]
  (cond-> props
          (and (contains? props :on-change)
               (or (identical? "input" tag)
                   (identical? "textarea" tag))) (update :on-change render-loop/apply-sync!)))

(defn reactive-render
  "Wrap a render function to force-update the component when re-db patterns accessed during evaluation are invalidated."
  [f]
  (fn []
    (this-as ^js this
      (let [{:keys [patterns value]} (patterns/capture-patterns (apply f this (.-re$children this)))

            prev-patterns (.-re$dbPatterns this)]
        (when-not (= prev-patterns patterns)
          (when-let [un-sub (.-reactiveUnsubscribe this)] (un-sub))
          (set! (.-reactiveUnsubscribe this) (when-not (empty? patterns)
                                               (d/listen patterns #(force-update this))))
          (set! (.-re$dbPatterns this) patterns))
        value))))

(defn- class-key
  "Mapping of methods-map keys to React lifecycle keys."
  [k]
  (case k
    :constructor "constructor"
    
    ;; re-view class keys
    :view/props "re$props"
    :view/state "re$state"
    :view/children "re$children"
    :view/initial-state "re$initialState"
    
    ;; react class keys
    :view/did-catch "componentDidCatch"
    :view/will-mount "componentWillMount"
    :view/did-mount "componentDidMount"
    :view/will-receive-props "componentWillReceiveProps"
    :view/will-receive-state "componentWillReceiveState"
    :view/should-update "shouldComponentUpdate"
    :view/will-update "componentWillUpdate"
    :view/did-update "componentDidUpdate"
    :view/will-unmount "componentWillUnmount"
    :view/render "render"
    
    ;; fallback to "re$<name>" 
    ;; (you can specify class keys in a view's method map by using the :view/.. namespace)
    (str "re$" (v-util/camelCase (name k)))))

(defn compseq
  "Compose fns to execute sequentially over the same arguments"
  [& fns]
  (fn [& args]
    (doseq [f fns]
      (apply f args))))

(defn- collect
  "Merge a list of method maps. Multiple lifecycle methods execute sequentially. Only the last-defined :should-update function is applied."
  [methods]
  (let [methods (apply merge-with (fn [a b] (if (vector? a) (conj a b) [a b])) methods)]
    (reduce-kv (fn [m method-k fns]
                 (cond-> m
                         (vector? fns) (assoc method-k (if (keyword-identical? method-k :view/should-update)
                                                         (last fns)
                                                         (apply compseq fns))))) methods methods)))

(defn finish-lifecycle [^js this]
  "after update, update prev-props and prev-state"
  (set! (.-re$prevProps this) (.-re$props this))
  (set! (.-re$prevChildren this) (.-re$children this))
  (when-let [state (.-re$state this)]
    (set! (.-re$prevState this) @state)))

(defn- wrap-methods
  "Wrap a component's methods, binding arguments and specifying lifecycle update behaviour."
  [method-k f]
  (if-not (fn? f)
    f
    (case method-k
      (:view/initial-state
       :view/state
       :key
       :constructor) f
      :view/render (reactive-render f)
      :view/will-receive-props
      (fn [props]
        (binding [*trigger-state-render* false]
          (this-as this
            (f this props))))
      (:view/will-mount
       :view/will-unmount
       :view/will-receive-state
       :view/will-update)
      (fn []
        (binding [*trigger-state-render* false]
          (this-as this
            (apply f this (.-re$children this)))))
      (:view/did-mount
       :view/did-update)
      (fn []
        (this-as this
          (apply f this (.-re$children this))))
      (fn [& args]
        (this-as this
          (apply f this args))))))

(defn- init-state!
  "Bind a component to update whenever `state` changes.
  `state` can be any type which implements IWatchable and IDeref."
  [^js component state]
  (set! (.-re$state component) state)
  (set! (.-re$prevState component) @state)

  (add-watch state component (fn [_ _ old-state new-state]
                               (when (not= old-state new-state)
                                 (set! (.-re$prevState component) old-state)
                                 (when-let [^js will-receive (gobj/get component "componentWillReceiveState")]
                                   (.call will-receive component))
                                 (when (and *trigger-state-render* (if-let [^js should-update (gobj/get component "shouldComponentUpdate")]
                                                                     (.call should-update component)
                                                                     true))
                                   (force-update component)))))
  state)

(defn- ensure-state!
  "Lazily create and bind a state atom for `component`"
  [^js component]
  (when-not (.-re$state component)
    (init-state! component (atom nil))))

(extend-protocol ILookup
  ;; for convenience, we allow reading keys from a component's props by looking them up
  ;; directly on the component. this enables destructuring in lifecycle/render method arglist.
  react/Component
  (-lookup
    ([^js this k]
     ;; TODO (perf)
     ;; - use direct comparisons instead of set operations
     ;; - save props/state/children directly on the component instead of in a single map
     (let [ns (namespace k)]
       (cond (identical? ns "view")
             (if (keyword-identical? k :view/state)
               (do (ensure-state! this)
                   (.-re$state this))
               (gobj/get this (class-key k)))

             (identical? ns "spec")
             (get (.-re$spec this) k)

             :else
             (get (.-re$props this) k))))
    ([this k not-found]
     (let [ns (namespace k)]
       (cond (identical? ns "view")
             (if (keyword-identical? k :view/state)
               (do (ensure-state! this)
                   (.-re$state this))
               (gobj/get this (class-key k)))

             (identical? ns "spec")
             (get (.-re$spec this) k)

             :else
             (get (.-re$props this) k not-found))))))

(defn- lifecycle-methods
  "Augment lifecycle methods with default behaviour."
  [methods]
  (->> (collect [{:view/will-receive-props (fn [^js this ^js incoming-prop-obj]
                                             ;; when a component receives new props, update internal state.
                                             (set! (.-re$prevProps this) (.-re$props this))
                                             (set! (.-re$props this) (.-props incoming-prop-obj))
                                             (set! (.-re$prevChildren this) (.-re$children this))
                                             (set! (.-re$children this) (.-children incoming-prop-obj)))
                  :view/should-update (fn [this]
                                        ;; default should-update behaviour compares props, children, and state.
                                        (or (not= (.-re$props this) (.-re$prevProps this))
                                            (not= (.-re$children this) (.-re$prevChildren this))
                                            (when-let [state (.-re$state this)]
                                              (not= @state (.-re$prevState this)))))}
                 methods
                 {:view/will-unmount (fn [{:keys [view/state] :as this}]
                                       ;; manually track unmount state, react doesn't do this anymore,
                                       ;; otherwise our async render loop can't tell if a component is still on the page.
                                       (gobj/set this "unmounted" true)

                                       (when-let [un-sub (.-reactiveUnsubscribe this)]
                                         (un-sub))
                                       (some-> state (remove-watch this)))
                  :view/did-update finish-lifecycle}])
       (reduce-kv (fn [obj method-k method]
                    (doto obj
                      (gobj/set (class-key method-k) (wrap-methods method-k method)))) #js {})))

(defn swap-silently!
  "Swap a component's state atom without forcing an update (render)"
  [& args]
  (binding [*trigger-state-render* false]
    (apply swap! args)))

(defn- init-component
  "Bind element methods and populate initial props for `component`."
  [component ^js $props]
  (if $props
    (let [props (.-props $props)
          children (.-children $props)]

      (set! (.-re$spec component) (.-spec $props))
      (set! (.-re$props component) (dissoc props :view/state))
      (set! (.-re$children component) children)

      (when-let [instance-keys (.-instance $props)]
        (doseq [k (gobj/getKeys instance-keys)]
          (let [f (gobj/get instance-keys k)]
            (gobj/set component k (if (fn? f) (fn [& args]
                                                (apply f component args)) f)))))
      (when-let [state (or
                        ;;;;;;;;;;;;;;;
                        ;;
                        ;; state can be provided in 1 of 3 ways, depends on whether you want to provide state
                        ;; at time of component definition or element instantiation.
                        ;;
                        ;; 1. pass state as :view/state prop, when element is created.
                        ;;    in this case it must be an atom-like thing that implements IWatchable/IDeref

                        (get props :view/state)

                        ;; 2. in the component's methods map, :view/initial-state can either be a static value or
                        ;;    a function, which will be called w/ the component to return initial state.
                        ;;    the initial value is wrapped in an atom.

                        (when-let [initial-state (.-re$initialState component)]
                          (atom (cond-> initial-state (fn? initial-state) (apply component children))))

                        ;; 3. in the component's methods map, can specify :view/state directly. Must be
                        ;;    an atom-like thing.

                        (when-let [watchable-state (gobj/get component "$state")]
                          (cond-> watchable-state (fn? watchable-state) (apply component children))))]
        (init-state! component state))))
  component)

(defn- factory
  "Return a function which returns a React element when called with props and children."
  [^js constructor]
  (let [{:keys [spec-keys
                instance-keys] :as re$view$base} (.-re$view$base constructor)
        normalized-specs {:spec/props (vspec/normalize-props-map (get spec-keys :spec/props))
                          :spec/children (vspec/resolve-spec-vector (get spec-keys :spec/children))}
        display-name (.-displayName constructor)]
    (doto (fn [props & children]
            (let [[props children] (if (or (map? props)
                                           (nil? props)) [props children] [nil (cons props children)])
                  props (if-let [spec-defaults (get-in normalized-specs [:spec/props :props/defaults])]
                          (merge spec-defaults props)
                          props)
                  key (or (get props :key)
                          (when-let [class-react-key (.-key constructor)]
                            (cond (string? class-react-key) class-react-key
                                  (keyword? class-react-key) (get props class-react-key)
                                  (fn? class-react-key) (apply class-react-key (assoc props :view/children children) children)
                                  :else (throw (js/Error "Invalid key supplied to component"))))
                          display-name)]

              (when (true? INSTRUMENT!)
                (vspec/validate-props display-name (get normalized-specs :spec/props) props)
                (vspec/validate-children display-name (get normalized-specs :spec/children) children))

              (react/createElement constructor #js {"key" key
                                                    "ref" (get props :ref)
                                                    "props" (dissoc props :ref)
                                                    "children" children
                                                    "instance" instance-keys
                                                    "spec" normalized-specs})))
      (gobj/set "re$view$base" re$view$base))))

(defn- ^:export class*
  [{:keys [lifecycle-keys
           react-keys] :as re-view-base}]
  (let [prototype (new react/Component)
        _ (gobj/extend prototype (lifecycle-methods lifecycle-keys))
        ^js constructor (fn ReView [$props]
                          (this-as this
                            (init-component this $props)))
        _ (gobj/set constructor "prototype" prototype)]
    (doseq [[k v] (seq react-keys)]
      (gobj/set constructor (v-util/camelCase k) v))
    (set! (.-re$view$base constructor)
          (assoc re-view-base :prototype prototype))
    constructor))

(def ^:export view*
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
   Result of :render function is automatically passed through hiccup/element,
   unless it is already a valid React element.
   "
  (comp factory class*))

(defn prototype [^js class]
  (:prototype (.-re$view$base class)))

(defn render-to-dom
  "Render view to element, which should be a DOM element or id of element on page."
  [component element]
  (react-dom/render component (cond->> element
                                       (string? element)
                                       (.getElementById js/document))))

(defn partial
  "Partially apply props and optional class-keys to base view. Props specified at runtime will overwrite those given here.
  `re$view$base` property is retained."
  ([^js base props]
   (let [^js partially-applied-view (fn [& args]
                                      (let [[user-props & children] (cond->> args
                                                                             (not (map? (first args))) (cons {}))]
                                        (apply base (merge props user-props) children)))]

     (set! (.-re$view$base partially-applied-view)
           (.-re$view$base base))
     partially-applied-view))
  ([^js base base-overrides props]
   (partial (view* (merge-with merge (.-re$view$base base) base-overrides)) props)))

(defn pass-props
  "Remove prop keys handled by component, useful for passing down unhandled props to a child component.
  By default, removes all keys listed in the component's :spec/props map. Set `:consume false` for props
  that should be passed through."
  [this]
  (apply dissoc (get this :view/props) (get-in this [:spec/props :props/consumed])))

(def is-react-element? v-util/is-react-element?)

