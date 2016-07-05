(ns re-db.d
  (:refer-clojure :exclude [get get-in])
  (:require-macros [re-db.d])
  (:require [re-db.core :as d]))

(def ^:dynamic *db* (d/create {}))

(defn prefix-atom
  ([f] (prefix-atom f false))
  ([f read?] (fn [& args]
               (let [db (or *db* *db*)]
                 (apply f (cons (cond-> db read? deref) args))))))

(def entity (prefix-atom d/entity true))
(def has? (prefix-atom d/has? true))
(def entities (prefix-atom d/entities true))
(def entity-ids (prefix-atom d/entity-ids true))
(def get (prefix-atom d/get true))
(def get-in (prefix-atom d/get-in true))
(def transaction (prefix-atom d/transaction true))

(def transact! (prefix-atom d/transact!))
(def listen! (prefix-atom d/listen!))
(def unlisten! (prefix-atom d/unlisten!))
(def listen-attr! (prefix-atom d/listen-attr!))
(def unlisten-attr! (prefix-atom d/unlisten-attr!))
(def merge-schema! (prefix-atom d/merge-schema!))
(def squuid d/squuid)


























