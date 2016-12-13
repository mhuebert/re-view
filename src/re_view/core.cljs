(ns ^:figwheel-always re-view.core
  (:require-macros [re-view.core])
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [re-db.core :as d]
            [goog.object :as gobj]
            [re-view.shared :refer [*lookup-log*]]
            [re-view.subscriptions :as subs]
            [sablono.core :refer [html]]))

;; todo
;; - store component state in/on component atom (avoid cost of re-db transactions)
;; - some kind of devtools? (show component hierarchy; show subscribed patterns for a component)
;; - can we get accurate error messages from React, to show exactly which component hasn't declared :key properties?
;; - spec? https://www.youtube.com/watch?v=oyLBGkS5ICk&feature=youtu.be
;; - test perf of (empty? to-render) vs (= [] to-render)
;; - look at :should-update behavior in core-test

(def ^:dynamic *trigger-state-render* true)

(def ^:private count-fps? false)
(def ^:private last-fps-time 1)
(def frame-rate 0)

(defn count-fps!
  [enable?]
  (set! count-fps? enable?))

(defn force-update! [this]
  (when-not (true? (.-unmounted this))
    (try (.forceUpdate this)
         (catch js/Error e
           (if-let [on-error (aget this "onError")]
             (on-error e)
             (do (.debug js/console "No :on-error method in component" this)
                 (.error js/console e)))))))

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
(def to-run [])
(def frame-count 0)

(defn flush!
  []
  (when-not ^:boolean (empty? to-render)
    (doseq [c to-render]
      (force-update! c))
    (set! to-render #{}))

  (when-not ^:boolean (empty? to-run)
    (doseq [f to-run] (f))
    (set! to-run [])))

(defn render-loop
  [frame-ms]
  (set! frame-count (inc frame-count))
  (when ^:boolean (and (true? count-fps?) (identical? 0 (mod frame-count 29)))
    (set! frame-rate (* 1000 (/ 30 (- frame-ms last-fps-time))))
    (set! last-fps-time frame-ms))

  (flush!)

  (js/requestAnimationFrame render-loop))

(defonce _render-loop (js/requestAnimationFrame render-loop))

(defn force-update [this]
  (set! to-render (conj to-render this)))

(defn schedule! [f]
  (set! to-run (conj to-run f)))

;; https://github.com/omcljs/om/blob/master/src/main/om/next.cljs#L745
(defn ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (aget component "refs") (aget name)))

(defn dom-node
  "Return dom node for component"
  [component]
  (.findDOMNode js/ReactDOM component))

(defn focus!
  "Schedules a focus action for a component"
  [component] (schedule! #(.focus (dom-node component))))

;; State manipulation

(declare update-state!)

(defn reset-state! [this new-state]
  (when (not= new-state (aget this "$reView" "state"))
    (aset this "$reView" "prev-state" (aget this "$reView" "state"))
    (aset this "$reView" "state" new-state)
    (when-let [will-receive (aget this "componentWillReceiveState")]
      (.call will-receive this))

    ;; we do not call shouldComponentUpdate here, choosing instead to always re-render
    ;; on setState.
    (when *trigger-state-render* (force-update this))

    (aset this "$reView" "prev-state" new-state)))

(defn swap-state! [this f & args]
  (reset-state! this (apply f (cons (:state this) args))))

(def base-mixin-before
  {"constructor"       (fn [this $props]
                         (aset this "$reView" #js {"props"    (some-> $props (.-cljs$props))
                                                   "children" (some-> $props (.-cljs$children))})
                         ;; initialize state
                         (when-let [initial-state-f (aget this "$getInitialState")]
                           (let [initial-state (initial-state-f this)]
                             (aset this "$reView" "state" initial-state)
                             (aset this "$reView" "prev-state" initial-state)))
                         this)
   :will-receive-props (fn [this props]
                         (let [{prev-props :props prev-children :children} this
                               next-props (aget props "cljs$props")]
                           (aset this "$reView" "props" next-props)
                           (aset this "$reView" "prev-props" prev-props)
                           (aset this "$reView" "children" (aget props "cljs$children"))
                           (aset this "$reView" "prev-children" prev-children)))})
(def base-mixin-after
  {
   :should-update (fn [this] (not= (:props this) (:prev-props this)))
   :will-unmount  #(set! (.-unmounted %) true)})

(def reactive-mixin
  {:will-unmount #(some-> (.-reactiveUnsubscribe %) (.call))
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

        (fn? f) (bind* f)

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
       (reduce-kv (fn [js-m method-k method]
                    (doto js-m
                      (aset (get kmap method-k (camelCase (name method-k)))
                            (bind method-k method)))) #js {})))

(defn specify-protocols [o]
  (specify! o
    ILookup
    (-lookup
      ([this k]
       (when-not ^:boolean (nil? *lookup-log*) (swap! *lookup-log* conj k))
       (gobj/getValueByKeys this #js ["$reView" (name k)]))
      ([this k not-found]
       (when-not ^:boolean (nil? *lookup-log*) (swap! *lookup-log* conj k))
       (gobj/getValueByKeys this #js ["$reView" (name k)] not-found)))

    ISwap
    (-swap!
      ([this f] (swap-state! this f))
      ([this f a] (swap-state! this f a))
      ([this f a b] (swap-state! this f a b))
      ([this f a b xs] (apply swap-state! (concat (list this f a b) xs))))

    IReset
    (-reset! [this v] (reset-state! this v))))

(defn factory
  [class]
  (doto (fn view-f
          [& children]
          (let [[props children] (cond (empty? children) nil
                                       (or (map? (first children))
                                           (nil? (first children))) [(first children) (rest children)]
                                       :else [nil children])
                {:keys [ref key] :as props} props]
            (js/React.createElement
              class
              #js {"key"           (or
                                     key
                                     (let [key (aget class "prototype" "key")]
                                       (cond (string? key) key
                                             (fn? key) (.call key {:props    props
                                                                   :children children})
                                             :else nil)))
                   "ref"           ref
                   "cljs$props"    (dissoc props :ref :key)
                   "cljs$children" children})))
    (aset "$reView" #js {})))

(defn extends [child parent]
  (let [ReactView (aget child "constructor")]
    (set! (.-prototype ReactView) (new parent))
    (doseq [k (.keys js/Object child)]
      (let [f (aget child k)]
        (aset ReactView "prototype" k f)))
    ReactView))

(specify-protocols (.-prototype js/React.Component))

(defn react-class [methods]
  (-> (wrap-lifecycle-methods methods)
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
       (or (.-$reView x)
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