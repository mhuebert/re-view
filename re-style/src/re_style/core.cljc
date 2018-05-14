(ns re-style.core
  (:refer-clojure :exclude [rem])
  (:require [re-style.utils :as utils]
            [clojure.string :as str]
            [clojure.test :refer [is are]]))

(defn join-class-vec [x]
  (->> (mapv utils/stringify x)
       (str/join "-")))

(declare join-classes compile-coll)

(def pseudo? #{:hover
               :focus
               :active
               :visited
               :focus-within})

(defn prepend-pseudo [pseudo kw]
  (keyword (str (name pseudo) ":" (namespace kw))
           (name kw)))

(defn path->keyword
  "Returns canonical keyword for css-class path"
  [path]
  (cond

    ;; numbers cannot be the `name` of a keyword, so they are joined to the ns, eg. :a-1
    (and (= 2 (count path))
         (number? (second path)))
    (keyword (->> (mapv utils/stringify path)
                  (str/join \-)))

    ;; pseudo classes are prepended to the keyword, eg. :hover:a/b
    (pseudo? (first path))
    (prepend-pseudo (first path) (path->keyword (rest path)))

    :else

    (if (qualified-keyword? (first path))
      ;; when the 1st keyword in the path is multi-segment, use it as the namespace
      (keyword (namespace (first path)) (join-class-vec (cons (name (first path)) (rest path))))
      (keyword (utils/stringify (first path)) (join-class-vec (rest path))))))

(defn join-classes [coll]
  (->> coll
       (mapv (fn [x]
               (cond (keyword? x) (utils/keyword->dashed-string x)
                     (string? x) x
                     (coll? x) (join-class-vec x))))
       (str/join " ")))

(defn indexed
  ([coll] (indexed (range 1 999) coll))
  ([index coll]
   (let [index (if (fn? index)
                 (map index coll)
                 index)]
     (->> coll
          (interleave index)
          (partition 2)))))

