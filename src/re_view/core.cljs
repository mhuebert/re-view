(ns re-view.core
  (:require-macros [re-view.core])
  (:require [re-db.core :as d]
            [re-view.render-loop :as render-loop]
            [re-view-hiccup.core :as hiccup]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [cljsjs.react]))

(def schedule! render-loop/schedule!)
(def force-update render-loop/force-update)
(def force-update! render-loop/force-update!)
(def flush! render-loop/flush!)

(def ^:dynamic *trigger-state-render* true)

(defn camelCase
  "Return camelCased string, eg. hello-there to helloThere. Does not modify existing case."
  [s]
  (clojure.string/replace s #"-(.)" (fn [[_ match]] (clojure.string/upper-case match))))

(defn dom-node
  "Return DOM node for component"
  [component]
  (.findDOMNode js/ReactDOM component))

(defn focus
  "Focus the first input|textarea in a component"
  [component]
  (let [node (dom-node component)
        p #(#{"INPUT" "TEXTAREA"} (.-tagName %))]
    (if (p node)
      (.focus node)
      (some-> (gdom/findNode node p)
              (.focus)))))

(defn respond-to-changed-state
  "Calls lifecycle method and triggers async render"
  [this]
  (when-let [will-receive (aget this "componentWillReceiveState")]
    (.call will-receive this))
  (when *trigger-state-render*
    (force-update this)))

(defn mounted?
  "Return false if component has unmounted.
  (necessary to track mounted state to avoid async render of unmounted component)"
  [this]
  (not (true? (aget this "unmounted"))))

(defn reactive-render
  "Wraps a render function to record database reads and listen to accessed patterns."
  [f this]
  (let [{:keys [patterns value]} (d/capture-patterns (apply f this (get this :view/children)))
        prev-patterns (aget this "reactivePatterns")]
    (when-not (= prev-patterns patterns)
      (some-> (aget this "reactiveUnsubscribe") (.call))
      (aset this "reactiveUnsubscribe" (when-not (empty? patterns)
                                         (apply re-db.d/listen! (conj (vec patterns) #(force-update this)))))
      (aset this "reactivePatterns" patterns))
    value))

(def kmap {:constructor        "constructor"
           :initial-state      "$getInitialState"
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
  (if-not (.isValidElement js/React element) (hiccup/element element) element))

(defn as-list [items]
  (if (vector? items)
    (seq (remove nil? items))
    [items]))

(defn compseq
  "Compose fns to execute sequentially over the same arguments"
  [& fns]
  (fn [& args]
    (doseq [f fns]
      (apply f args))))

(defn wrap-methods
  [method-k f]
  (case method-k
    :render (partial reactive-render f)
    :should-update (if (vector? f)
                     (fn [this]
                       (first (for [f (if (vector? f) f [f])
                                    :let [update? (f this)]
                                    :when update?]
                                true)))
                     f)
    (if (vector? f) (apply compseq f)
                    f)))

(defn collect [methods]
  (->> (apply merge-with (fn [a b] (if (vector? a) (conj a b) [a b])) methods)
       (reduce-kv (fn [m method-k f]
                    (assoc m method-k (wrap-methods method-k f))) {})))

(defn bind
  "Bind methods to be called with their component and its children."
  [method-k f]
  (case method-k
    (:initial-state
      :key
      :constructor) f
    :will-receive-props
    (fn [props]
      (binding [*trigger-state-render* false]
        (this-as this (f this props))))
    (:will-mount :will-unmount :will-receive-state :will-update)
    (fn []
      (binding [*trigger-state-render* false]
        (this-as this
          (apply f this (get this :view/children)))))
    (:did-mount :did-update)
    (fn []
      (this-as this
        (apply f this (get this :view/children))))
    (if (fn? f)
      (fn [& args]
        (this-as this
          (apply f this args)))
      f)))

(defn init-state
  "Returns a new state atom for component."
  [this initial-state]
  (let [a (atom initial-state)]
    (aset this "re$view" "state" a)
    (aset this "re$view" "prevState" initial-state)
    (add-watch a :state-changed (fn [_ _ old-state new-state]
                                  (when (not= old-state new-state)
                                    (aset this "re$view" "prevState" old-state)
                                    (respond-to-changed-state this))))
    a))

(defn init-props
  [this $props]
  (if $props
    (do (aset this "re$view" #js {"props"    (aget $props "re$props")
                                  "children" (aget $props "re$children")})
        (when-let [element-obj (aget $props "re$element")]
          (doseq [k (.keys js/Object element-obj)]
            (let [v (aget element-obj k)]
              (aset this k (if (fn? v)
                             (fn [& args]
                               (apply v this args))
                             v))))))
    (aset this "re$view" #js {"props"    nil
                              "children" nil}))
  this)

(defn wrap-lifecycle-methods
  "Lifecycle methods are wrapped to manage CLJS props and state
   and provide default behaviour."
  [methods]
  (->> (collect [{:will-receive-props (fn [this props]
                                        (let [{prev-props :view/props prev-children :view/children :as this} this]
                                          (let [next-props (aget props "re$props")]
                                            (aset this "re$view" "props" next-props)
                                            (aset this "re$view" "prevProps" prev-props)
                                            (aset this "re$view" "children" (aget props "re$children"))
                                            (aset this "re$view" "prevChildren" prev-children))))}
                 methods
                 {:should-update (fn [{:keys [view/props
                                              view/prev-props
                                              view/children
                                              view/prev-children]}]
                                   (or (not= props prev-props)
                                       (not= children prev-children)))
                  :will-unmount  (fn [{:keys [view/state] :as this}]
                                   (aset this "unmounted" true)
                                   (some-> (aget this "reactiveUnsubscribe") (.call))
                                   (some-> state (remove-watch :state-changed)))
                  :did-update    (fn [this]
                                   (aset this "re$view" "prevState"
                                         (some-> (aget this "re$view" "state")
                                                 (deref))))}])
       (reduce-kv (fn [m method-k method]
                    (assoc m method-k (bind method-k method))) {})))

(defn is-react-element? [x]
  (and x
       (or (boolean (aget x "re$view"))
           (.isValidElement js/React x))))

(defn specify-protocols [o]
  (specify! o
    ILookup
    (-lookup
      ([this k]
       (if-let [re-view-var (and (keyword? k)
                                 (= "view" (namespace k))
                                 (camelCase (name k)))]
         (do (when (and (= re-view-var "state") (not (gobj/containsKey (aget this "re$view") "state")))
               (aset this "re$view" "state" (init-state this nil)))
             (aget this "re$view" re-view-var))
         (get (aget this "re$view" "props") k)))
      ([this k not-found]
       (if-let [re-view-var (and (keyword? k)
                                 (= "view" (namespace k))
                                 (camelCase (name k)))]
         (aget this "re$view" re-view-var)
         (get (aget this "re$view" "props") k not-found))))))

(defn swap-silently!
  "Swap state without causing component to re-render"
  [& args]
  (binding [*trigger-state-render* false]
    (apply swap! args)))

(def ^:export ReactComponent
  (do
    (specify-protocols (.-prototype js/React.Component))
    js/React.Component))

(defn mock
  "Initialize an unmounted element, from which props and instance methods can be read."
  [element]
  (doto #js {}
    (init-props (aget element "props"))
    (specify-protocols)))

(defn element-get
  "'Get' from an unmounted element"
  [element k]
  (or (some-> (aget element "type") (aget (camelCase (name k))))
      (get (mock element) k)))

(defn extend-react-component [constructor display-name docstring child]
  (let [proto (reduce-kv (fn [m k v]
                           (doto m (aset (get kmap k) v))) (new ReactComponent) child)]
    (doto constructor
      (aset "prototype" proto)
      (aset "docstring" docstring)
      (aset "displayName" display-name))))

(defn factory
  [class key display-name element-keys]
  (fn [props & children]
    (let [[{prop-key :key ref :ref :as props} children] (cond (or (map? props)
                                                                  (nil? props)) [props children]
                                                              (and (object? props)
                                                                   (not (.isValidElement js/React props))) [(js->clj props :keywordize-keys true) children]
                                                              :else [nil (cons props children)])]
      (.createElement js/React class
                      #js {"key"         (or prop-key
                                             (when key
                                               (cond (string? key) key
                                                     (keyword? key) (get props key)
                                                     (fn? key) (apply key props children)
                                                     :else (throw (js/Error "Invalid key supplied to component"))))
                                             display-name)
                           "ref"         ref
                           "re$props"    (dissoc props :ref)
                           "re$children" children
                           "re$element"  element-keys}))))

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
  [{:keys [lifecycle-methods
           static-keys
           element-keys
           key
           display-name
           docstring]}]
  (let [constructor (fn Element [$props]
                      (this-as this
                        (init-props this $props)
                        (when-not (undefined? (aget this "$getInitialState"))
                          (let [initial-state (aget this "$getInitialState")]
                            (init-state this (if (fn? initial-state) (apply initial-state this (get this :view/children)) initial-state))))
                        this))
        class (->> (wrap-lifecycle-methods lifecycle-methods)
                   (extend-react-component constructor display-name docstring))]
    (doseq [[k v] (seq static-keys)]
      (aset class k v))
    (factory class key display-name element-keys)))

(defn render-to-element
  "Render views to page. Element should be HTML Element or ID."
  [component element]
  (.render js/ReactDOM component (cond->> element
                                          (string? element)
                                          (.getElementById js/document))))

(comment

  ;; example of component with controlled input

  (ns my-app.core
    (:require [re-view.core :refer [defview]]))

  (defview greeting
           {:initial-state {:first-name "Herbert"}}
           [{:keys [first-name view/state] :as this}]
           [:div
            [:p (str "Hello, " first-name "!")]
            [:input {:value     first-name
                     :on-change #(swap! state assoc :first-name (-> % .-target .-value))}]]))

(defn update-attrs [el f & args]
  (if-not (vector? el)
    el
    (let [attrs? (map? (second el))]
      (into [(el 0) (apply f (if attrs? (el 1) {}) args)]
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