(ns re-db.core
  (:refer-clojure :exclude [get get-in set! update peek])
  (:require [cljs-uuid-utils.core :as uuid-utils]
            [clojure.set :as set])
  (:require-macros [re-db.core :refer [capture-patterns]]))

(enable-console-print!)

(def get-in* cljs.core/get-in)
(def get* cljs.core/get)
(def update* cljs.core/update)

(def ^:dynamic *db-log* nil)                                ;; maintains tx-report while bound
(def ^:dynamic *mute* false)                                ;; ignore listeners
(def ^:dynamic *access-log* nil)                            ;; capture access patterns

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
          (do (some-> *access-log*
                      (swap! update* :attr-val (fnil conj #{}) [nil a v]))
              (get-in* db-snap [:index a v]))))
      id)))

(defn has? [db-snap e]
  (contains? (:data db-snap) (resolve-id db-snap e)))

(defn- listen-path!
  [db path f]
  (doto db
    (swap! update-in path #((fnil conj #{}) % f))))

(defn- unlisten-path!
  [db path f]
  (doto db
    (swap! update-in path #(disj % f))))

(declare get entity)

(defn pattern->idx
  "Identify access pattern type"
  [db-snap pattern]
  (let [[id attr val] (map #(if (= % '_) nil %) pattern)]
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
    :attr-val (:id (entity db-snap [attr val]))
    :id (entity db-snap id)
    nil))

(declare listen! unlisten!)

(defn lookup-ref?
  "Returns true if pattern is lookup ref"
  [pattern]
  (sequential? (first pattern)))

(defn listen-lookup-ref!

  [db pattern f]
  (let [[[lookup-attr lookup-val] attr] pattern]
    (let [prev-id (atom)
          intermediate-cb (fn [resolved-id]
                            (when @prev-id (unlisten! db [@prev-id attr] f))
                            (when resolved-id (listen! db [resolved-id attr] f))
                            (reset! prev-id resolved-id)
                            (f (pattern->val @db pattern)))]
      (listen! db [nil lookup-attr lookup-val] intermediate-cb)
      (swap! db assoc-in [:listeners :lookup-ref pattern f] {:clear #(do (intermediate-cb nil)
                                                                         (unlisten! db [nil lookup-attr lookup-val] intermediate-cb)
                                                                         (swap! db update-in [:listeners :lookup-ref pattern] dissoc f))}))))

(defn listen!
  [db & patterns]
  (let [f (last patterns)]
    (doseq [pattern (drop-last patterns)]
      (if (lookup-ref? pattern)
        (listen-lookup-ref! db pattern f)
        (listen-path! db (pattern->listener-path @db pattern) f)))))

(defn unlisten!
  [db & patterns]
  (let [f (last patterns)]
    (doseq [pattern (drop-last patterns)]
      (if (lookup-ref? pattern)
        ((get-in* @db [:listeners :lookup-ref pattern f :clear])))
      (unlisten-path! db (pattern->listener-path @db pattern) f))))

(defn entity [db-snap id]
  (when-let [id (resolve-id db-snap id)]
    (some-> *access-log*
            (swap! update* :id (fnil conj #{}) [id]))
    (some-> (get-in* db-snap [:data id])
            (assoc :id id))))

(defn get [db-snap id & args]
  (when-let [id (resolve-id db-snap id)]
    (some-> *access-log*
            (swap! update-in [:entity-attr id] (fnil conj #{}) (first args)))

    (apply get* (cons (some-> (get-in* db-snap [:data id])
                              (assoc :id id)) args))))

(defn get-in [db-snap id [attr & ks]]
  (get-in* (get db-snap id attr) ks))

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
      (update* :entity-attrs conj [id attr])
      (cond->
        (unique? (:db-before reports) attr) (update* :attr-vals into (cond-> []
                                                                             value (conj [id attr value true])
                                                                             prev-value (conj [id attr prev-value false]))))))

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
   {:pre [(not (duplicate-on-unique? db-snap id attr val))
          (not= attr :id)]}

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
    (do
      (assert (contains? m :id))
      (when (> (count m) 1)
        (map (fn [[a v]] (if (nil? v)
                           [:db/retract-attr (:id m) a]
                           [:db/add (:id m) a v])) (dissoc m :id))))
    (list m)))

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

(defn notify-listeners [{:keys [db-before db-after] {:keys [datoms ids attrs entity-attrs attr-vals]} :reports}]
  (let [metadata {:db-before db-before
                  :db-after  db-after
                  :datoms    datoms}]

    (when (seq datoms)
      (when (seq (get-in* db-after [:listeners :entity]))
        (doseq [id ids]
          (doseq [f (get-in* db-after [:listeners :entity id])]
            (f (entity db-after id) metadata))))

      (when (seq (get-in* db-after [:listeners :entity-attr]))
        (doseq [[id attr] entity-attrs]
          (let [listeners (get-in* db-after [:listeners :entity-attr id attr])]
            (doseq [f listeners]
              (f (get db-after id attr))))))

      (when (seq (get-in* db-after [:listeners :attr-val]))
        (doseq [[id attr val added?] attr-vals]
          (let [listeners (get-in* db-after [:listeners :attr-val attr val])]
            (doseq [f listeners]
              (f (if added? id nil))))))

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
                                            {:datoms       []
                                             :ids          #{}
                                             :attrs        {}
                                             :entity-attrs #{}
                                             :attr-vals    #{}
                                             :db-before    db-before})]
                             txs)]
    {:db-before db-before
     :db-after  db-after
     :reports   reports}))

(defn transact!
  [db txs]
  (when-let [{:keys [db-after] :as tx} (cond (nil? txs) nil
                                             (and (map? txs) (contains? txs :reports)) txs
                                             (sequential? txs) (transaction @db txs)
                                             :else (throw (js/Error "Transact! was not passed a valid transaction")))]
    (reset! db db-after)
    (when *db-log* (reset! *db-log* (cond-> tx
                                            (:db-before @*db-log*) (assoc :db-before @*db-log*))))
    (when-not *mute* (notify-listeners tx))
    db))

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

(defn access-log-patterns
  "Returns vector of access log patterns, pruning overlapping patterns"
  [{ids :id attr-vals :attr-val entity-attrs :entity-attr}]
  (reduce-kv
    (fn [patterns id attrs]
      (if (contains? ids [id])
        patterns
        (apply conj patterns (for [attr attrs]
                               [id attr]))))
    (apply into #{} ids attr-vals)
    entity-attrs))

(defn compute-value!
  ([db pattern]
   (compute-value! db pattern nil))
  ([db [id attr] fresh-f]
   (let [{prev-f :f prev-patterns :patterns prev-listener :listener} (cljs.core/get-in @db [:listeners :computed [id attr]])]
     (cond (false? fresh-f)
           (do
             ;; remove computed value
             (apply unlisten! (concat (list db) prev-patterns (list prev-listener)))
             (swap! db update-in [:listeners :computed] dissoc [id attr])
             true)

           (fn? fresh-f)
           (do
             ;; (re)set computed value function
             (swap! db assoc-in [:listeners :computed [id attr]] {:listener (or prev-listener #(compute-value! db [id attr]))
                                                                  :f        fresh-f})
             (compute-value! db [id attr]))

           (nil? fresh-f)
           ;; run computation when value changes
           (let [{next-patterns :patterns value :value} (re-db.core/capture-patterns (prev-f))
                 next-patterns (disj next-patterns [id attr])]
             (when (not= prev-patterns next-patterns)
               (swap! db assoc-in [:listeners :computed [id attr] :patterns next-patterns])
               (when (seq prev-patterns)
                 (apply unlisten! (concat (list db) prev-patterns (list prev-listener))))
               (when (seq next-patterns)
                 (apply listen! (concat (list db) next-patterns (list prev-listener)))))
             (re-db.core/transact! db [[:db/add id attr value]])
             value)))))

; metadata on transactions (what function/cell was the source of this fact?)
; timestamp on fact (compare facts by date)
; [id attr val added? timestamp tx]