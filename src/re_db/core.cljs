(ns ^:figwheel-always re-db.core
  (:refer-clojure :exclude [get get-in select-keys set! peek contains?] :rename {get         get*
                                                                                 get-in      get-in*
                                                                                 contains?   contains?*
                                                                                 select-keys select-keys*})
  (:require [cljs-uuid-utils.core :as uuid-utils]
            [clojure.set :as set])
  (:require-macros [re-db.core :refer [capture-patterns]]))

(enable-console-print!)

(def ^:dynamic *db-log* nil)                                ;; maintains tx-report while bound
(def ^:dynamic *mute* false)                                ;; ignore listeners
(def ^:dynamic *access-log* nil)                            ;; capture access patterns
(def ^:dynamic *debug* false)

(def id-attr :db/id)

(defn create
  "Create a new db, with optional schema, which should be a mapping of attribute keys to
  the following options:

    :db/index       [true, :db.index/unique]
    :db/cardinality [:db.cardinality/many]"
  ([] (create {}))
  ([schema]
   (atom {:data   {}
          :schema schema})))

(defn merge-schema!
  "Merge additional schema options into a db. Indexes are not created for existing data."
  [db schema]
  (swap! db update :schema merge schema))

(defn index?
  "Returns true if attribute is indexed."
  [db-snap a]
  (contains?* (get-in* db-snap [:schema a]) :db/index))

(defn many?
  "Returns true for attributes with cardinality `many`, which store a set of values for each attribute."
  [db-snap a]
  (keyword-identical? :db.cardinality/many (get-in* db-snap [:schema a :db/cardinality])))

(defn unique?
  "Returns true for attributes where :db/index is :db.index/unique."
  [db-snap a]
  (keyword-identical? :db.index/unique (get-in* db-snap [:schema a :db/index])))

(defn resolve-id
  "Returns id, resolving lookup refs (vectors of the form `[attribute value]`) to ids. Lookup refs are only supported for indexed attributes."
  [db-snap id]
  (if ^:boolean (vector? id)
    (let [[attr val] id]
      (if-not (unique? db-snap attr)
        (throw (js/Error (str "Not a unique attribute: " attr ", with value: " val)))
        (do (some-> *access-log* (swap! update :attr-val conj [nil attr val]))
            (get-in* db-snap [:index attr val]))))
    id))

(defn contains?
  "Returns true if entity with given id exists in db."
  [db-snap id]
  (contains?* (get* db-snap :data) (resolve-id db-snap id)))

