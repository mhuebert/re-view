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

(defmacro defupdate-fx
  "Registers an event handler with re-frame which may return an update
  to the db as well as other coeffects.

  The function will be passed a _map_ containing `db`, eg. {:db db},
  and should return a map containing :db as well as any effects."
  [& args]
  (let [[docstring [event-key [coeffects-sym & arg-syms] & body]] (parse-opt-args [string?] args)]
    `(~'re-frame.core/reg-event-fx
       ~event-key
       (fn [~(or coeffects-sym '_) ~(into '[_] arg-syms)]
         ~@body))))

(defmacro defquery
  "Registers a subscription with re-frame."
  [name argslist & body]
  (let [subscription-key (keyword (str *ns*) (str name))
        arg-syms argslist]
    `(def ~name
       (do
         (re-frame.core/reg-sub
           ~subscription-key
           (fn [_ ~(into '[_] arg-syms)]
             (binding [~'re-view.re-frame-simple/*in-query?* true]
               ~@body)))
         (fn [& args#]
           @(re-frame.core/subscribe
              (into [~subscription-key] args#)))))))