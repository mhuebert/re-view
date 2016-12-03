(ns ^:figwheel-always re-db.core
  (:refer-clojure :exclude [get get-in set! update peek contains?])
  (:require [cljs-uuid-utils.core :as uuid-utils]
            [clojure.set :as set])
  (:require-macros [re-db.core :refer [capture-patterns]]))

(enable-console-print!)

(def get-in* cljs.core/get-in)
(def get* cljs.core/get)
(def update* cljs.core/update)
(def contains?* cljs.core/contains?)

(def ^:dynamic *db-log* nil)                                ;; maintains tx-report while bound
(def ^:dynamic *mute* false)                                ;; ignore listeners
(def ^:dynamic *access-log* nil)                            ;; capture access patterns

(def id-attr :db/id)

(defn create
  "Create a new db, with optional schema."
  ([] (create {}))
  ([schema]
   (atom {:data         {}
          :schema       schema
          :debug        (:debug schema)
          :entity-count 1})))

(defn merge-schema!
  [db schema]
  (swap! db update* :schema merge schema))

(defn debug? [db-snap] (:debug db-snap))

(defn db? [conn]
  (satisfies? cljs.core/IDeref conn))

(defn index? [db-snap a]
  (or (true? (get-in* db-snap [:schema a :db/index]))
      (not (nil? (get-in* db-snap [:schema a :db/unique])))))

(defn many? [db-snap a]
  (keyword-identical? :db.cardinality/many (get-in* db-snap [:schema a :db/cardinality])))

(defn unique? [db-snap a]
  (get-in* db-snap [:schema a :db/unique]))

