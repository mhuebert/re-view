(ns ^:figwheel-always re-view.core
  (:refer-clojure :exclude [partial])
  (:require-macros [re-view.core])
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [re-db.core :as d]
            [re-view.subscriptions :as subs]
            [sablono.core :refer [html]]))

(def ^:dynamic *trigger-state-render* true)
(def ^:dynamic *use-render-loop* false)

(def db (d/create {:view-id {:db/index true}}))

(defn by-id [id]
  (d/entity-ids @db [:view-id id]))

(defn force-update! [this]
  (try (.forceUpdate this)
       (catch js/Error e
         (if-let [on-error (aget this "onError")]
           (on-error e)
           (do (.debug js/console "No :on-error method in component" this)
               (.error js/console e))))))

(def to-render (atom #{}))

(defn raf-polyfill []
  (if-not (aget js/window "requestAnimationFrame")
    (aset js/window "requestAnimationFrame"
          (or
            (aget js/window "webkitRequestAnimationFrame")
            (aget js/window "mozRequestAnimationFrame")
            (aget js/window "oRequestAnimationFrame")
            (aget js/window "msRequestAnimationFrame")
            (fn [cb]
              (.call (aget js/window "setTimeout") js/window cb (/ 1000 60)))))))

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
  (if *use-render-loop* (swap! to-render conj this)
                        (force-update! this)))

;; https://github.com/omcljs/om/blob/master/src/main/om/next.cljs#L745
(defn get-ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (aget component "refs") (aget name)))

;; self-management of cljs props and state

(defn partial
  "Partially apply props to a component"
  [component partial-props]
  (fn [& args]
    (let [[props & children] (cond->> args
                                      (not (map? (first args))) (cons {}))]
      (apply component (cons (merge partial-props props) children)))))

;; State manipulation

(declare update-state!)

(defn set-state! [this new-state]
  ;; set-state! always triggers render, unless shouldComponentUpdate returns false.
  ;; if we assume that if state hasn't changed we don't re-render,
  ;; controlled inputs break.
  (d/transact! db [{:id         this
                    :prev-state (d/get @db this :state)
                    :state      new-state}])

  (when-let [will-receive (aget this "componentWillReceiveState")]
    (.call will-receive this))

  (when (and *trigger-state-render*
             (or (nil? (aget this "shouldComponentUpdate"))
                 (.call (aget this "shouldComponentUpdate") this)))
    (force-update this))

  (d/transact! db [[:db/add this :prev-state new-state]]))

(defn swap-state! [this f & args]
  (set-state! this (apply f (cons (:state this) args))))

(defn render-component
  "Force render a component with supplied props, even if not a root component."
  ([this] (render-component this (:props this)))
  ([this props] (render-component this props nil))
  ([this props & children]
    ;; manually invoke componentWillReceiveProps
   (when-let [will-receive-props (aget this "componentWillReceiveProps")]
     (.call will-receive-props this #js {:cljs$props    props
                                         :cljs$children children}))

    ;; only render if shouldComponentUpdate returns true (emulate ordinary React lifecycle)
   (when (and (.-shouldComponentUpdate this) (.shouldComponentUpdate this))
     (force-update this))))


(defn update-patterns [this prev-patterns next-patterns]
  (when (not= prev-patterns next-patterns)
    (let [cb (cljs.core/partial force-update this)]
      (~'apply ~'re-db.d/unlisten! (concat prev-patterns (list cb)))
      (~'apply ~'re-db.d/listen! (concat next-patterns (list cb)))
      (reset! prev-patterns next-patterns))))

(def base-mixin-before
  {"constructor"
   (fn [this $props]
     ;; initialize props and children
     (let [{:keys [view-id id] :as initial-props} (some-> $props (.-cljs$props))]
       (d/transact! db [{:id            this
                         :props         initial-props
                         :prev-props    nil
                         :view-id       (or view-id id)
                         :children      (some-> $props (.-cljs$children))
                         :prev-children nil}]))
     ;; initialize state
     (when-let [initial-state-f (aget this "$getInitialState")]
       (let [initial-state (initial-state-f this)]
         (d/transact! db [{:id         this
                           :state      initial-state
                           :prev-state initial-state}])))
     this)
   :will-receive-props
   (fn [this props]
     (let [{prev-props :props prev-children :children} this
           {:keys [view-id id] :as next-props} (aget props "cljs$props")]
       (d/transact! db [{:id            this
                         :props         next-props
                         :children      (aget props "cljs$children")
                         :prev-props    prev-props
                         :prev-children prev-children
                         :view-id       (or view-id id)}])))})
