(ns re-db.d
  (:refer-clojure :exclude [get get-in contains? select-keys namespace])
  (:require [re-db.core :as d])
  (:require-macros [re-db.d]))

(defonce ^:dynamic *db* (d/create {}))

(defn partial-deref
  "Partially apply a (an atom) to f, but deref the atom at time of application."
  [a f deref?]
  (fn [& args]
    (apply f @a args)))

(def entity (partial-deref *db* d/entity true))
(def get (partial-deref *db* d/get true))
(def get-in (partial-deref *db* d/get-in true))
(def select-keys (partial-deref *db* d/select-keys true))

(def entity-ids (partial-deref *db* d/entity-ids true))
(def entities (partial-deref *db* d/entities true))

(def contains? (partial-deref *db* d/contains? true))
(def touch (partial-deref *db* d/touch true))

(def transact! (partial *db* d/transact!))
(def listen! (partial *db* d/listen!))
(def unlisten! (partial *db* d/unlisten!))
(def merge-schema! (partial *db* d/merge-schema!))

(def squuid d/squuid)
(def capture-patterns* d/capture-patterns*)