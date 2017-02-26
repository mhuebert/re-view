(ns re-view.core
  (:require-macros [re-view.core])
  (:require [re-db.core :as d]
            [goog.object :as gobj]
            [re-view.shared :refer [*lookup-log*]]
            [re-view.subscriptions :as subs]
            [re-view.render-loop :as render-loop]
            [re-view.hiccup :as hiccup]
            [sablono.core :refer-macros [html]]
            [clojure.string :as string]))

(defonce _ (when (js* "typeof window !== 'undefined'") (render-loop/init)))
(def schedule! render-loop/schedule!)
(def force-update render-loop/force-update)
(def force-update! render-loop/force-update!)
(def flush! render-loop/flush!)

(def ^:dynamic *trigger-state-render* true)

(defn camelCase
  "Convert dash-ed and name/spaced-keywords to strings: dashEd and name_spacedKeywords"
  [s]
  (-> (if (keyword? s) (-> (str s)
                           (subs 1)
                           (string/replace "/" "_")) s)
      (string/replace
        #"([^\\-])-([^\\-])" (fn [[_ m1 m2]] (str m1 (clojure.string/upper-case m2))))))

;; https://github.com/omcljs/om/blob/master/src/main/om/next.cljs#L745
(defn ref
  "Returns the component associated with a component's React ref."
  [component name]
  (some-> (aget component "refs") (aget name)))

(defn dom-node
  "Return dom node for component"
  [component]
  (.findDOMNode js/ReactDOM component))

(defn respond-to-changed-state [this]
  (when-let [will-receive (aget this "componentWillReceiveState")]
    (.call will-receive this))
  (when *trigger-state-render* (force-update this)))

(defn mounted? [this]
  (not (true? (.-unmounted this))))

(defn init-props [this $props]
  (doseq [[k v] (seq (.-re$class $props))]
    (aset this (camelCase k) v))
  (set! (.-re$state this) (if $props (.-re$class $props) {}))
  (when $props
    (set! (.-re$state this)
          (assoc (.-re$state this)
            :view/props (.-re$props $props)
            :view/children (.-re$children $props))))
  this)

(defn reactive-render [this f]
  (let [{:keys [patterns value]} (d/capture-patterns (f))
        prev-patterns (.-reactivePatterns this)]
    (when-not (= prev-patterns patterns)
      (when-let [unsub (.-reactiveUnsubscribe this)]
        (unsub))
      (set! (.-reactiveUnsubscribe this)
            (when-not (empty? patterns)
              (apply re-db.d/listen! (concat patterns (list #(force-update this))))))
      (set! (.-reactivePatterns this) patterns))
    value))

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
  (if-not (js/React.isValidElement element) (hiccup/element element) element))

(defn apply-all [method-k fns]
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
        (reactive-render this #(apply (last fns) (cons this (get this :view/children)))))
      (if (contains? kmap method-k)
        (fn [& args]
          (doseq [f fns] (apply f args)))
        (last fns)))))

(defn collect [methods]
  (->> (apply merge-with (cons (fn [a b] (if (vector? a) (conj a b) [a b])) methods))
       (reduce-kv (fn [m method-k fns]
                    (assoc m method-k (cond->> fns
                                               (contains? kmap method-k)
                                               (apply-all method-k)))) {})))

(defn bind*
  ([f]
   (fn [& args]
     (this-as this
       (apply f (cons this args)))))
  ([f this]
   (fn [& args]
     (apply f (cons this args)))))

(defn bind-without-render* [f]
  (fn [& args]
    (binding [*trigger-state-render* false]
      (this-as this (apply f (cons this args))))))

(defn bind [method-k f]
  (case method-k
    (:initial-state
      :key) f
    (:will-receive-props :will-mount :will-unmount :will-receive-state :will-update)
    (bind-without-render* f)
    (if (and (fn? f)
             (or (not (keyword? method-k))
                 (not= "element" (namespace method-k))))
      (bind* f)
      f)))

(defn update-keys
  "Update keys of map m with function f"
  [update-key-f m]
  (reduce-kv (fn [m key val] (assoc m (update-key-f key) val)) {} m))

(defn get-state-atom
  ([this]
   (or (get (.-re$state this) :view/state-atom)
       (get-state-atom this {})))
  ([this initial-state]
   (let [a (atom initial-state)]
     (set! (.-re$state this) (assoc (.-re$state this)
                               :view/state-atom a
                               :prev-state initial-state))
     (add-watch a :state-changed (fn [_ _ old-state new-state]
                                   (when (not= old-state new-state)
                                     (set! (.-re$state this) (assoc (.-re$state this)
                                                               :prev-state old-state))
                                     (respond-to-changed-state this))))
     a)))

(defn wrap-lifecycle-methods
  "Lifecycle methods are wrapped to manage CLJS props and state
   and provide default behaviour."
  [methods]
  (->> (collect [{:constructor        (fn [this $props]
                                        (init-props this $props)
                                        (when-let [initial-state-f (aget this "$getInitialState")]
                                          (get-state-atom this (initial-state-f this)))
                                        this)
                  :will-receive-props (fn [{prev-props :view/props prev-children :view/children :as this} props]
                                        (let [next-props (aget props "re$props")]
                                          (set! (.-re$state this)
                                                (assoc (.-re$state this)
                                                  :view/props next-props
                                                  :view/prev-props prev-props
                                                  :view/children (.-re$children props)
                                                  :view/prev-children prev-children))))
                  :will-unmount       #(some-> (.-reactiveUnsubscribe %) (.call))}
                 (when (contains? methods :subscriptions) subs/subscription-mixin)
                 methods
                 {:should-update (fn [this] (or (not= (:view/props this) (:view/prev-props this))
                                                (not= (:view/children this) (:view/prev-children this))))
                  :will-unmount  #(set! (.-unmounted %) true)}])
       (reduce-kv (fn [m method-k method]
                    (assoc m method-k (bind method-k method))) {})))

(defn is-react-element? [x]
  (and x
       (or (boolean (.-re$state x))
           (js/React.isValidElement x))))

(defn specify-protocols [o]
  (specify! o
    ILookup
    (-lookup
      ([this k]
       (when-not ^:boolean (nil? *lookup-log*) (swap! *lookup-log* conj k))
       (if (keyword-identical? k :view/state)
         (get-state-atom this)
         (get-in (.-re$state this) [:view/props k]
                 (get (.-re$state this) k))))
      ([this k not-found]
       (when-not ^:boolean (nil? *lookup-log*) (swap! *lookup-log* conj k))
       (if (keyword-identical? k :view/state)
         (get-state-atom this)
         (get-in (.-re$state this) [:view/props k]
                 (get (.-re$state this) k not-found)))))

    ISwap
    (-swap!
      ([this f] (.error js/console "no swapping of component!") #_(swap-state! this f))
      ([this f a] (.error js/console "no swapping of component!") #_(swap-state! this f a))
      ([this f a b] (.error js/console "no swapping of component!") #_(swap-state! this f a b))
      ([this f a b xs] (.error js/console "no swapping of component!") #_(apply swap-state! (concat (list this f a b) xs))))

    IReset
    (-reset! [this v] (.error js/console "no reset of component!") #_(reset-state! this v))))

(specify-protocols (.-prototype js/React.Component))

(defn element-get
  "Get attribute from unmounted React Element"
  [el k]
  (let [mock-component (specify-protocols (init-props #js {} (.-props el)))
        v (get mock-component k)]
    (cond-> v
            (fn? v) (bind* mock-component))))

(defn extends [{:keys [constructor display-name] :as child} parent]
  (let [ReactView constructor]
    (set! (.-prototype ReactView) (reduce-kv (fn [m k v]
                                               (doto m
                                                 (aset (get kmap k (camelCase k)) v))) (new parent) child))
    (set! (.-displayName ReactView) display-name)
    ReactView))

(defn factory
  [{:keys [key display-name] :as methods}]
  (let [class (extends methods js/React.Component)]
    (fn [props & children]
      (let [[{prop-key :key ref :ref :as props} children] (cond (or (map? props)
                                                                    (nil? props)) [props children]
                                                                (and (object? props)
                                                                     (not (js/React.isValidElement props))) [(js->clj props :keywordize-keys true) children]
                                                                :else [nil (cons props children)])]
        (js/React.createElement class
                                #js {"key"         (or prop-key
                                                       ref
                                                       (when key
                                                         (cond (string? key) key
                                                               (keyword? key) (get props key)
                                                               (fn? key) (apply key (cons props children))
                                                               :else (throw (js/Error "Invalid key supplied to component"))))
                                                       display-name)
                                     "ref"         ref
                                     "re$props"    (dissoc props :ref :key :children)
                                     "re$children" (or (get props :children) children)
                                     "re$class"    methods})))))

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
       (wrap-lifecycle-methods)
       (factory)))


(defn render-to-node [component element]
  (js/ReactDOM.render component element))

(defn render-to-id [component id]
  (some->> (.getElementById js/document id)
           (js/ReactDOM.render component)))

(comment

  ;; example of component with controlled input

  (ns my-app.core
    (:require [re-view.core :refer [defview]]))

  (defview greeting
           {:initial-state (fn [this] {:first-name "Herbert"})}
           (fn [{:keys [first-name] :as this}]
             [:div
              [:p (str "Hello, " first-name "!")]
              [:input {:value    first-name
                       :onChange #(swap! this assoc :first-name (-> % .-target .-value))}]])))

(defn update-attrs [el f & args]
  (if-not (vector? el)
    el
    (let [attrs? (map? (second el))]
      (into [(el 0) (apply f (cons (if attrs? (el 1) {}) args))]
            (subvec el (if attrs? 2 1))))))

(defn ensure-keys [forms]
  (let [seen #{}]
    (map-indexed #(update-attrs %2 update :key (fn [k]
                                                 (if (or (nil? k) (contains? seen k))
                                                   %1
                                                   (do (swap! seen conj k)
                                                       k)))) forms)))

(defn map-with-keys [& args]
  (ensure-keys (apply cljs.core/map args)))