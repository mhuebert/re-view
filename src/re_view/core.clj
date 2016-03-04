(ns re-view.core
  (:require [cljs.core :refer [specify!]]
            [clojure.string :as string]))

;; for brevity - aliasing namespaced symbols to use in macros
(def parse-props 're-view.core/parse-props)
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
        (let [initial-state-f# ~f
              state# (~'js->clj {})
              initial-val# (if initial-state-f# (initial-state-f# this#) nil)]
          ;; can't use #js shorthand here &
          ;; also can't use the recursive clj->js fn,
          ;; so we do this, unfortunately.
          (~'aset state# "cljs$state" initial-val#)
          (~'aset state# "cljs$nextState" initial-val#)
          state#)))
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
              update?# (if should-update#
                         (should-update# this#
                                         (~parse-props next-props#)
                                         (~next-state this#)) true)]
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
        (~f this# (~prev-props this#) (~prev-state this#))))})

(defn wrap-lifecycle-methods [parsed-methods]
  ;; always wrap 'shouldComponentUpdate and 'componentWillUpdate, even if they aren't provided,
  ;; because this is where we advance state
  (for [name (into #{'shouldComponentUpdate
                     'componentWillUpdate
                     'getInitialState} (keys parsed-methods))
        :let [{:keys [fn form]} (get parsed-methods name)]]
    (if-let [wrap-f (get lifecycle-wrap-fns name)] (wrap-f fn) form)))

(defn camelCase
  "Convert dash-ed and name/spaced-keywords to dashEd and name_spacedKeywords"
  [s]
  (clojure.string/replace s #"([^\\-])-([^\\-])" (fn [[_ m1 m2]]
                                                   (str m1 (clojure.string/upper-case m2)))))



(defn parse-lifecycle-map [m]
  (reduce-kv (fn [m kw-name [_ args & body :as f]]
               (let [method-name (symbol (camelCase (name kw-name)))]
                 (assoc m method-name {:name method-name
                                       :form `(~method-name ~args ~@body)
                                       :fn   f})))
             {}
             m))

(defn parse-lifecycle-methods [forms]
  (if (map? forms)
    (parse-lifecycle-map forms)
    (reduce (fn [m [name args & body :as form]]
              (assoc m name {:name name
                             :form form
                             :fn   `(~'fn ~args ~@body)}))
            {}
            forms)))

(defn react-class [display-name methods]
  `(~'-> (specify!
           (~'clj->js {:displayName ~(some-> display-name str)})
           ~'Object
           ~@(wrap-lifecycle-methods methods))
     (~'js/React.createClass)))

(defmacro defcomponent [name & lifecycle-methods]
  `(def ~name
     (~'re-view.core/factory
       ~(react-class name (parse-lifecycle-methods lifecycle-methods)))))

(defmacro component
  ([method-map] `(~'re-view.core/component nil ~method-map))
  ([display-name method-map]
   `(~'re-view.core/factory
      ~(react-class display-name (parse-lifecycle-map method-map)))))