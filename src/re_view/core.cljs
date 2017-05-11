(ns re-view.core
  (:refer-clojure :exclude [partial])
  (:require-macros [re-view.core])
  (:require [re-db.core :as d]
            [re-db.patterns :as patterns :include-macros true]
            [re-view.render-loop :as render-loop]
            [re-view.hiccup :as hiccup]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [re-view.util :refer [camelCase]]
            [cljsjs.react]))

(def schedule! render-loop/schedule!)
(def force-update render-loop/force-update)
(def force-update! render-loop/force-update!)
(def flush! render-loop/flush!)

(def ^:dynamic *trigger-state-render* true)

(defn dom-node
  "Return DOM node for component"
  [component]
  (.findDOMNode js/ReactDOM component))

(defn focus
  "Focus the first input or textarea in a component"
  [component]
  (let [node (dom-node component)
        p #(#{"INPUT" "TEXTAREA"} (.-tagName %))]
    (if (p node)
      (.focus node)
      (some-> (gdom/findNode node p)
              (.focus)))))

(defn respond-to-changed-state
  "When a state atom has changed, calls lifecycle method and schedules an update."
  [this]
  (when-let [will-receive (aget this "componentWillReceiveState")]
    (.call will-receive this))
  (when *trigger-state-render*
    (force-update this)))

(defn mounted?
  "Returns true if component is still mounted to the DOM.
  This is necessary to avoid updating unmounted components."
  [this]
  (not (true? (aget this "unmounted"))))

(defn reactive-render
  "Wrap a render function to force-update the component when re-db patterns accessed during evaluation are invalidated."
  [f]
  (fn []
    (this-as this
      (let [{:keys [patterns value]} (patterns/capture-patterns (apply f this (aget this "re$view" "children")))
            prev-patterns (aget this "re$view" "dbPatterns")]
        (when-not (= prev-patterns patterns)
          (some-> (aget this "reactiveUnsubscribe") (.call))
          (aset this "reactiveUnsubscribe" (when-not (empty? patterns)
                                             (re-db.d/listen patterns #(force-update this))))
          (aset this "re$view" "dbPatterns" patterns))
        value))))

(def kmap
  "Mapping of convenience keys to React lifecycle method keys."
  {:constructor        "constructor"
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

(defn compseq
  "Compose fns to execute sequentially over the same arguments"
  [& fns]
  (fn [& args]
    (doseq [f fns]
      (apply f args))))

(defn wrap-should-update
  "Evaluate fns sequentially, stopping if any return true."
  [fns]
  (fn [this]
    (loop [fns fns]
      (if (empty? fns)
        false
        (or ((first fns) this)
            (recur (rest fns)))))))

(defn collect
  "Merge a list of method maps, preserving special behavour of :should-update and wrapping methods with the same key to execute sequentially."
  [methods]
  (let [methods (apply merge-with (fn [a b] (if (vector? a) (conj a b) [a b])) methods)]
    (reduce-kv (fn [m method-k fns]
                 (cond-> m
                         (vector? fns) (assoc method-k (if (keyword-identical? method-k :should-update)
                                                         (wrap-should-update fns)
                                                         (apply compseq fns))))) methods methods)))

(defn wrap-methods
  "Wrap a component's methods, binding arguments and specifying lifecycle update behaviour."
  [method-k f]
  (if-not (fn? f)
    f
    (case method-k
      (:initial-state
        :key
        :constructor) f
      :render (reactive-render f)
      :will-receive-props
      (fn [props]
        (binding [*trigger-state-render* false]
          (f (js-this) props)))
      (:will-mount :will-unmount :will-receive-state :will-update)
      (fn []
        (binding [*trigger-state-render* false]
          (apply f (js-this) (aget (js-this) "re$view" "children"))))
      (:did-mount :did-update)
      (fn []
        (apply f (js-this) (aget (js-this) "re$view" "children")))
      (fn [& args]
        (apply f (js-this) args)))))

(defn init-state
  "Return a state atom for component. The component will update when it changes."
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
  "When a component is instantiated, bind element methods and populate initial props."
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
  "Augment lifecycle methods with default behaviour."
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
                    (assoc m method-k (wrap-methods method-k method))) {})))

(defn is-react-element? [x]
  (and x
       (or (boolean (aget x "re$view"))
           (.isValidElement js/React x))))

(defn ensure-state [this]
  (when-not (.hasOwnProperty (aget this "re$view") "state")
    (init-state this nil)))

(defn view-var [k]
  (and (keyword? k)
       (= "view" (namespace k))
       (camelCase k)))

(defn specify-protocols
  "Implement ILookup protocol to read prop keys and `view`-namespaced keys on a component."
  [o]
  (specify! o
    ILookup
    (-lookup
      ([this k]
       (if-let [re-view-var (view-var k)]
         (do (when (= re-view-var "state") (ensure-state this))
             (aget this "re$view" re-view-var))
         (get (aget this "re$view" "props") k)))
      ([this k not-found]
       (if (or (contains? (aget this "re$view" "props") k)
               (.hasOwnProperty (aget this "re$view") (view-var k)))
         (get this k)
         not-found)))))

(defn swap-silently!
  "Swap a component's state atom without forcing an update (render)"
  [& args]
  (binding [*trigger-state-render* false]
    (apply swap! args)))

(defn- mock
  "Initialize an unmounted element, from which props and instance methods can be read."
  [element]
  (doto #js {}
    (init-props (aget element "props"))
    (specify-protocols)))

(defn element-get
  "'Get' from an unmounted element"
  [element k]
  (or (some-> (aget element "type") (aget (camelCase k)))
      (get (mock element) k)))

(defn init-element
  "Body of constructor function for ReView component."
  [this $props]
  (init-props this $props)
  (when-let [initial-state (aget this "$getInitialState")]
    (init-state this (cond-> initial-state
                             (fn? initial-state) (apply this (aget this "re$view" "children")))))
  this)

(specify-protocols (.-prototype js/React.Component))

(defn react-component
  "Extend React.Component with lifecycle methods of a view"
  [lifecycle-methods]
  (doto (fn ReView [$props]
          (init-element (js-this) $props))
    (aset "prototype" (->> lifecycle-methods
                           (reduce-kv (fn [m k v]
                                        (doto m (aset (get kmap k) v))) (new js/React.Component))))))

(defn factory
  "Return a function which, when called with props and children, returns a React element."
  [constructor element-keys]
  (fn [props & children]
    (let [[{prop-key :key
            ref      :ref
            :as      props} children] (cond (or (map? props)
                                                (nil? props)) [props children]
                                            (and (object? props)
                                                 (not (.isValidElement js/React props))) [(js->clj props :keywordize-keys true) children]
                                            :else [nil (cons props children)])]
      (.createElement js/React constructor
                      (cond-> #js {"key"         (or prop-key
                                                     (when-let [class-key (.-key constructor)]
                                                       (cond (string? class-key) class-key
                                                             (keyword? class-key) (get props class-key)
                                                             (fn? class-key) (apply class-key props children)
                                                             :else (throw (js/Error "Invalid key supplied to component"))))
                                                     (.-displayName constructor))
                                   "ref"         ref
                                   "re$props"    (dissoc props :ref)
                                   "re$children" children
                                   "re$element"  element-keys}
                              )))))

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
           class-keys
           element-keys] :as re-view-base}]
  (let [class (->> (wrap-lifecycle-methods lifecycle-methods)
                   (react-component))]
    (doseq [[k v] (seq class-keys)]
      (aset class (camelCase k) v))
    (doto (factory class element-keys)
      (aset "reViewBase" re-view-base))))

(defn render-to-element
  "Render views to page. Element should be HTML Element or ID."
  [component element]
  (.render js/ReactDOM component (cond->> element
                                          (string? element)
                                          (.getElementById js/document))))

(defn partial
  "Partially apply props and optional class-keys to base view. Props specified at runtime will overwrite those given here."
  ([base props]
   (fn [& args]
     (let [[user-props & children] (cond->> args
                                            (not (map? (first args))) (cons {}))]
       (apply base (merge props user-props) children))))
  ([base class-keys props]
   (partial (view* (-> (aget base "reViewBase")
                       (update :class-keys merge class-keys))) props)))



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