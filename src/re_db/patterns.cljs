(ns re-db.patterns
  (:require [clojure.set :as set]))

(def ^:dynamic *pattern-log*
  "Dynamic var used in conjunction with re-db.patterns/capture-patterns macro to
  identify data patterns accessed during the execution of a block of code."
  nil)

(def ^:private blank-pattern-log
  {:e__ #{}
   :_a_ #{}
   :_av #{}
   :ea_ #{}})

(defn log-read
  "Record a read pattern to *pattern-log*."
  ([kind pattern]
   (when-not (nil? *pattern-log*)
     (set! *pattern-log* (update *pattern-log* kind conj pattern))))
  ([kind pattern multiple?]
   (when-not (nil? *pattern-log*)
     (set! *pattern-log* (update *pattern-log* kind (if multiple? into conj) pattern)))))

(def conj-set (fnil conj #{}))

(defn pattern-path
  "Given a pattern type and datom, returns path"
  [kind pattern]
  (case kind :e__ [:e__ (pattern 0)]
             :ea_ [:ea_ (pattern 0) (pattern 1)]
             :_av [:_av (pattern 1) (pattern 2)]
             :_a_ [:_a_ (pattern 2)]
             (into [kind] pattern)))

(defn add-value
  [value-map pattern-key pattern value]
  (update-in value-map (pattern-path pattern-key pattern) conj-set value))

(defn remove-value
  [value-map pattern-key pattern value]
  (update-in value-map (pattern-path pattern-key pattern) disj value))

(declare listen! unlisten!)

(defn resolve-id
  [db-snap attr val]
  (log-read :_av [nil attr val])
  (first (get-in db-snap [:ave attr val])))

(defn listen-lookup-ref!
  "Stateful listener for datoms where the id is specified as a lookup ref."
  [listeners db pattern f]
  (let [[[lookup-attr lookup-val :as lookup-ref] attr] pattern
        lookup-target (atom (resolve-id @db lookup-attr lookup-val))
        lookup-cb (fn [{:keys [db-after] :as tx-report}]
                    (let [next-lookup-target (resolve-id db-after lookup-attr lookup-val)]
                      (when @lookup-target
                        (unlisten! db {:e__ [[@lookup-target]]} f))
                      (when-not (nil? next-lookup-target)
                        (listen! db {:e__ [[next-lookup-target]]} f))
                      (reset! lookup-target next-lookup-target)
                      ;; trigger callback when lookup ref points to a different entity
                      (f tx-report)))]
    (-> (cond-> listeners
                (not (nil? @lookup-target)) (add-value :e__ [@lookup-target] f))
        (add-value :_av [nil lookup-attr lookup-val] lookup-cb)
        (assoc-in [:lookup-ref pattern f] {:lookup-cb     lookup-cb
                                           :lookup-target lookup-target}))))

(defn unlisten-lookup-ref
  "Removes lookup ref listener."
  [listeners db [[lookup-attr lookup-val] :as pattern] f]
  (let [{:keys [lookup-cb lookup-target]} (get-in listeners [:lookup-ref pattern f])]
    (-> (cond-> listeners
                @lookup-target (remove-value :e__ [@lookup-target] f))
        (remove-value :_av [nil lookup-attr lookup-val] lookup-cb)
        (update-in [:lookup-ref pattern] dissoc f))))

(defn listen!

  [db patterns value]
  (swap! db assoc :listeners
         (reduce-kv (fn [listeners kind patterns]
                      (reduce (fn [listeners pattern]
                                (if (vector? (first pattern))
                                  (listen-lookup-ref! listeners db pattern value)
                                  (add-value listeners kind pattern value))) listeners patterns)) (get @db :listeners) patterns)))

(defn unlisten!
  [db patterns value]
  (swap! db assoc :listeners
         (reduce-kv (fn [listeners kind patterns]
                      (reduce (fn [listeners pattern]
                                (if (vector? (first pattern))
                                  (unlisten-lookup-ref listeners db pattern value)
                                  (remove-value listeners kind pattern value))) listeners patterns)) (get @db :listeners) patterns)))

(defn get-values [value-map pattern-key pattern]
  (get-in value-map (pattern-path pattern-key pattern)))

(defn datom-patterns
  "Patterns invalidated by a list of datoms. Provide a list of pattern-keys to limit results.

  many? should return true for attribute keys which are :cardinality/many"
  ([datoms many?] (datom-patterns datoms many? (keys blank-pattern-log)))
  ([datoms many? pattern-keys]
   (->> datoms
        ;; for every datom...
        (reduce (fn [patterns datom]
                  ;; for every active pattern key...
                  (reduce (fn [pattern-paths pattern-key]
                            (if (and (keyword-identical? pattern-key :_av)
                                     (many? (nth datom 1)))
                              ;; for cardinality/many values, one pattern per item in value and prev-value
                              (into pattern-paths (let [[_ attr val prev-val] datom]
                                                    (reduce
                                                      (fn [patterns v] (conj patterns (pattern-path :_av [nil attr v]))) [] (into val prev-val))))
                              (conj pattern-paths (pattern-path pattern-key datom)))) patterns pattern-keys))
                #{}))))

(defn active-patterns
  "List of pattern keys for which values exist in map"
  [value-map]
  (reduce (fn [ks k]
            (cond-> ks
                    (not (empty? (get value-map k))) (conj k))) [] (keys blank-pattern-log)))

(defn datom-values
  "Given a mapping of patterns to values, return the set of values for patterns that match any datom in the list."
  [value-map datoms many?]
  (->>
    ;; only collect patterns for which values exist
    (active-patterns value-map)
    ;; parse datoms for patterns
    (datom-patterns datoms many?)
    ;; collect values from map
    (reduce (fn [values pattern]
              (into values (get-in value-map pattern))) #{})
    ;; add :tx-log patterns
    (into (get value-map :tx-log))))

