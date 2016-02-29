(ns re-view.core
  (:require [cljs.core :refer [specify!]]))

;; must a better way to do this
(def parse-props 're-view.core/parse-props)
(def next-state 're-view.core/next-state)
(def prev-state 're-view.core/prev-state)
(def has-forced-props? 're-view.core/has-forced-props?)
(def forced-props 're-view.core/forced-props)
(def prev-props 're-view.core/prev-props)
(def ^:dynamic *trigger-state-render* 're-view.core/*trigger-state-render*)
(def advance-state 're-view.core/advance-state)

(defn parse-lifecycle-methods [forms]
  (reduce (fn [m [name [& args] & body :as form]]
            (assoc m name {:name name
                           :form form
                           :fn   `(~'fn ~(vec args) ~@body)}))
          {}
          forms))

(def lifecycle-wrap-fns
  {'componentWillMount
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

(defn reshape [parsed-methods]
  (for [name (into #{'shouldComponentUpdate 'componentWillUpdate} (keys parsed-methods))
        :let [{:keys [fn form]} (get parsed-methods name)
              reshape-f (get lifecycle-wrap-fns name)]]
    (if reshape-f (reshape-f fn) form)))

(defn defui* [name forms]
  (let [reshaped-forms (doall (->> forms
                                   parse-lifecycle-methods
                                   reshape))]
    `(def ~name
       (~'-> (specify! (~'clj->js {:getInitialState (fn [] (~'clj->js {}))
                                   :displayName     ~(str name)}) ~'Object
               ~@reshaped-forms)
         (~'js/React.createClass)
         (~'re-view.core/factory)))))

(defmacro defview [name & forms]
  (defui* name forms))