(defn postpend-pseudo [s]
  (if-let [selector (second (re-find #"^(hover|focus\-within|focus|active|visited)-" "focus-within-"))]
    (str s ":" selector)
    s))

(def class-selector (comp (partial str ".")
                          postpend-pseudo
                          join-classes))


(defn map-keys [f m]
  (reduce-kv (fn [m k v]
               (assoc m (f k) v)) {} m))


(defn rem [n] (str n "rem"))
(defn px [n] (str n "px"))
(defn % [n] (str n "%"))
(defn deg [n] (str n "deg"))

(defn path-rule
  ([n] (path-rule n (inc n)))
  ([start end]
   (fn [{:keys [path-rule value]}]
     {(join-class-vec (->> path-rule
                           (drop start)
                           (take (- end start)))) value})))

(defn re? [kw]
  (and (keyword? kw)
       (= "re" (namespace kw))))

(defn- flatten-keys
  "Flatten a map in which some keys are collections of keys."
  [m]
  (reduce-kv (fn [m k v]
               (let [v (if (re? k)
                         v
                         (cond->> v
                                  (map? v) (flatten-keys)
                                  (keyword? v) (hash-map :re/rule)))]

                 (if (coll? k)
                   (let [{:as v
                          :keys [re/index]} v]
                     (cond
                       ;; map of {index, value}
                       (map? k)
                       (->> k
                            (reduce-kv (fn [m index value]
                                         (assoc m value (assoc v :re/i index)))
                                       (dissoc m k)))
                       ;; index to be paired with values at current depth in path
                       index
                       (->> (indexed index k)
                            (reduce (fn [m [i k]]
                                      (assoc m k (cond-> v
                                                         (map? v) (assoc :re/i i))))
                                    (dissoc m k)))
                       :else
                       (->> k
                            (reduce (fn [m k]
                                      (assoc m k v))
                                    (dissoc m k)))))
                   (assoc m k v))))
             {} m))

(comment
 (= (flatten-keys {:a {{0 "0px"} :the-rule}})
    {:a {0 {:re/rule :the-rule
            :re/value "0px"}}}))

(defn- find-rules
  ([m] (find-rules [] m))
  ([path-rule m]
   (when (keyword? m)
     (throw (ex-info "Should not be a keyword!" {:data m})))
   (if (contains? m :re/rule)
     {path-rule m}
     (->> m
          (reduce-kv (fn [m k v]
                       (cond-> m
                               (not (re? k))
                               (merge m (find-rules (conj path-rule k) v))))
                     {})))))

(defn join-if-coll [x]
  (cond-> x
          (coll? x) (join-class-vec)))

(defn expand-rule [rule {:as data
                         :keys [re/value]}]
  (cond (keyword? rule) {rule value}
        (map? rule) rule
        (fn? rule) (rule data)
        :else (throw (#?(:clj  Exception.
                         :cljs js/Error)
                      (str "Invalid rule" rule)))))

(defn compile-rules [& rules]

  (as-> rules rules

        (mapv flatten-keys rules)

        (apply merge rules)

        (find-rules rules)

        ;; expand pseudo-selectors
        (reduce-kv
         (fn [m path-rule {:keys [re/pseudos] :as v}]
           (if pseudos
             (reduce (fn [m pseudo]
                       (assoc m (into [pseudo] path-rule)
                                (assoc v :re/pseudo? true))) m pseudos)
             m)) rules rules)

        ;; compile rules
        (reduce-kv
         (fn [m path-rule {:as info
                      :keys [re/rule
                             re/i
                             re/pseudo?
                             re/format-value
                             re/format-index]
                      replacements :re/replace}]
           (try (let [i (cond-> i
                                (and i format-index) (format-index))
                      class-kw (path->keyword (cond-> path-rule
                                                      i (assoc (dec (count path-rule)) i)))
                      path-rule (cond->> path-rule
                                         replacements (replace replacements)
                                         replacements (keep identity)
                                         pseudo? (drop 1))
                      value (if format-value
                              (format-value (last path-rule))
                              (utils/stringify (last path-rule)))
                      rule (if rule
                             (expand-rule rule (assoc info
                                                 :re/path path-rule
                                                 :re/value value))
                             {(join-class-vec
                               (cond-> path-rule
                                       i (butlast))) value})]
                  (assoc m class-kw (map-keys join-if-coll rule)))
                (catch Exception e
                  (throw (ex-info "cannot compile!" (merge {:path path-rule
                                                            :info info
                                                            :error e}))))))
         (sorted-map)
         rules)))



(comment

 (= (keyword->dashed-string :a/b)
    "a-b")

 (= (utils/stringify :a/b)
    "a-b"
    "a-b")

 (= (utils/stringify 1)
    "1")

 (= (join-class-vec [:a/b :c])
    "a-b-c")

 (= (prepend-pseudo :hover :a/b)
    :hover:a/b)

 (= (path->keyword [:a 1])
    :a-1)
 (= (path->keyword [:a :b])
    :a/b)
 (= (path->keyword [:a-b :c :d])
    :a-b/c-d)
 (= (path->keyword [:a/b :c :d])
    :a/b-c-d)
 (= (path->keyword [:hover :a :b])
    :hover:a/b)


 (= (keyword->dashed-string :hover:text/color-black)
    "hover-text-color-black")


 (are [expr keywords]
   (is (= (->> (apply (spacing-fn (first expr)) (rest expr))
               (mapv path->keyword))
          keywords))

   [:pad 1 2 nil nil] [:pad/top-1
                       :pad/right-2]
   [:pad 1 2] [:pad/top-1
               :pad/bottom-1
               :pad/left-2
               :pad/right-2]
   [:pad 1] [:pad/all-1]

   )



 (are [expr keyword]
   (is (= (path->keyword expr) keyword))

   [:weight 2] :weight-2
   [:cursor :default] :cursor/default
   [:hover :cursor :default] :hover:cursor/default
   [:text :size 12] :text/size-12
   ["text" "size" "12"] :text/size-12)


 (= (join-classes ["a" :a/b [:a 1]])
    "a a-b a-1"))