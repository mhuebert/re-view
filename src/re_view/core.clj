(ns re-view.core
  (:require [cljs.core :refer [specify!]]
            [sablono.core :include-macros true]
            [clojure.string :as string]))

;; for brevity - aliasing namespaced symbols to use in macros
(def parse-props 're-view.core/parse-props)
(def props 're-view.core/props)
(def state 're-view.core/state)
(def next-state 're-view.core/next-state)
(def prev-state 're-view.core/prev-state)
(def has-forced-props? 're-view.core/has-forced-props?)
(def forced-props 're-view.core/forced-props)
(def prev-props 're-view.core/prev-props)
(def ^:dynamic *trigger-state-render* 're-view.core/*trigger-state-render*)
(def advance-state 're-view.core/advance-state)

(def lifecycle-wrap-fns
  {'getInitialState
   (fn [f]
     `(~'getInitialState [this#]
        (let [get-initial-state# ~f]
          (~'js-obj "cljs$state" (if get-initial-state# (get-initial-state# this#) nil)))))
   'componentWillMount
   (fn [f]
     `(~'componentWillMount [this#]
        (~'binding [~*trigger-state-render* false]
          (~f this#))))
   'componentWillReceiveProps
   (fn [f]
     `(~'componentWillReceiveProps [this# next-props#]
        (~'binding [~*trigger-state-render* false]
          (~f this# (~parse-props next-props#)))))
   'shouldComponentUpdate
   (fn [f]
     `(~'shouldComponentUpdate [this# next-props# next-state#]
        (let [should-update# ~f
              next-props# (~parse-props next-props#)
              next-state# (~next-state this#)
              update?# (if should-update#
                         (should-update# this# next-props# next-state#)
                         ;; by default, update if props or state have changed
                         (or (not= next-props# (~props this#))
                             (not= next-state# (~state this#))))]
          (when-not update?# (~advance-state this#))
          update?#)))
   'componentWillUpdate
   (fn [f]
     `(~'componentWillUpdate [this# next-props# next-state#]
        (let [will-update# ~f]
          (when will-update#
            (let [next-props# (if (~has-forced-props? this#) (~forced-props this#)
                                                             (~parse-props next-props#))]
              (will-update# this# next-props# (~next-state this#))))
          (~advance-state this#))))
   'componentDidUpdate
   (fn [f]
     `(~'componentDidUpdate [this# prev-props# prev-state#]
        (~f this# (~prev-props this#) (~prev-state this#))))
   'render
   (fn [f]
     `(~'render [this#]
        (let [element# (~f this#)]
          (if (~'js/React.isValidElement element#)
            element#
            (~'sablono.core/html element#)))))})

(defn wrap-lifecycle-methods
  "Lifecycle methods are wrapped to manage CLJS props and state
   and provide default behaviour."
  [parsed-methods]
  ;; 'shouldComponentUpdate, 'componentWillUpdate, and 'getInitialState
  ;; have default behaviours, so we always 'wrap' them.
  (for [name (into #{'shouldComponentUpdate
                     'componentWillUpdate
                     'getInitialState} (keys parsed-methods))
        :let [{:keys [fn form]} (get parsed-methods name)]]
    (if-let [wrap-f (get lifecycle-wrap-fns name)] (wrap-f fn) form)))

(defn camelCase
  "Convert dash-ed and name/spaced-keywords to dashEd and name_spacedKeywords"
  [s]
  (clojure.string/replace s #"([^\\-])-([^\\-])"
                          (fn [[_ m1 m2]] (str m1 (clojure.string/upper-case m2)))))

(defn parse-lifecycle-map [m]
  (reduce-kv (fn [m kw-name [_ args & body :as f]]
               (let [method-name (symbol (camelCase (name kw-name)))]
                 (assoc m method-name {:name method-name
                                       :form `(~method-name ~args ~@body)
                                       :fn   f}))) {} m))

(defn parse-lifecycle-methods [forms]
  (if (map? forms)
    (parse-lifecycle-map forms)
    (reduce (fn [m [name args & body :as form]]
              (assoc m name {:name name
                             :form form
                             :fn   `(~'fn ~args ~@body)})) {} forms)))

(defn react-class [display-name methods]
  `(~'-> (specify!
           (~'clj->js {:displayName ~(some-> display-name str)})
           ~'Object
           ~@(wrap-lifecycle-methods methods))
     (~'js/React.createClass)))

(defmacro defcomponent
  "Old way of defining components, similar to om. example:

  (defcomponent greeting
    (render [this] [:p \"Hello!\"]))

  Does not play well with Cursive-ide because of undefined symbols."
  [name & lifecycle-methods]
  `(def ~name
     (~'re-view.core/factory
       ~(react-class name (parse-lifecycle-methods lifecycle-methods)))))

(defmacro component
  "Given a map of lifecycle methods, returns a React component factory."
  ([method-map] `(~'re-view.core/component nil ~method-map))
  ([display-name method-map]
   `(~'re-view.core/factory
      ~(react-class display-name (parse-lifecycle-map method-map)))))

(comment

  ;; example of component with controlled input

  (ns my-app.core
    (:require [re-view.core :as view :refer-macros [component]]))

  (def greeting
    (component
      {:get-initial-state (fn [this] {:first-name "Herbert"})
       :render
       (fn [this]
         (let [{:keys [first-name]} (view/state this)]
           [:div
            [:p (str "Hello, " first-name "!")]
            [:input {:value     first-name
                     :on-change #(view/update-state!
                                  this assoc :first-name (-> % .-target .-value))}]]))})))