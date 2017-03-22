(ns re-db.d
  (:refer-clojure :exclude [get get-in contains? select-keys namespace])
  (:require [re-db.core :as d])
  (:require-macros [re-db.d]))

(defonce ^:dynamic *db* (d/create {}))

(defn prefix-atom
  ([f] (prefix-atom f false))
  ([f read?] (fn [& args]
               (let [db (or *db* *db*)]
                 (apply f (cond-> db read? deref) args)))))

(def entity (prefix-atom d/entity true))
(def contains? (prefix-atom d/contains? true))
(def entities (prefix-atom d/entities true))
(def entity-ids (prefix-atom d/entity-ids true))
(def get (prefix-atom d/get true))
(def get-in (prefix-atom d/get-in true))
(def touch (prefix-atom d/touch true))
(def select-keys (prefix-atom d/select-keys true))
(def transaction (prefix-atom d/transaction true))


(def transact! (prefix-atom d/transact!))
(def listen! (prefix-atom d/listen!))
(def unlisten! (prefix-atom d/unlisten!))
(def merge-schema! (prefix-atom d/merge-schema!))
(def squuid d/squuid)
(def namespace (prefix-atom d/namespace true))