(def base-mixin-after
  {:will-unmount #(d/transact! db [[:db/retract-entity %]])})

(def kmap {:initial-state      "$getInitialState"
           :will-mount         "componentWillMount"
           :did-mount          "componentDidMount"
           :will-receive-props "componentWillReceiveProps"
           :will-receive-state "componentWillReceiveState"
           :should-update      "shouldComponentUpdate"
           :will-update        "componentWillUpdate"
           :did-update         "componentDidUpdate"
           :will-unmount       "componentWillUnmount"
           :render             "render"})

(defn quash [method-k fns]
  (when-let [fns (->> (if (vector? fns) fns [fns])
                      (remove nil?)
                      seq)]
    (case method-k
      :initial-state
      (fn [this]
        (reduce merge {} (mapv #(% this) fns)))
      :should-update
      (fn [this]
        (first (for [f fns
                     :let [update? (f this)]
                     :when update?]
                 true)))
      :render
      (fn [this]
        (let [element ((last fns) this)]
          ;; wrap in sablono.core/html if not already a valid React element
          (cond-> element
                  (not (js/React.isValidElement element)) (html))))
      (fn [& args]
        (doseq [f fns] (apply f args)))) ))

(defn collect [methods]
  (->> (apply merge-with (cons (fn [a b] (if (vector? a) (conj a b) [a b])) methods))
       (reduce-kv (fn [m method-k fns]
                    (assoc m method-k (cond->> fns
                                               (contains? kmap method-k)
                                               (quash method-k)))) {})))

(defn bind* [f]
  (fn [& args]
    (this-as this
      (apply f (cons this args)))))

(defn bind-without-render* [f]
  (fn [& args]
    (binding [*trigger-state-render* false]
      (this-as this (apply f (cons this args))))))

(defn bind [method-k f]
  (cond (= :initial-state method-k) f

        (contains? #{:will-receive-props :will-mount :will-unmount :will-receive-state :will-update} method-k)
        (bind-without-render* f)

        (or (fn? f) (keyword? f))
        (bind* f)

        :else f))

(defn camelCase
  "Convert dash-ed and name/spaced-keywords to strings: dashEd and name_spacedKeywords"
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
  (->> (collect [base-mixin-before
                 (when (contains? methods :subscriptions) subs/subscription-mixin)
                 methods
                 base-mixin-after])
       (reduce-kv (fn [m method-k method]
                    (assoc m method-k (bind method-k method))) {})
       (update-keys #(get kmap % (camelCase (name %))))))

(defn specify-protocols [o]
  (set! (.-isView o) true)
  (specify! o
    ILookup
    (-lookup
      ([this k]
       (d/get @db this k))
      ([this k not-found]
       (d/get @db this k not-found)))

    ISwap
    (-swap!
      ([this f] (swap-state! this f))
      ([this f a] (swap-state! this f a))
      ([this f a b] (swap-state! this f a b))
      ([this f a b xs] (apply swap-state! (concat (list this f a b) xs))))

    IReset
    (-reset! [this v] (set-state! this v))))

(defn factory
  [class]
  (fn [props & children]
    (let [props (js->clj props)
          props? (or (nil? props) (map? props))
          children (if props? children (cons props children))
          {:keys [ref key] :as props} (when props? props)]
      (js/React.createElement
        class
        #js {:key           (or key
                                (if-let [keyfn (aget class "prototype" "reactKey")]
                                  (if (string? keyfn) keyfn (keyfn props)) key)
                                (.-displayName class))
             :ref           ref
             :cljs$props    (dissoc props :keyfn :ref :key)
             :cljs$children (when (not= '(nil) children) children)}))))

(defn extends [child parent]
  (let [ReactView (aget child "constructor")]
    (set! (.-prototype ReactView) (new parent))
    (doseq [k (.keys js/Object child)]
      (let [f (aget child k)]
        (aset ReactView "prototype" k f)))
    ReactView))

(specify-protocols (.-prototype js/React.Component))

(defn react-class [methods]
  (-> (apply js-obj (mapcat identity (wrap-lifecycle-methods methods)))
      (extends js/React.Component)))


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

(defn is-react-element? [x]
  (and x
       (or (.-isView x)
           (js/React.isValidElement x))))

(defn render-to-dom [component el-id]
  (when-let [element (.getElementById js/document el-id)]
    (js/ReactDOM.render component element)))

(comment

  ;; example of component with controlled input

  (ns my-app.core
    (:require [re-view.core :refer [defcomponent]]))


  (defcomponent greeting

                :initial-state
                (fn [this] {:first-name "Herbert"})

                :render
                (fn [{{:keys [first-name]} :state :as this}]
                  [:div
                   [:p (str "Hello, " first-name "!")]
                   [:input {:value     first-name
                            :on-change #(swap! this assoc :first-name (-> % .-target .-value))}]])))