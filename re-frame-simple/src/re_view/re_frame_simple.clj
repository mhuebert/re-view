(ns re-view.re-frame-simple
  (:require [re-view.util :refer [parse-opt-args]]))

(defmacro defview [])

(defmacro defupdate
  "Registers an event handler with re-frame which updates the db."
  [name & args]
  (let [docstring (when (string? (first args)) (first args))
        [[db-sym & arg-syms] & body] (cond-> args docstring (rest))]
    `(~'re-frame.core/reg-event-db ~name
       (fn [~(or db-sym '_) ~(into '[_] arg-syms)]
         ~@body))))

(defmacro defquery
  "Registers a subscription with re-frame."
  [name & args]
  (let [docstring (when (string? (first args)) (first args))
        [argslist & body] (cond-> args docstring (rest))
        subscription-key (keyword (str *ns*) (str name))
        arg-syms argslist]
    `(def ~name
       (do
         (re-frame.core/reg-sub-raw
           ~subscription-key
           (fn [~'_ ~(into '[_] arg-syms)]
             (~'reagent.ratom/reaction
               (binding [~'re-view.re-frame-simple/*in-query?* true]
                 ~@body))))
         (fn [& args#]
           @(re-frame.core/subscribe
              (into [~subscription-key] args#)))))))