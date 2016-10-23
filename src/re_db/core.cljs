(ns re-db.core
  (:refer-clojure :exclude [get get-in set! update])
  (:require [cljs-uuid-utils.core :as uuid-utils]
            [clojure.set :as set]))

(enable-console-print!)

(def get-in* cljs.core/get-in)
(def get* cljs.core/get)
(def update* cljs.core/update)

;; bind *db-log* to an empty atom and it will maintain :db-before from the first transaction,
;; :db-after for the latest transaction, and :reports of transactions that occur while it is bound.
(def ^:dynamic *db-log* nil)

;; bind *mute* to prevent listeners from being called.
(def ^:dynamic *mute* false)

(defn create
  "Create a new db, with optional schema."
  ([] (create {}))
  ([schema]
   (atom {:data         {}
          :schema       schema
          :listeners    {}
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
      (boolean (get-in* db-snap [:schema a :db/unique]))))

(defn many? [db-snap a]
  (= :db.cardinality/many (get-in* db-snap [:schema a :db/cardinality])))

(defn unique? [db-snap a]
  (boolean (get-in* db-snap [:schema a :db/unique])))

(defn identity? [db-snap a]
  (= :db.unique/identity (get-in* db-snap [:schema a :db/unique])))

(defn resolve-id
  [db-snap id]
  (when id
    (if
      (sequential? id)
      (let [[a v] id]
        (if-not (unique? db-snap a)
          (throw (js/Error (str "Not a unique attribute: " a ", with value: " v)))
          (get-in* db-snap [:index a v])))
      id)))

(defn has? [db-snap e]
  (contains? (:data db-snap) (resolve-id db-snap e)))

(defn- listen-path!
  [db path f]
  (doto db
    (swap! update-in path #((fnil conj #{}) % f))))

(defn listen!
  ([db f]
   (listen-path! db [:listeners :tx-log] f))
  ([db id f]
   (listen-path! db [:listeners :entity id] f))
  ([db id attr f]
   (listen-path! db [:listeners :entity-attr id attr] f)))

(defn listen-attr!
  [db attr f]
  (listen-path! db [:listeners :attr attr] f))


(defn- unlisten-path!
  [db path f]
  (doto db
    (swap! update-in path #(disj % f))))

(defn unlisten!
  ([db f]
   (unlisten-path! db [:listeners :tx-log] f))
  ([db id f]
   (unlisten-path! db [:listeners :entity id] f))
  ([db id attr f]
   (unlisten-path! db [:listeners :entity-attr id attr] f)))

(defn unlisten-attr!
  [db attr f]
  (unlisten-path! db [:listeners :attr attr] f))

(defn entity [db-snap id]
  (when-let [id (resolve-id db-snap id)]
    (get-in* db-snap [:data id])))

(defn get [db-snap id & args]
  (apply get* (cons (entity db-snap id) args)))

(defn get-in [db-snap id ks]
  (get-in* (entity db-snap id) ks))

(defn add-index [[db-snap reports] id a v]
  [(cond (unique? db-snap a)
         (assoc-in db-snap [:index a v] id)

         (index? db-snap a)
         (update-in db-snap [:index a v] (fnil conj #{}) id)

         :else db-snap) reports])

(defn remove-index [[db-snap reports] id a v]
  [(cond (unique? db-snap a)
         (update-in db-snap [:index a] dissoc v)

         (index? db-snap a)
         (update-in db-snap [:index a v] disj id)

         :else db-snap) reports])

(defn clear-empty-ent [[db-snap reports] id]
  [(cond-> db-snap
           (empty? (dissoc (entity db-snap id) :id)) (update* :data dissoc id))
   reports])

(declare retract-attr)

(defn disj-kill
  "m contains set at path attr. disj value from set; if empty, dissoc set."
  [m attr value]
  (if (= #{value} (get* m attr))
    (dissoc m attr)
    (update* m attr disj value)))

(defn add-report [reports id attr value prev-value]
  (-> reports
      (update* :datoms conj [id attr value prev-value])
      (update* :ids conj id)
      (update* :attrs update* attr (fnil conj #{}) id)
      (update* :id-attrs conj [id attr])))

(defn retract-attr-many [[db-snap reports] id attr value]
  (let [id (resolve-id db-snap id)]
    (if (or (not value)
            (= value (get db-snap id attr)))                ;; retract entire attribute
      (reduce (fn [dbx v] (retract-attr-many dbx id attr v))
              [db-snap reports] (get db-snap id attr))
      (-> [(update-in db-snap [:data id] disj-kill attr value)
           (add-report reports id attr nil value)]
          (remove-index id attr value)
          (clear-empty-ent id)))))

(defn retract-attr
  ([[db-snap reports] id attr] (retract-attr [db-snap reports] id attr (get db-snap id attr)))
  ([[db-snap reports] id attr value]
   (if (many? db-snap attr)
     (retract-attr-many [db-snap reports] id attr value)
     (let [id (resolve-id db-snap id)]
       (if-let [prev-val (or value (get db-snap id attr))]

         (-> [(update-in db-snap [:data id] dissoc attr)
              (add-report reports id attr nil prev-val)]
             (remove-index id attr prev-val)
             (clear-empty-ent id))
         [db-snap reports])))))

(defn retract-entity [[db-snap reports] id]
  (let [id (resolve-id db-snap id)]
    (reduce (fn [[db reports] [a v]] (retract-attr [db reports] id a v))
            [db-snap reports]
            (entity db-snap id))))

(defn duplicate-on-unique? [db-snap id a v]
  (and (unique? db-snap a)
       (entity db-snap [a v])
       (not= id (:id (entity db-snap [a v])))))

(defn add
  ([[db-snap reports] id attr val]
   {:pre [(not (duplicate-on-unique? db-snap id attr val))]}

   (when-let [{:keys [f message] :as validation} (get-in* db-snap [:schema attr :validate])]
     (or (f val) (throw js/Error (str "Validation failed for " attr ": " message " on " val))))

   (let [many? (many? db-snap attr)
         ;multi-many? (and many? (sequential? val))
         no-op? (if many?
                  (contains? (get* (entity db-snap id) attr) val)
                  ;; some invalid values throw when compared, eg `(map inc (take 2))`
                  (try (= val (get db-snap id attr))
                       (catch js/Error e false)))]
     (cond
       no-op? [db-snap reports]

       ;multi-many? (reduce #(add %1 id attr %2) [db-snap reports] val)

       many? (-> [(update-in db-snap [:data id attr] (fnil conj #{}) val)
                  (add-report reports id attr val nil)]
                 (add-index id attr val))

       :else (let [prev-val (get* (entity db-snap id) attr)]
               (-> [(assoc-in db-snap [:data id attr] val)
                    (add-report reports id attr val prev-val)]
                   (add-index id attr val)
                   (remove-index id attr prev-val)))))))

(defn update-attr [[db-snap reports] id attr f & args]
  {:pre [(not (many? db-snap attr))]}
  (assert (not (many? db-snap attr)) "Cannot update a :many attribute")
  (let [new-val (apply f (cons (get db-snap id attr) args))]
    (add [db-snap reports] id attr new-val)))

(def db-f
  {:db/retract-entity retract-entity
   :db/retract-attr   retract-attr
   :db/add            add
   :db/update-attr    update-attr})

(defn get-id [tx]
  (cond (map? tx) (:id tx)
        :else (second tx)))

(defn map->txs [m]
  (if (map? m)
    (when (> (count m) 1)
      (map (fn [[a v]] (if (nil? v)
                         [:db/retract-attr (:id m) a]
                         [:db/add (:id m) a v])) m))
    (list m)) )

(defn swap-id [tx id]
  (cond (map? tx) (assoc tx :id id)
        :else (assoc tx 1 id)))

(defn resolve-ids [db-snap txs]
  (loop [remaining txs
         out []]
    (if-let [tx (first remaining)]
      (let [id (get-id tx)
            new-id (resolve-id db-snap id)]
        (recur
          (rest remaining)
          (conj out (cond-> tx
                            (and new-id (not= new-id id)) (swap-id new-id)))))
      out)))

(defn notify-listeners [{:keys [db-before db-after] {:keys [datoms ids attrs id-attrs]} :reports}]
  (let [metadata {:db-before db-before
                  :db-after  db-after
                  :datoms    datoms}]

    (when (seq datoms)
      (when (seq (get-in* db-after [:listeners :entity]))
        (doseq [id ids]
          (doseq [f (get-in* db-after [:listeners :entity id])]
            (f (entity db-after id) metadata))))

      (when (seq (get-in* db-after [:listeners :entity-attr]))
        (doseq [[id attr] id-attrs]
          (let [listeners (get-in* db-after [:listeners :entity-attr id attr])]
            (doseq [f listeners]
              (f (get db-after id attr))))))

      (when (seq (get-in* db-after [:listeners :attr]))
        (doseq [attr (keys attrs)]
          (doseq [f (get-in* db-after [:listeners :attr attr])]
            (f (get* attrs attr)))))

      (doseq [listener (get-in* db-after [:listeners :tx-log])]
        (listener (remove nil? datoms))))))

(defn transaction [db-before new-txs]
  (let [txs (->> new-txs
                 (resolve-ids db-before)
                 (mapcat map->txs)
                 (remove nil?))
        [db-after reports] (reduce
                             (fn [[snapshot reports] tx]
                               (if-let [f (get* db-f (first tx))]
                                 (apply f (cons [snapshot reports] (rest tx)))
                                 (throw (js/Error (str "No db function: " (first tx) tx)))))
                             [db-before (or (some-> *db-log* deref :reports)
                                            {:datoms   []
                                             :ids      #{}
                                             :attrs    {}
                                             :id-attrs #{}})]
                             txs)]
    {:db-before db-before
     :db-after  db-after
     :reports   reports}))

(defn transact! [db txs]
  (when-let [{:keys [db-after] :as tx} (cond (nil? txs) nil
                                             (and (map? txs) (contains? txs :reports)) txs
                                             (sequential? txs) (transaction @db txs)
                                             :else (throw (js/Error "Transact! was not passed a valid transaction")))]
    (reset! db db-after)
    (when *db-log* (reset! *db-log* (cond-> tx
                                            (:db-before @*db-log*) (assoc :db-before @*db-log*))))
    (when-not *mute* (notify-listeners tx))
    tx))

(defn update-attr! [db id attr & args]
  (transact! db [(into [:db/update-attr id attr] args)]))

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