(ns re-view.re-frame-simple
  (:require [re-view.util :refer [parse-opt-args]]))

(defmacro defview [])

(defmacro defupdate
  "Registers an event handler with re-frame which updates the db.
  Argslist should begin with "
  [& args]
  (let [[docstring [event-key [db-sym & arg-syms] & body]] (parse-opt-args [string?] args)]
    `(~'re-frame.core/reg-event-db
       ~event-key
       (fn [~(or db-sym '_) ~(into '[_] arg-syms)]
         ~@body))))

(defmacro defquery
  "Registers a subscription with re-frame."
  [name & args]
  (let [[docstring [argslist & body]] (parse-opt-args [string?] args)
        subscription-key (keyword (str *ns*) (str name))
        arg-syms argslist]
    `(def ~name
      (do
        (re-frame.core/reg-sub
          ~subscription-key
          (fn [~'_ ~(into '[_] arg-syms)]
            (binding [~'re-view.re-frame-simple/*in-query?* true]
              ~@body)))
        (fn [& args#]
          @(re-frame.core/subscribe
             (into [~subscription-key] args#)))))))