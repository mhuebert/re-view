(ns re-db.core
  (:refer-clojure :exclude [get])
  (:require [cljs-uuid-utils.core :as uuid-utils]
            [clojure.set :as set]))

(enable-console-print!)

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
  (or (true? (get-in db-snap [:schema a :db/index]))
      (true? (get-in db-snap [:schema a :db/unique]))))

(defn many? [db-snap a]
  (true? (get-in db-snap [:schema a :db/many])))

(defn unique? [db-snap a]
  (true? (get-in db-snap [:schema a :db/unique])))

(defn resolve-id [db-snap id]
  (when id
    (cond (number? id) id
          (sequential? id) (let [[a v] id]
                             (if-not (unique? db-snap a)
                               (throw (js/Error (str "Not a unique attribute: " a)))
                               (get-in db-snap [:index a v])))
          :else (do
                  (throw (js/Error (str "Not a valid id: " id)))))))

(defn listener-path
  [db-snap key path]
  (cond (= '_ (first path))
        [:listeners :attr (second path) key]

        (= '_ (second path))
        [:listeners :entity (resolve-id db-snap (first path)) key]

        :else [:listeners :entity-attr (resolve-id db-snap (first path)) (second path) key]))

(defn listen!
  ([db key f]
   (swap! db assoc-in [:listeners :tx-log key] f) db)
  ([db key path f]
   (swap! db assoc-in (listener-path @db key path) f) db))

(defn unlisten!
  ([db key]
   (swap! db update-in [:listeners :tx-log] dissoc key)
   db)
  ([db key path]
   (swap! db update-in (pop (listener-path @db key path)) dissoc key)
   db))

(defn listen-once!
  [& args]
  (apply listen! (concat (drop-last args)
                         (list (fn [& new-args]
                                 (apply (last args) new-args)
                                 (apply unlisten! (drop-last args)))))))

(defn upsert-attr? [id]
  (and (number? id) (neg? id)))

(defn has? [db-snap e]
  (contains? (:data db-snap) (resolve-id db-snap e)))

(defn entity [db-snap id]
  (when-let [id (resolve-id db-snap id)]
    (get-in db-snap [:data id])))

(defn get [db-snap id attr]
  (cljs.core/get (entity db-snap id) attr))

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
           (empty? (dissoc (entity db-snap e) :db/id)) (update :data dissoc e))
   reports])

(declare retract-attr)

(defn disj-kill
  "m contains set at path attr. disj value from set; if empty, dissoc set."
  [m attr value]
  (if (= #{value} (cljs.core/get m attr))
    (dissoc m attr)
    (update m attr disj value)))

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

   (when-let [{:keys [f message] :as validation} (get-in db-snap [:schema attr :validate])]
     (or (f val) (throw js/Error (str "Validation failed for " attr ": " message " on " val))))

   (let [id (resolve-id db-snap id)
         many? (many? db-snap attr)
         multi-many? (and many? (sequential? val))
         no-op? (if many?
                  (contains? (cljs.core/get (entity db-snap id) attr) val)
                  ;; some invalid values throw when compared, eg `(map inc (take 2))`
                  (try (= val (cljs.core/get (entity db-snap id) attr))
                       (catch js/Error e false)))]
     (cond
       no-op? [db-snap reports]

       multi-many? (reduce #(add %1 id attr %2) [db-snap reports] val)

       many? (-> [(update-in db-snap [:data id attr] (fnil conj #{}) val)
                  (conj reports [id attr val nil])]
                 (add-index id attr val))

       :else (let [prev-val (cljs.core/get (entity db-snap id) attr)]
               (-> [(assoc-in db-snap [:data id attr] val)
                    (conj reports [id attr val prev-val])]
                   (add-index id attr val)
                   (remove-index id attr prev-val)))))))

(defn update! [[db-snap reports] id attr f & args]
  {:pre [(not (many? db-snap attr))]}
  (assert (not (many? db-snap attr)) "Cannot update a :many attribute")
  (let [new-val (apply f (cons (get db-snap id attr) args))]
    (add [db-snap reports] id attr new-val)))

(def db-f
  {:db/retract-entity retract-entity
   :db/retract-attr   retract-attr
   :db/add            add
   :db/update         update!})

(defn create-id! [db]
  (:entity-count (swap! db update :entity-count
                        (fn [c] (first (filter #(not (has? @db %))
                                               (iterate inc (inc c))))))))



(defn find-id-by-unique-attr [db-snap m]
  (when-let [a (->> (keys m)
                    (filter (partial unique? db-snap))
                    first)]
    (resolve-id db-snap [a (cljs.core/get m a)])))

(defn resolve-map-id [db m]
  (assoc m :db/id
           (if-not (upsert-attr? (:db/id m))
             (resolve-id @db (:db/id m))
             (or (find-id-by-unique-attr @db m) (create-id! db)))))

(defn map->txs [db m]
  {:pre [(contains? m :db/id)]}
  (let [{:keys [db/id] :as m} (resolve-map-id db m)]
    (map (fn [[a v]] (list :db/add id a v)) m)))

(defn notify-listeners [db-snap reports]
  (let [called-entities (atom #{})]
    (doseq [report reports]
      (let [e (first report)
            a (second report)]
        ;; attribute listeners
        (doseq [f (vals (get-in db-snap [:listeners :attr a]))]
          (f report))
        ;; entity-attr listeners
        (doseq [f (vals (get-in db-snap [:listeners :entity-attr e a]))]
          (f report))
        (when-not (contains? @called-entities e)
          (swap! called-entities conj e)
          (doseq [f (vals (get-in db-snap [:listeners :entity e]))]
            (f (entity db-snap e)))))))
  (doseq [listener (vals (get-in db-snap [:listeners :tx-log]))]
    (listener (remove nil? reports))))

(defn transact! [db txs]
  (let [txs (remove nil? (mapcat #(if (map? %) (map->txs db %) [%]) txs))
        db-before @db
        [next-db reports] (reduce
                            (fn [[snapshot reports] tx]
                              (if-let [f (cljs.core/get db-f (first tx))]
                                (apply f (cons [snapshot reports] (rest tx)))
                                (throw (js/Error (str "No db function: " (first tx) tx)))))
                            [db-before []]
                            txs)]
    (reset! db next-db)
    (notify-listeners @db reports)
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
                               (get-in db-snap [:index attribute value])

                               :else (do (when (debug? db-snap) (prn (str "Not an indexed attribute: " attribute)))
                                         (entity-ids db-snap #(= value (cljs.core/get % attribute))))))))) qs)))

(defn entities
  [db-snap & qs]
  (map (partial entity db-snap) (apply entity-ids (cons db-snap qs))))

(defn squuid []
  (str (uuid-utils/make-random-uuid)))