(defn resolve-id
  [db-snap id]
  (if ^:boolean (sequential? id)
    (let [[a v] id]
      (if-not (unique? db-snap a)
        (throw (js/Error (str "Not a unique attribute: " a ", with value: " v)))
        (do (some-> *access-log* (swap! update* :attr-val (fnil conj #{}) [nil a v]))
            (get-in* db-snap [:index a v]))))
    id))

(defn contains? [db-snap e]
  (contains?* (get* db-snap :data) (resolve-id db-snap e)))

(defn- listen-path!
  [db path f]
  (doto db
    (swap! update-in path #((fnil conj #{}) % f))))

(defn- unlisten-path!
  [db path f]
  (doto db
    (swap! update-in path #(disj % f))))

(declare get entity)

(defn push! [a v]
  (.push a v)
  a)

(defn pattern->idx
  "Identify access pattern type"
  [db-snap pattern]
  (let [[id attr val] (mapv #(if (= % '_) nil %) pattern)]
    (cond (and id attr) :entity-attr
          (and attr val) (do (assert (unique? db-snap attr))
                             :attr-val)
          id :id
          attr :attr
          :else nil)))

(defn- pattern->listener-path
  "Listener path for access pattern"
  [db-snap [id attr val :as pattern]]
  (case (pattern->idx db-snap pattern)
    :entity-attr [:listeners :entity-attr id attr]
    :attr-val [:listeners :attr-val attr val]
    :id [:listeners :entity id]
    :attr [:listeners :attr attr]
    nil [:listeners :tx-log]))

(defn- pattern->val
  "Current val from database for access pattern"
  [db-snap [id attr val :as pattern]]
  (case (pattern->idx db-snap pattern)
    :entity-attr (get db-snap id attr)
    :attr-val (get* (entity db-snap [attr val]) id-attr)
    :id (entity db-snap id)
    nil))

(declare listen! unlisten!)

(defn listen-lookup-ref!
  [db pattern f]
  (let [[[lookup-attr lookup-val] attr] pattern]
    (let [unlisten (atom)
          cb #(f (pattern->val @db pattern))]
      (listen! db [nil lookup-attr lookup-val]
               (fn [datom]
                 (let [added? (not (nil? (datom 2)))]
                   (when added? (reset! unlisten (listen! db [(datom 0) attr] cb)))
                   (when @unlisten (@unlisten) (reset! unlisten nil))
                   (cb))))
      (swap! db assoc-in [:listeners :lookup-ref pattern f]
             {:clear #(do (when @unlisten (@unlisten))
                          (swap! db update-in [:listeners :lookup-ref pattern] dissoc f))}))))

(defn unlisten!
  [db & patterns]
  (let [f (last patterns)]
    (doseq [pattern (or (seq (drop-last patterns)) [[]])]
      (if (sequential? (first pattern))
        ((get-in* @db [:listeners :lookup-ref pattern f :clear])))
      (unlisten-path! db (pattern->listener-path @db pattern) f))))

(defn listen!
  [db & patterns]
  (let [f (last patterns)]
    (doseq [pattern (or (seq (drop-last patterns)) [[]])]
      (if (sequential? (first pattern))
        (listen-lookup-ref! db pattern f)
        (listen-path! db (pattern->listener-path @db pattern) f))))
  #(apply unlisten! (cons db patterns)))

(defn entity [db-snap id]
  (when-let [id (resolve-id db-snap id)]
    (some-> *access-log*
            (swap! update* :id (fnil conj #{}) [id]))
    (some-> (get-in* db-snap [:data id])
            (assoc id-attr id))))

(defn get [db-snap id & args]
  (when-let [id (resolve-id db-snap id)]
    (some-> *access-log*
            (swap! update-in [:entity-attr id] (fnil conj #{}) (first args)))

    (apply get* (cons (some-> (get-in* db-snap [:data id])
                              (assoc id-attr id)) args))))

(defn get-in [db-snap id [attr & ks]]
  (get-in* (get db-snap id attr) ks))

(defn add-index [[db-snap datoms] id a v]
  [(cond (unique? db-snap a)
         (assoc-in db-snap [:index a v] id)

         (index? db-snap a)
         (update-in db-snap [:index a v] (fnil conj #{}) id)

         :else db-snap) datoms])

(defn remove-index [[db-snap datoms] id a v]
  [(cond (unique? db-snap a)
         (update-in db-snap [:index a] dissoc v)

         (index? db-snap a)
         (update-in db-snap [:index a v] disj id)

         :else db-snap) datoms])

(defn clear-empty-ent [[db-snap datoms] id]
  [(cond-> db-snap
           (empty? (get-in* db-snap [:data id])) (update* :data dissoc id))
   datoms])

(declare retract-attr)

(defn disj-kill
  "m contains set at path attr. disj value from set; if empty, dissoc set."
  [m attr value]
  (if (= #{value} (get* m attr))
    (dissoc m attr)
    (update* m attr disj value)))

(defn retract-attr-many [[db-snap datoms] id attr value]
  (let [id (resolve-id db-snap id)]
    (if (or (not value)
            (= value (get db-snap id attr)))                ;; retract entire attribute
      (reduce (fn [dbx v] (retract-attr-many dbx id attr v))
              [db-snap datoms] (get db-snap id attr))
      (-> [(update-in db-snap [:data id] disj-kill attr value)
           (push! datoms [id attr nil value])]
          (remove-index id attr value)
          (clear-empty-ent id)))))

(defn retract-attr
  ([state id attr] (retract-attr state id attr (get (state 0) id attr)))
  ([[db-snap datoms :as state] id attr value]
   (if (many? db-snap attr)
     (retract-attr-many state id attr value)
     (if-let [prev-val (or value (get db-snap id attr))]
       (-> [(update-in db-snap [:data id] dissoc attr)
            (push! datoms [id attr nil prev-val])]
           (remove-index id attr prev-val)
           (clear-empty-ent id))
       state))))

(defn retract-entity [state id]
  (reduce (fn [state [a v]]
            (retract-attr state id a v))
          state
          (entity (state 0) id)))

(defn duplicate-on-unique? [db-snap id a v]
  (and (unique? db-snap a)
       (entity db-snap [a v])
       (not= id (get* (entity db-snap [a v]) id-attr))))

(defn add
  [[db-snap datoms :as state] id attr val]
  {:pre [(not (duplicate-on-unique? db-snap id attr val))
         (not= attr id-attr)]}

  (let [many? ^:boolean (many? db-snap attr)
        ;multi-many? (and many? (sequential? val))
        no-op? (if many?
                 (contains?* (get* (entity db-snap id) attr) val)
                 (try (= val (get db-snap id attr))
                      (catch js/Error e false)))]
    (cond
      no-op? state

      ;multi-many? (reduce #(add %1 id attr %2) [db-snap reports] val)

      many? (-> [(update-in db-snap [:data id attr] (fnil conj #{}) val)
                 (push! datoms [id attr val nil])]
                (add-index id attr val))

      :else (let [prev-val (get* (entity db-snap id) attr)]
              (-> [(assoc-in db-snap [:data id attr] val)
                   (push! datoms [id attr val prev-val])]
                  (add-index id attr val)
                  (remove-index id attr prev-val))))))

(defn update-attr [[db-snap datoms] id attr f & args]
  {:pre [(not (many? db-snap attr))]}
  (assert (not (many? db-snap attr)) "Cannot update a :many attribute")
  (let [new-val (apply f (cons (get db-snap id attr) args))]
    (add [db-snap datoms] id attr new-val)))

(defn notify-listeners [{:keys [db-after datoms]}]
  (when-let [listeners (get* db-after :listeners)]
    (let [{entity-f      :entity
           entity-attr-f :entity-attr
           attr-val-f    :attr-val
           attr-f        :attr
           tx-log-f      :tx-log} listeners
          seen-ids (atom #{})]

      (doseq [datom datoms]

        (swap! seen-ids conj (datom 0))

        ;; entity-attr listeners
        (doseq [f (get-in* entity-attr-f [(datom 0) (datom 1)])]
          (f (get db-after (datom 0) (datom 1))))

        ;; attr-val listeners
        (doseq [f (get-in* attr-val-f [(datom 1) (or (datom 2) (datom 3))])]
          (f datom))

        ;; attr listeners
        (doseq [f (get* attr-f (datom 1))] (f datom)))

      (doseq [id @seen-ids]
        (doseq [f (get* entity-f id)]
          (f (entity db-after id))))

      ;; tx-log listeners
      (doseq [f tx-log-f] (f datoms)))))

(defn map->txs!
  [db-snap a m]
  (assert (contains?* m id-attr))
  (when-not (= 1 (count m))
    (let [id (resolve-id db-snap (get* m id-attr))]
      (doseq [[attr val] (dissoc m id-attr)]
        (.push a (if (nil? val) [:db/retract-attr id attr]
                                [:db/add id attr val])))))
  a)

(def ops {:db/add            add
          :db/update-attr    update-attr
          :db/retract-entity retract-entity
          :db/retract-attr   retract-attr})

(defn transaction [db-before new-txs]
  (let [resolve-id #(resolve-id db-before %)
        txs (reduce (fn [txs tx]
                      (if (map? tx) (map->txs! db-before txs tx)
                                    (push! txs (update* tx 1 resolve-id)))) #js [] new-txs)
        [db-after datoms] (reduce (fn [state tx]
                                    (let [f (get* ops (tx 0) #(throw (js/Error (str "No re-db op: " (tx 0)))))]
                                      (apply f (cons state (rest tx)))))
                                  [db-before #js []] txs)]
    {:db-before db-before
     :db-after  db-after
     :datoms    (vec datoms)}))

(defn transact!
  [db txs]
  (when-let [{:keys [db-after] :as tx} (cond (nil? txs) nil
                                             (and (map? txs) (contains?* txs :datoms)) txs
                                             (sequential? txs) (transaction @db txs)
                                             :else (throw (js/Error "Transact! was not passed a valid transaction")))]
    (reset! db db-after)
    (when-not (nil? *db-log*)
      (reset! *db-log* (cond-> tx
                               (:db-before @*db-log*) (assoc :db-before @*db-log*))))
    (when-not (true? *mute*)
      (notify-listeners tx))
    db))

(defn entity-ids
  [db-snap & qs]
  (apply set/intersection
         (map (fn [q]
                (set (if (or (fn? q) (keyword? q))
                       (reduce-kv (fn [s id entity] (if (q entity) (conj s id) s)) #{} (:data db-snap))
                       (let [[attribute value] q]
                         (cond (unique? db-snap attribute)
                               (list (resolve-id db-snap q))

                               (index? db-snap attribute)
                               (get-in* db-snap [:index attribute value])

                               :else (do (when (debug? db-snap) (prn (str "Not an indexed attribute: " attribute)))
                                         (entity-ids db-snap #(= value (get* % attribute)))))))))
              qs)))

(defn entities
  [db-snap & qs]
  (map (partial entity db-snap) (apply entity-ids (cons db-snap qs))))

(defn squuid []
  (str (uuid-utils/make-random-uuid)))

(defn access-log-patterns
  "Returns vector of access log patterns, pruning overlapping patterns"
  [{ids :id attr-vals :attr-val entity-attrs :entity-attr}]
  (reduce-kv
    (fn [patterns id attrs]
      (if (contains?* ids [id])
        patterns
        (apply conj patterns (for [attr attrs]
                               [id attr]))))
    (apply into #{} ids attr-vals)
    entity-attrs))