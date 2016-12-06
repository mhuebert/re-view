(ns ^:figwheel-always re-view.core
  (:require-macros [re-view.core])
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [re-db.core :as d]
            [re-view.shared :refer [*lookup-log*]]
            [re-view.subscriptions :as subs]
            [sablono.core :refer [html]]))

(def ^:dynamic *trigger-state-render* true)
(def ^:dynamic *use-render-loop* true)

(def ^:private count-fps? false)
(def ^:private last-fps-time 1)

(defn count-fps!
  [enable?]
  (set! count-fps? enable?))

(defonce db (re-db.core/create))

(defn force-update! [this]
  (try (.forceUpdate this)
       (catch js/Error e
         (if-let [on-error (aget this "onError")]
           (on-error e)
           (do (.debug js/console "No :on-error method in component" this)
               (.error js/console e))))))

(defonce _raf-polyfill
         (if-not (aget js/window "requestAnimationFrame")
           (aset js/window "requestAnimationFrame"
                 (or
                   (aget js/window "webkitRequestAnimationFrame")
                   (aget js/window "mozRequestAnimationFrame")
                   (aget js/window "oRequestAnimationFrame")
                   (aget js/window "msRequestAnimationFrame")
                   (fn [cb]
                     (.call (aget js/window "setTimeout") js/window cb (/ 1000 60)))))))

(def to-render #{})
(def frame-count 0)

(defn render-loop
  [frame-ms]
  (set! frame-count (inc frame-count))
  (when ^:boolean (and (true? count-fps?) (identical? 0 (mod frame-count 29)))
    (re-db.d/transact! [[:db/add ::state :fps (* 1000 (/ 30 (- frame-ms last-fps-time)))]])
    (set! last-fps-time frame-ms))
  (when-not ^:boolean (empty? to-render)
    (doseq [c to-render]
      (force-update! c))
    (set! to-render #{}))
  (js/requestAnimationFrame render-loop))

(defonce _render-loop (js/requestAnimationFrame render-loop))

(defn force-update [this]
  (if *use-render-loop* (set! to-render (conj to-render this))
                        (force-update! this)))

;; https://github.com/omcljs/om/blob/master/src/main/om/next.cljs#L745
(defn get-ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (aget component "refs") (aget name)))


;; State manipulation

(declare update-state!)

(defn set-state! [this new-state]
  ;; set-state! always triggers render, unless shouldComponentUpdate returns false.
  ;; if we assume that if state hasn't changed we don't re-render,
  ;; controlled inputs break.
  (d/transact! db [{:db/id      this
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

(def base-mixin-before
  {"constructor"
   (fn [this $props]
     ;; initialize props and children
     (let [{:keys [re-view/id] :as initial-props} (some-> $props (.-cljs$props))]
       (d/transact! db [{:db/id         this
                         :props         initial-props
                         :prev-props    nil
                         :re-view/id    id
                         :children      (some-> $props (.-cljs$children))
                         :prev-children nil}]))
     ;; initialize state
     (when-let [initial-state-f (aget this "$getInitialState")]
       (let [initial-state (initial-state-f this)]
         (d/transact! db [{:db/id      this
                           :state      initial-state
                           :prev-state initial-state}])))
     this)
   :will-receive-props
   (fn [this props]
     (let [{prev-props :props prev-children :children} this
           {:keys [re-view/id] :as next-props} (aget props "cljs$props")]
       (d/transact! db [{:db/id         this
                         :props         next-props
                         :children      (aget props "cljs$children")
                         :prev-props    prev-props
                         :prev-children prev-children
                         :re-view/id    id}])))})
(def base-mixin-after
  {:will-unmount #(d/transact! db [[:db/retract-entity %]])})

(def reactive-mixin
  {:will-unmount #(when-let [unsub (.-reactiveUnsubscribe %)] (unsub))
   :render       (fn [this f]
                   (let [{:keys [patterns value]} (d/capture-patterns (f))
                         prev-patterns (.-reactivePatterns this)]

                     (when-not (= prev-patterns patterns)

                       (when-let [unsub (.-reactiveUnsubscribe this)]
                         (unsub))

                       (set! (.-reactiveUnsubscribe this)
                             (when-not (empty? patterns)
                               (apply re-db.d/listen! (concat patterns (list #(force-update this))))))

                       (set! (.-reactivePatterns this) patterns))
                     value))})

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

(defn ensure-element [element]
  (if-not (js/React.isValidElement element) (html element) element))

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
        ;; support a single render-wrap fn
        (let [render (last fns)]
          (if (= 1 (count fns))
            (ensure-element ((first fns) this))
            ((first fns) this #(ensure-element (if (fn? render)
                                                 (render this)
                                                 render))))))
      (fn [& args]
        (doseq [f fns] (apply f args))))))

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
                 reactive-mixin
                 methods
                 base-mixin-after])
       (reduce-kv (fn [m method-k method]
                    (assoc m method-k (bind method-k method))) {})
       (update-keys #(get kmap % (camelCase (name %))))))

(defn specify-protocols [o]
  (specify! o
    ILookup
    (-lookup
      ([this k]
       (when-not ^:boolean (nil? *lookup-log*) (swap! *lookup-log* conj k))
       (d/get @db this k))
      ([this k not-found]
       (when-not ^:boolean (nil? *lookup-log*) (swap! *lookup-log* conj k))
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
  (doto (fn view-f
          ([] (view-f {}))
          ([props & children]
           (let [props? (or (nil? props) (map? props))
                 children (cond-> children
                                  (not props?) (cons props))
                 {:keys [ref key] :as props} (when props? props)]
             (js/React.createElement
               class
               #js {"key"           (or key
                                        (if-let [keyfn (aget class "prototype" "reactKey")]
                                          (if (string? keyfn) keyfn (keyfn props)) key)
                                        (.-displayName class))
                    "ref"           ref
                    "cljs$props"    (dissoc props :keyfn :ref :key)
                    "cljs$children" children}))))
    (aset "isView" true)))

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


(defn view
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
  [methods]
  (->> (cond (map? methods) methods
             (vector? methods) {:render (fn [] methods)}
             (fn? methods) {:render methods}
             :else (throw "re-view.core/view must be supplied with a map of lifecycl methods, a render function, or a hiccup vector."))
       (react-class)
       (factory)))

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
    (:require [re-view.core :refer [defview]]))

  (defview greeting
           {:initial-state (fn [this] {:first-name "Herbert"})}
           (fn [{{:keys [first-name]} :state :as this}]
             [:div
              [:p (str "Hello, " first-name "!")]
              [:input {:value     first-name
                       :on-change #(swap! this assoc :first-name (-> % .-target .-value))}]])))