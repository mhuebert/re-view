(ns re-db.core
  (:refer-clojure :exclude [get get-in set! update])
  (:require [cljs-uuid-utils.core :as uuid-utils]
            [clojure.set :as set]))

(enable-console-print!)

(def get-in* cljs.core/get-in)
(def get* cljs.core/get)
(def update* cljs.core/update)

(def error-messages
  {:add!-missing-entity
   (fn [attr id]
     (str "The entity you want to add a " attr " to, " id ", does not exist.\n"
          (when (sequential? id)
            (str "To upsert a new entity, call add! with a map, like {"
                 (first id) " " (second id) " :other-attr other-val}"))))})

(defn err [[k & args]]
  (throw (js/Error
           (some-> (get* error-messages k) (apply args)))))


(defn create
  "Create a new db, with optional schema."
  ([] (create {}))
  ([schema]
   (atom {:data         {}
          :schema       (assoc schema
                          :db/id {:validate {:f       number?
                                             :message "Must be a number"}})
          :listeners    {}
          :debug        (:debug schema)
          :entity-count 1})))

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

(defn has? [db-snap e]
  (contains? (:data db-snap) (resolve-id db-snap e)))

(defn create-id! [db]
  (:entity-count (swap! db update* :entity-count
                        (fn [c] (first (filter #(not (has? @db %))
                                               (iterate inc (inc c))))))))

(defn resolve-id
  [db-snap id]
  (when id
    (cond (number? id) id
          (sequential? id) (let [[a v] id]
                             (if-not (unique? db-snap a)
                               (throw (js/Error (str "Not a unique attribute: " a)))
                               (get-in* db-snap [:index a v])))
          :else (do
                  (throw (js/Error (str "Not a valid id: " id)))))))

(defn listen!
  [db key f]
  (swap! db assoc-in [:listeners :tx-log key] f) db)

(defn unlisten!
  [db key]
  (swap! db update-in [:listeners :tx-log] dissoc key)
  db)

(defn upsert-attr? [id]
  (and (number? id) (neg? id)))

(defn entity [db-snap id]
  (when-let [id (resolve-id db-snap id)]
    (get-in* db-snap [:data id])))

(defn get [db-snap id attr]
  (get* (entity db-snap id) attr))