(defn- listen-path!
  [db path f]
  (doto db
    (swap! update-in path #((fnil conj #{}) % f))))

(defn- unlisten-path!
  [db path f]
  (doto db
    (swap! update-in path disj f)))

(declare get entity)

(defn push! [attr val]
  (.push attr val)
  attr)

(defn pattern->idx
  "Returns type of listener pattern."
  [db-snap pattern]
  (let [[id attr val] (mapv #(if (= % '_) nil %) pattern)]
    (cond (and id attr) :entity-attr
          (and attr val) (do (assert (unique? db-snap attr))
                             :attr-val)
          id :id
          attr :attr
          :else nil)))

(defn- pattern->listener-path
  "Returns path to listener set for the given access pattern."
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
    :attr-val (resolve-id db-snap id)
    :id (entity db-snap id)
    nil))

(declare listen! unlisten!)

(defn- listen-lookup-ref!
  "Listen for a pattern where the id is a lookup ref (Requires an intermediate listener.)"
  [db pattern f]
  (let [[[lookup-attr lookup-val :as lookup-ref] attr] pattern]
    (let [unlisten (atom)
          cb #(f lookup-ref)]
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
  "Remove pattern listeners."
  [db & patterns]
  (let [f (last patterns)]
    (doseq [pattern (or (seq (drop-last patterns)) [[]])]
      (if (vector? (first pattern))
        ((get-in* @db [:listeners :lookup-ref pattern f :clear])))
      (unlisten-path! db (pattern->listener-path @db pattern) f))))

(defn listen!
  "Add pattern listeners. Supported patterns:
   [id attr _]  => :entity-attr
   [_ attr val] => :attr-val
   [id _ _]     => :entity
   [_ attr _]   => :attr"
  [db & patterns]
  (let [f (last patterns)]
    (doseq [pattern (seq (drop-last patterns))]
      (cond (= :tx-log pattern) (listen-path! db [:listeners :tx-log] f)
            (vector? (first pattern)) (listen-lookup-ref! db pattern f)
            :else (listen-path! db (pattern->listener-path @db pattern) f))))
  #(apply unlisten! (cons db patterns)))

(defn entity
  "Returns entity for resolved id."
  [db-snap id]
  (when-let [id (resolve-id db-snap id)]
    (some-> *access-log*
            (swap! update :id conj [id]))
    (some-> (get-in* db-snap [:data id])
            (assoc id-attr id))))

(defn get
  "Get attribute in entity with given id."
  ([db-snap id attr] (get db-snap id attr nil))
  ([db-snap id attr not-found]
   (when-let [id (resolve-id db-snap id)]
     (some-> *access-log*
             (swap! update-in [:entity-attr id] (fnil conj #{}) attr))
     (get-in* db-snap [:data id attr] not-found))))

(defn get-in
  "Get-in the entity with given id."
  [db-snap id ks]
  (when-let [id (resolve-id db-snap id)]
    (apply get-in* (cons db-snap (cons id ks)))))

(defn select-keys
  "Select keys from entity of id"
  [db-snap id ks]
  (when-let [id (resolve-id db-snap id)]
    (some-> *access-log*
            (swap! update-in [:entity-attr id] (fnil into #{}) ks))
    (-> (get-in* db-snap [:data id])
        (assoc id-attr id)
        (select-keys* ks))))

(defn- add-index [[db-snap datoms] id a v]
  [(cond (unique? db-snap a)
         (assoc-in db-snap [:index a v] id)

         (index? db-snap a)
         (update-in db-snap [:index a v] (fnil conj #{}) id)

         :else db-snap) datoms])

(defn- remove-index [[db-snap datoms] id a v]
  [(cond (unique? db-snap a)
         (update-in db-snap [:index a] dissoc v)

         (index? db-snap a)
         (update-in db-snap [:index a v] disj id)

         :else db-snap) datoms])

(defn- clear-empty-ent [[db-snap datoms] id]
  [(cond-> db-snap
           (empty? (get-in* db-snap [:data id])) (update :data dissoc id))
   datoms])

(declare retract-attr)

(defn- disj-kill
  "m contains set at path attr. disj value from set; if empty, dissoc set."
  [m attr value]
  (if (= #{value} (get* m attr))
    (dissoc m attr)
    (update m attr disj value)))

(defn- retract-attr-many [[db-snap datoms] id attr value]
  (let [id (resolve-id db-snap id)]
    (if (or (not value)
            (= value (get-in* db-snap [:data id attr])))    ;; retract entire attribute
      (reduce (fn [dbx v] (retract-attr-many dbx id attr v))
              [db-snap datoms] (get-in* db-snap [:data id attr]))
      (-> [(update-in db-snap [:data id] disj-kill attr value)
           (push! datoms [id attr nil value])]
          (remove-index id attr value)
          (clear-empty-ent id)))))

(defn- retract-attr
  ([state id attr] (retract-attr state id attr (get-in* (state 0) [:data id attr])))
  ([[db-snap datoms :as state] id attr value]
   (if (many? db-snap attr)
     (retract-attr-many state id attr value)
     (let [prev-val (if-not (nil? value) value (get-in* db-snap [:data id attr]))]
       (if-not (nil? prev-val)
         (-> [(update-in db-snap [:data id] dissoc attr)
              (push! datoms [id attr nil prev-val])]
             (remove-index id attr prev-val)
             (clear-empty-ent id))
         state)))))

(defn- retract-entity [state id]
  (reduce (fn [state [a v]]
            (retract-attr state id a v))
          state
          (entity (state 0) id)))

(defn- duplicate-on-unique? [db-snap id a v]
  (and (unique? db-snap a)
       (let [existing-id (resolve-id db-snap [a v])]
         (and existing-id (not= id existing-id)))))

(defn- add
  [[db-snap datoms :as state] id attr val]
  {:pre [(not (duplicate-on-unique? db-snap id attr val))
         (not (keyword-identical? attr id-attr))]}

  (if (many? db-snap attr)
    (if (contains?* (get* (entity db-snap id) attr) val)
      state
      (-> [(update-in db-snap [:data id attr] (fnil conj #{}) val)
           (push! datoms [id attr val nil])]
          (add-index id attr val)))

    (if (= val (get-in* db-snap [:data id attr]))
      state
      (let [prev-val (get* (entity db-snap id) attr)]
        (-> [(assoc-in db-snap [:data id attr] val)
             (push! datoms [id attr val prev-val])]
            (add-index id attr val)
            (remove-index id attr prev-val))))))

(defn- update-attr [[db-snap datoms] id attr f & args]
  {:pre [(not (many? db-snap attr))]}
  (assert (not (many? db-snap attr)) "Cannot update a :many attribute")
  (let [new-val (apply f (cons (get-in* db-snap [:data id attr]) args))]
    (add [db-snap datoms] id attr new-val)))

(defn- notify-listeners [{:keys [db-after datoms]}]
  (when-let [listeners (get* db-after :listeners)]
    (let [seen-ids (atom #{})]

      (doseq [[id a v prev-v :as datom] datoms]

        (swap! seen-ids conj (datom 0))

        ;; entity-attr listeners
        (doseq [f (get-in* listeners [:entity-attr id a])]
          (f datom))

        ;; attr-val listeners
        (doseq [f (get-in* listeners [:attr-val a (or v prev-v)])]
          (f datom))

        ;; attr listeners
        (doseq [f (get-in* listeners [:attr a])]
          (f datom)))

      (doseq [id @seen-ids]
        (doseq [f (get-in* listeners [:entity id])]
          (f id)))

      ;; tx-log listeners
      (doseq [f (get* listeners :tx-log)] (f datoms)))))

(defn- map->txs!
  [m]
  (assert (contains?* m id-attr))
  (when-not (= 1 (count m))
    (let [id (get* m id-attr)]
      (mapv (fn [[attr val]]
              (if (nil? val)
                [:db/retract-attr id attr]
                [:db/add id attr val])) (dissoc m id-attr)))))

(defn- commit-tx [state tx]
  (apply (case (tx 0)
           :db/add add
           :db/update-attr update-attr
           :db/retract-entity retract-entity
           :db/retract-attr retract-attr
           #(throw (js/Error (str "No re-db op: " (tx 0)))))
         (assoc tx 0 state)))

(defn- transaction [db-before new-txs]
  (let [resolve-id #(resolve-id db-before %)
        [db-after datoms] (reduce (fn [state tx]
                                    (if (vector? tx)
                                      (commit-tx state (update tx 1 resolve-id))
                                      (reduce commit-tx state (map->txs! (update tx id-attr resolve-id)))))
                                  [db-before #js []]
                                  new-txs)]
    {:db-before db-before
     :db-after  db-after
     :datoms    (vec datoms)}))

(defn transact!
  ([db txs] (transact! db txs {}))
  ([db txs options]
   (when-let [{:keys [db-after] :as tx} (cond (nil? txs) nil
                                              (and (map? txs) (contains?* txs :datoms)) txs
                                              (or (vector? txs)
                                                  (list? txs)
                                                  (seq? txs)) (transaction @db txs)
                                              :else (throw (js/Error "Transact! was not passed a valid transaction")))]
     (reset! db db-after)
     (when-not (nil? *db-log*)
       (reset! *db-log* (cond-> tx
                                (:db-before @*db-log*) (assoc :db-before @*db-log*))))
     (when-not (or (true? (get* options :mute)) (true? *mute*))
       (notify-listeners tx))
     db)))

(defn entity-ids
  [db-snap & qs]
  (->> qs
       (mapv (fn [q]
               (set (cond (fn? q)
                          (reduce-kv (fn [s id entity] (if ^:boolean (q entity) (conj s id) s)) #{} (get* db-snap :data))
                          (keyword? q)
                          (do (some-> *access-log*
                                      (swap! update :attr conj [nil q nil]))
                              (reduce-kv (fn [s id entity] (if ^:boolean (contains?* entity q) (conj s id) s)) #{} (get* db-snap :data)))
                          :else
                          (let [[attr val] q]
                            (some-> *access-log*
                                    (swap! update :attr-val conj [nil attr val]))
                            (cond (unique? db-snap attr)
                                  [(resolve-id db-snap q)]

                                  (index? db-snap attr)
                                  (get-in* db-snap [:index attr val])

                                  :else (do (when ^:boolean (true? *debug*) (println (str "Not an indexed attribute: " attr)))
                                            (entity-ids db-snap #(= val (get* % attr))))))))))
       (apply set/intersection)))

(defn entities
  [db-snap & qs]
  (map (partial entity db-snap) (apply entity-ids (cons db-snap qs))))

(defn squuid []
  (str (uuid-utils/make-random-uuid)))

(def blank-access-log
  {:id          #{}
   :attr        #{}
   :attr-val    #{}
   :entity-attr {}})

(defn access-log-patterns
  "Returns vector of access log patterns, pruning overlapping patterns"
  [{ids :id attr-vals :attr-val entity-attrs :entity-attr attrs :attr}]
  (reduce-kv
    (fn [patterns id attrs]
      (if (contains?* ids [id])
        patterns
        (apply conj patterns (for [attr attrs]
                               [id attr]))))
    (set/union ids attr-vals attrs)
    entity-attrs))