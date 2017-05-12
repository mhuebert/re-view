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
     (set! *pattern-log* (conj *pattern-log* pattern))))
  ([kind pattern multiple?]
   (when-not (nil? *pattern-log*)
     (set! *pattern-log* ((if multiple? into conj) *pattern-log* pattern)))))

(def conj-set (fnil conj #{}))

(defn pattern-path
  "Given a pattern type and datom, returns path"
  [kind pattern]
  (case kind :e__ [(pattern 0) nil nil]
             :ea_ [(pattern 0) (pattern 1) nil]
             :_av [nil (pattern 1) (pattern 2)]
             :_a_ [nil (pattern 2) nil]
             (into [kind] pattern)))

(defn add-value
  [value-map pattern-key pattern value]
  (update value-map (pattern-path pattern-key pattern) conj-set value))

(defn remove-value
  [value-map pattern-key pattern value]
  (update value-map (pattern-path pattern-key pattern) disj value))

(declare listen unlisten)

(defn resolve-id
  [db-snap attr val]
  (log-read :_av [nil attr val])
  (first (get-in db-snap [:ave attr val])))

(defn listen-lookup-ref
  "Stateful listener for datoms where the id is specified as a lookup ref."
  [listeners db pattern f]
  (let [[[lookup-attr lookup-val :as lookup-ref] attr] pattern
        lookup-target (atom (resolve-id @db lookup-attr lookup-val))
        lookup-cb (fn [{:keys [db-after] :as tx-report}]
                    (let [next-lookup-target (resolve-id db-after lookup-attr lookup-val)]
                      (when @lookup-target
                        (unlisten db {:e__ [[@lookup-target]]} f))
                      (when-not (nil? next-lookup-target)
                        (listen db {:e__ [[next-lookup-target]]} f))
                      (reset! lookup-target next-lookup-target)
                      ;; trigger callback when lookup ref points to a different entity
                      (f tx-report)))]
    (-> (cond-> listeners
                (not (nil? @lookup-target)) (add-value :e__ [@lookup-target] f))
        (add-value :_av [nil lookup-attr lookup-val] lookup-cb)
        (assoc-in [:lookup-refs [pattern f]] {:lookup-cb     lookup-cb
                                              :lookup-target lookup-target}))))

(defn unlisten-lookup-ref
  "Removes lookup ref listener."
  [listeners db [[lookup-attr lookup-val] :as pattern] f]
  (let [{:keys [lookup-cb lookup-target]} (get-in listeners [:lookup-refs [pattern f]])]
    (-> (cond-> listeners
                @lookup-target (remove-value :e__ [@lookup-target] f))
        (remove-value :_av [nil lookup-attr lookup-val] lookup-cb)
        (dissoc [:lookup-refs [pattern f]]))))

(defn listen
  "Add listener for patterns"
  [db patterns value]
  (swap! db assoc :listeners
         (reduce-kv (fn [listeners kind patterns]
                      (reduce (fn [listeners pattern]
                                (if (vector? (first pattern))
                                  (listen-lookup-ref listeners db pattern value)
                                  (add-value listeners kind pattern value))) listeners patterns)) (get @db :listeners) patterns)))

(defn unlisten
  [db patterns value]
  (swap! db assoc :listeners
         (reduce-kv (fn [listeners kind patterns]
                      (reduce (fn [listeners pattern]
                                (if (vector? (first pattern))
                                  (unlisten-lookup-ref listeners db pattern value)
                                  (remove-value listeners kind pattern value))) listeners patterns)) (get @db :listeners) patterns)))

(defn get-values [value-map pattern-key pattern]
  (get-in value-map (pattern-path pattern-key pattern)))

(defn matches-datom? [pattern datom many?]
  (or (nil? pattern)
      (and (or (nil? (pattern 0)) (= (pattern 0) (datom 0)))
           (or (nil? (pattern 1)) (= (pattern 1) (datom 1)))
           (or (nil? (pattern 2)) (if (many? (datom 1))
                                    (or (contains? (datom 2) (pattern 2))
                                        (contains? (datom 3) (pattern 2)))
                                    (or (= (pattern 2) (datom 2))
                                        (= (pattern 2) (datom 3))))))))

(defn match-datoms [patterns datoms many?]
  (let [patterns (into-array patterns)
        d-len (count datoms)
        p-len (.-length patterns)]
    (loop [matched []
           p-index 0
           d-index 0]
      (if (or (= d-index d-len)                             ;; end of datoms
              (= (count matched) p-len))                    ;; matched all patterns
        matched
        (let [next? (= p-index (dec p-len))
              pattern (nth patterns p-index)
              matches? (or (nil? pattern)
                           (let [datom (nth datoms d-index)]
                             (and (or (nil? (pattern 0)) (= (pattern 0) (datom 0)))
                                  (or (nil? (pattern 1)) (= (pattern 1) (datom 1)))
                                  (or (nil? (pattern 2)) (if (many? (datom 1))
                                                           (or (contains? (datom 2) (pattern 2))
                                                               (contains? (datom 3) (pattern 2)))
                                                           (or (= (pattern 2) (datom 2))
                                                               (= (pattern 2) (datom 3))))))))]
          (when (true? matches?) (aset patterns p-index nil))
          (recur (if matches? (conj matched pattern) matched)
                 (if next? 0 (inc p-index))
                 (if next? (inc d-index) d-index)))))))


(assert (= (match-datoms [[:e nil nil]
                          [nil :a nil]
                          [nil nil :v]
                          [nil nil :pv]
                          [:e :a nil]
                          [nil :a :v]
                          [:e :e :e]
                          [:a :a nil]]
                         [[:e :a :v :pv]]
                         #{})
           [[:e nil nil]
            [nil :a nil]
            [nil nil :v]
            [nil nil :pv]
            [:e :a nil]
            [nil :a :v]]))

(defn datom-values
  "Given a mapping of patterns to values, return the set of values for patterns that match any datom in the list."
  [value-map datoms many?]
  (->> (match-datoms (-> value-map
                         (dissoc :lookup-refs)
                         (keys)) datoms many?)
       (reduce (fn [values pattern]
                 (into values (get value-map pattern))) (get value-map :tx-log #{}))))