(defn get-in [db-snap id & ks]
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

(defn clear-empty-ent [[db-snap reports] e]
  [(cond-> db-snap
           (empty? (dissoc (entity db-snap e) :db/id)) (update* :data dissoc e))
   reports])

(declare retract-attr)

(defn disj-kill
  "m contains set at path attr. disj value from set; if empty, dissoc set."
  [m attr value]
  (if (= #{value} (get* m attr))
    (dissoc m attr)
    (update* m attr disj value)))

(defn retract-attr-many [[db-snap reports] id attr value]
  (let [id (resolve-id db-snap id)]
    (if (or (not value)
            (= value (get db-snap id attr)))                ;; retract entire attribute
      (reduce (fn [dbx v] (retract-attr-many dbx id attr v))
              [db-snap reports] (get db-snap id attr))
      (-> [(update-in db-snap [:data id] disj-kill attr value)
           (conj reports [id attr nil value])]
          (remove-index id attr value)
          (clear-empty-ent id)))))

(defn retract-attr
  ([dbx id attr] (retract-attr dbx id attr nil))
  ([[db-snap reports] id attr value]
   (if (many? db-snap attr)
     (retract-attr-many [db-snap reports] id attr value)
     (let [id (resolve-id db-snap id)]
       (if-let [prev-val (or value (get db-snap id attr))]

         (-> [(update-in db-snap [:data id] dissoc attr)
              (conj reports [id attr nil prev-val])]
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
       (not= id (:db/id (entity db-snap [a v])))))

(defn add
  ([[db-snap reports] id attr val]
   {:pre [(not (upsert-attr? id))
          (not (duplicate-on-unique? db-snap id attr val))]}

   (when-let [{:keys [f message] :as validation} (get-in* db-snap [:schema attr :validate])]
     (or (f val) (throw js/Error (str "Validation failed for " attr ": " message " on " val))))

   (let [id (resolve-id db-snap id)
         ;; TODO - clarify when we upsert, etc._ (assert (has? db-snap id))
         many? (many? db-snap attr)
         multi-many? (and many? (sequential? val))
         no-op? (if many?
                  (contains? (get* (entity db-snap id) attr) val)
                  ;; some invalid values throw when compared, eg `(map inc (take 2))`
                  (try (= val (get* (entity db-snap id) attr))
                       (catch js/Error e false)))]
     (cond
       no-op? [db-snap reports]

       multi-many? (reduce #(add %1 id attr %2) [db-snap reports] val)

       many? (-> [(update-in db-snap [:data id attr] (fnil conj #{}) val)
                  (conj reports [id attr val nil])]
                 (add-index id attr val))

       :else (let [prev-val (get* (entity db-snap id) attr)]
               (-> [(assoc-in db-snap [:data id attr] val)
                    (conj reports [id attr val prev-val])]
                   (add-index id attr val)
                   (remove-index id attr prev-val)))))))

(defn update [[db-snap reports] id attr f & args]
  {:pre [(not (many? db-snap attr))]}
  (assert (not (many? db-snap attr)) "Cannot update a :many attribute")
  (let [new-val (apply f (cons (get db-snap id attr) args))]
    (add [db-snap reports] id attr new-val)))

(def db-f
  {:db/retract-entity retract-entity
   :db/retract-attr   retract-attr
   :db/add            add
   :db/update         update})

(defn find-id-by-unique-attr [db-snap tx]
  (when (map? tx)
        (when-let [a (->> (keys tx)
                          (filter (partial unique? db-snap))
                          first)]
          (resolve-id db-snap [a (get* tx a)]))))

(defn get-id [tx]
  (cond (map? tx) (:db/id tx)
        :else (second tx)))

(defn map->txs [m]
  (cond->> m
           (map? m) (map (fn [[a v]] [:db/add (:db/id m) a v]) m)
           (not (map? m)) (list)))

(defn temp-id [tx]
  (let [id (get-id tx)]
    (when ((every-pred number? neg?) id) id)))

(defn swap-id [tx id]
  (cond (map? tx) (assoc tx :db/id id)
        :else (assoc tx 1 id)))

(defn resolve-ids [db txs]
  (loop [ids {}
         remaining txs
         out []]
    (if-let [tx (first remaining)]
      (let [id (get-id tx)
            temp-id (temp-id tx)
            new-id (cond temp-id (or (find-id-by-unique-attr @db tx)
                                     (get* ids temp-id)
                                     (create-id! db))
                         (sequential? id) (resolve-id @db id)
                         :else nil)]
        (recur
          (cond-> ids
                  temp-id (assoc temp-id new-id))
          (rest remaining)
          (conj out (cond-> tx
                            new-id (swap-id new-id)))))
      out)))

(defn notify-listeners [db-snap reports]
  (doseq [listener (vals (get-in* db-snap [:listeners :tx-log]))]
    (listener (remove nil? reports))))

(defn transact! [db txs]
  (let [txs (->> txs
                 (resolve-ids db)
                 (mapcat map->txs)
                 (remove nil?))
        db-before @db
        [next-db reports] (reduce
                            (fn [[snapshot reports] tx]
                              (if-let [f (get* db-f (first tx))]
                                (apply f (cons [snapshot reports] (rest tx)))
                                (throw (js/Error (str "No db function: " (first tx) tx)))))
                            [db-before []]
                            txs)]
    (reset! db next-db)
    (notify-listeners @db reports)
    db))

(defn upsert! [db ent]
  {:pre [(map? ent)]}
  (transact! db [(cond-> ent
                         (not (contains? ent :db/id)) (assoc :db/id -1))]))

(defn add!
  ([db ent]
   (upsert! db ent))
  ([db id attr val]
   (when-not (has? @db id) (err [:add!-missing-entity attr id]))
   (transact! db [{:db/id id
                   attr   val}])))

(defn update! [db id & args]
  (transact! db [(into [:db/update id] args)]))

(defn retract!
  ([db id]
   (transact! db [[:db/retract-entity id]]))
  ([db id attr]
   (transact! db [[:db/retract-attr id attr]]))
  ([db id attr val]
   (transact! db [[:db/retract-attr id attr val]])))



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
  (prn (apply entity-ids (cons db-snap qs)))
  (map (partial entity db-snap) (apply entity-ids (cons db-snap qs))))

(defn squuid []
  (str (uuid-utils/make-random-uuid)))