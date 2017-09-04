(ns re-view.core
  (:refer-clojure :exclude [partial])
  (:require-macros [re-view.core])
  (:require [re-db.d :as d]
            [re-db.patterns :as patterns :include-macros true]
            [re-view.render-loop :as render-loop]
            [re-view-hiccup.core :as hiccup]
            [goog.object :as gobj]
            [re-view.util :as v-util]
            [re-view.view-spec :as vspec]
            react
            react-dom))

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
  [this]
  (not (true? (gobj/get this "unmounted"))))

(defn wrap-props
  "Wraps :on-change handlers of text inputs to apply changes synchronously."
  [props tag]
  (cond-> props
          (and (contains? props :on-change)
               (#{"input" "textarea"} tag)) (update :on-change render-loop/apply-sync!)))

(defn reactive-render
  "Wrap a render function to force-update the component when re-db patterns accessed during evaluation are invalidated."
  [f]
  (fn []
    (this-as this
      (let [re$view (gobj/get this "re$view")
            {:keys [patterns value]} (patterns/capture-patterns (apply f this (:view/children @re$view)))

            prev-patterns (:view/re-db.patterns @re$view)]
        (when-not (= prev-patterns patterns)
          (when-let [un-sub (gobj/get this "reactiveUnsubscribe")] (un-sub))

          (gobj/set this "reactiveUnsubscribe" (when-not (empty? patterns)
                                                 (d/listen patterns #(force-update this))))
          (vswap! re$view assoc :view/re-db.patterns patterns))
        value))))



(def kmap
  "Mapping of convenience keys to React lifecycle method keys."
  {:constructor             "constructor"
   :view/initial-state      "$getInitialState"
   :view/did-catch          "componentDidCatch"
   :view/will-mount         "componentWillMount"
   :view/did-mount          "componentDidMount"
   :view/will-receive-props "componentWillReceiveProps"
   :view/will-receive-state "componentWillReceiveState"
   :view/should-update      "shouldComponentUpdate"
   :view/will-update        "componentWillUpdate"
   :view/did-update         "componentDidUpdate"
   :view/will-unmount       "componentWillUnmount"
   :view/render             "render"})

(defn compseq
  "Compose fns to execute sequentially over the same arguments"
  [& fns]
  (fn [& args]
    (doseq [f fns]
      (apply f args))))

(defn collect
  "Merge a list of method maps. Multiple lifecycle methods execute sequentially. Only the last-defined :should-update function is applied."
  [methods]
  (let [methods (apply merge-with (fn [a b] (if (vector? a) (conj a b) [a b])) methods)]
    (reduce-kv (fn [m method-k fns]
                 (cond-> m
                         (vector? fns) (assoc method-k (if (keyword-identical? method-k :view/should-update)
                                                         (last fns)
                                                         (apply compseq fns))))) methods methods)))

(defn wrap-methods
  "Wrap a component's methods, binding arguments and specifying lifecycle update behaviour."
  [method-k f]
  (if-not (fn? f)
    f
    (case method-k
      (:view/initial-state
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
            (apply f this (:view/children @(gobj/get this "re$view"))))))
      (:view/did-mount
        :view/did-update)
      (fn []
        (this-as this
          (apply f this (:view/children @(gobj/get this "re$view")))))
      (fn [& args]
        (this-as this
          (apply f this args))))))

(defn init-state
  "Return a state atom for component. The component will update when it changes."
  [this initial-state]
  (let [a (atom initial-state)]
    (vswap! (gobj/get this "re$view")
            assoc
            :view/state a
            :view/prev-state initial-state)
    (add-watch a :state-changed (fn [_ _ old-state new-state]
                                  (when (not= old-state new-state)
                                    (vswap! (gobj/get this "re$view") assoc :view/prev-state old-state)
                                    (when-let [^js/Function will-receive (gobj/get this "componentWillReceiveState")]
                                      (.call will-receive this))
                                    (when (and *trigger-state-render* (if-let [^js/Function should-update (gobj/get this "shouldComponentUpdate")]
                                                                        (.call should-update this)
                                                                        true))
                                      (force-update this)))))
    a))

(defn ensure-state [this]
  (when-not (contains? @(aget this "re$view") :view/state)
    (init-state this nil)))

(extend-protocol ILookup
  react/Component
  (-lookup
    ([this k]
     (if (#{"view" "spec"} (namespace k))
       (do (when (keyword-identical? k :view/state) (ensure-state this))
           (get @(gobj/get this "re$view") k))
       (get-in @(gobj/get this "re$view") [:view/props k])))
    ([this k not-found]
     (if (#{"view" "spec"} (namespace k))
       (do (when (keyword-identical? k :view/state) (ensure-state this))
           (get @(gobj/get this "re$view") k))
       (get-in @(gobj/get this "re$view") [:view/props k] not-found)))))

(defn lifecycle-methods
  "Augment lifecycle methods with default behaviour."
  [methods]
  (->> (collect [{:view/will-receive-props (fn [this props]
                                             (let [{prev-props :view/props prev-children :view/children :as this} this]
                                               (let [next-props (aget props "props")]
                                                 (vswap! (gobj/get this "re$view")
                                                         assoc
                                                         :view/props next-props
                                                         :view/prev-props prev-props
                                                         :view/children (aget props "children")
                                                         :view/prev-children prev-children))))
                  :view/should-update      (fn [{:keys [view/props
                                                        view/prev-props
                                                        view/children
                                                        view/prev-children
                                                        view/state
                                                        view/prev-state]}]
                                             (or (not= props prev-props)
                                                 (not= children prev-children)
                                                 (when-not (nil? state)
                                                   (not= @state prev-state))))}
                 methods
                 {:view/will-unmount (fn [{:keys [view/state] :as this}]
                                       (gobj/set this "unmounted" true)
                                       (when-let [un-sub (aget this "reactiveUnsubscribe")]
                                         (un-sub))
                                       (some-> state (remove-watch :state-changed)))
                  :view/did-update   (fn [this]
                                       (let [re$view (aget this "re$view")
                                             {prev-props :view/props
                                              state      :view/state} @re$view]
                                         (vreset! re$view
                                                  (cond-> (assoc @re$view :view/prev-props prev-props)
                                                          state (assoc :view/prev-state @state)))))}])
       (reduce-kv (fn [obj method-k method]
                    (doto obj
                      (gobj/set (get kmap method-k) (wrap-methods method-k method)))) #js {})))



(defn swap-silently!
  "Swap a component's state atom without forcing an update (render)"
  [& args]
  (binding [*trigger-state-render* false]
    (apply swap! args)))

(defn init-props
  "When a component is instantiated, bind element methods and populate initial props."
  [this $props]
  (if $props
    (do (gobj/set this "re$view"
                  (volatile! (-> (gobj/get $props "class")
                                 (assoc :view/props (gobj/get $props "props")
                                        :view/children (gobj/get $props "children")))))
        (when-let [instance-keys (gobj/get $props "instance")]
          (doseq [k (gobj/getKeys instance-keys)]
            (let [f (gobj/get instance-keys k)]
              (gobj/set this k (if (fn? f) (fn [& args]
                                             (apply f this args)) f))))))
    (gobj/set this "re$view" (volatile! {:view/props    nil
                                         :view/children nil})))
  this)

(defn element-constructor
  "Body of constructor function for ReView component."
  [this $props]
  (init-props this $props)
  (when-let [initial-state (gobj/get this "$getInitialState")]
    (init-state this (cond-> initial-state
                             (fn? initial-state) (apply this (:view/children @(gobj/get this "re$view"))))))
  this)

(defn factory
  "Return a function which returns a React element when called with props and children."
  [constructor class-keys instance-keys]
  (let [{{defaults :props/defaults
          :as      prop-spec} :spec/props
         children-spec        :spec/children
         :as                  class-keys} (-> class-keys
                                              (update :spec/props vspec/normalize-props-map)
                                              (update :spec/children vspec/resolve-spec-vector))
        class-react-key (gobj/get constructor "key")
        display-name (gobj/get constructor "displayName")]
    (fn [props & children]
      (let [[props children] (if (or (map? props)
                                     (nil? props)) [props children] [nil (cons props children)])
            props (cond->> props defaults (merge defaults))
            key (or (get props :key)
                    (when class-react-key
                      (cond (string? class-react-key) class-react-key
                            (keyword? class-react-key) (get props class-react-key)
                            (fn? class-react-key) (apply class-react-key (assoc props :view/children children) children)
                            :else (throw (js/Error "Invalid key supplied to component"))))
                    display-name)]

        (when (true? INSTRUMENT!)
          (vspec/validate-props display-name prop-spec props)
          (vspec/validate-children display-name children-spec children))

        (react/createElement constructor #js {"key"      key
                                              "ref"      (get props :ref)
                                              "props"    (dissoc props :ref)
                                              "children" children
                                              "instance" instance-keys
                                              "class"    class-keys})))))

(defn ^:export view*
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
  [{:keys [lifecycle-keys
           class-keys
           instance-keys
           react-keys] :as re-view-base}]
  (let [prototype (new react/Component)
        _ (gobj/extend prototype (lifecycle-methods lifecycle-keys))
        constructor (fn ReView [$props]
                      (this-as this
                        (element-constructor this $props)))
        _ (gobj/set constructor "prototype" prototype)]
    (doseq [[k v] (seq react-keys)]
      (gobj/set constructor (v-util/camelCase k) v))
    (-> (factory constructor class-keys instance-keys)
        (doto (gobj/set "re$view$base" (assoc re-view-base :prototype prototype))))))

(defn render-to-dom
  "Render view to element, which should be a DOM element or id of element on page."
  [component element]
  (react-dom/render component (cond->> element
                                       (string? element)
                                       (.getElementById js/document))))

(defn partial
  "Partially apply props and optional class-keys to base view. Props specified at runtime will overwrite those given here.
  `re$view$base` property is retained on preserved."
  ([base props]
   (-> (fn [& args]
         (let [[user-props & children] (cond->> args
                                                (not (map? (first args))) (cons {}))]
           (apply base (merge props user-props) children)))
       (doto (gobj/set "re$view$base" (gobj/get base "re$view$base")))))
  ([base base-overrides props]
   (partial (view* (merge-with merge (gobj/get base "re$view$base") base-overrides)) props)))

(defn pass-props
  "Remove prop keys handled by component, useful for passing down unhandled props to a child component.
  By default, removes all keys listed in the component's :spec/props map. Set `:consume false` for props
  that should be passed through."
  [this]
  (apply dissoc (get this :view/props) (get-in this [:spec/props :props/consumed])))

(def is-react-element? v-util/is-react-element?)

