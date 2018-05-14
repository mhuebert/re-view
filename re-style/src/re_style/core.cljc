(ns re-style.core
  (:refer-clojure :exclude [rem])
  (:require [re-style.utils :as utils]
            [clojure.string :as str]
            [clojure.test :refer [is are]]
            [re-style.read :refer [expand-class-vec]]
            [garden.core :as garden]))

(defn- stringify-negatives [x]
  (if (and (number? x) (neg? x))
    (str \n (- x))
    x))

(defn join-class-vec [x]
  (->> (mapv (comp utils/stringify stringify-negatives) x)
       (str/join "-")))

(declare compile-coll)

(def pseudo? #{:hover
               :focus
               :active
               :visited
               :focus-within})

(defn canonical-kw
  "Returns canonical keyword for css-class path"
  [path]
  (cond

    ;; numbers cannot be the `name` of a keyword, so they are joined to the ns, eg. :a-1
    (and (= 2 (count path))
         (number? (second path)))
    (keyword (->> (mapv utils/stringify path)
                  (str/join \-)))

    (pseudo? (first path))
    ;; pseudo classes are prepended to the keyword, eg. :hover:bg/color-black
    (let [non-pseudo-keyword (canonical-kw (rest path))
          pseudo-name (name (first path))]
      (keyword (str pseudo-name ":" (namespace non-pseudo-keyword))
               (name non-pseudo-keyword)))

    :else

    (if (qualified-keyword? (first path))
      ;; when the 1st keyword in the path is multi-segment, use it as the namespace
      (keyword (namespace (first path)) (join-class-vec (cons (name (first path)) (rest path))))
      (keyword (utils/stringify (first path)) (join-class-vec (rest path))))))

(defn rem [n] (str n "rem"))
(defn px [n] (str n "px"))
(defn % [n] (str n "%"))
(defn deg [n] (str n "deg"))

(defn path-rule
  ([n] (path-rule n (inc n)))
  ([start end]
   (fn [{:keys [re/path re/value]}]
     {(->> path
           (drop start)
           (take (- end start))) value})))

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
                       (->> k
                            (interleave index)
                            (partition 2)
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
  ([path m]
   (when (keyword? m)
     (throw (ex-info "Should not be a keyword!" {:data m})))
   (if (contains? m :re/rule)
     {path m}
     (->> m
          (reduce-kv (fn [m k v]
                       (cond-> m
                               (not (re? k))
                               (merge m (find-rules (conj path k) v))))
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
         (fn [m path {:keys [re/pseudos] :as v}]
           (if pseudos
             (reduce (fn [m pseudo]
                       (assoc m (into [pseudo] path)
                                (assoc v :re/pseudo? true))) m pseudos)
             m)) rules rules)

        ;; compile rules
        (reduce-kv
         (fn [m path {:as info
                      :keys [re/rule
                             re/i
                             re/pseudo?
                             re/format-value
                             re/format-index]
                      replacements :re/replace}]
           (try (let [i (cond-> i
                                (and i format-index) (format-index))
                      class-path (cond-> path
                                         i (assoc (dec (count path)) i))
                      path (cond->> path
                                    replacements (replace replacements)
                                    replacements (keep identity)
                                    pseudo? (drop 1))
                      value (if format-value
                              (format-value (last path))
                              (utils/stringify (last path)))
                      rule (if rule
                             (expand-rule rule (assoc info
                                                 :re/path path
                                                 :re/value value))
                             {(join-class-vec
                               (cond-> path
                                       i (butlast))) value})]
                  (assoc m class-path (utils/map-keys join-if-coll rule)))
                (catch Exception e
                  (throw (ex-info "cannot compile!" (merge {:path path
                                                            :info info
                                                            :error e}))))))
         {}
         rules)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Emit CSS

;; TODO
;; re-style.registry should keep track of valid keywords

(defn postpend-pseudo [s]
  (if-let [selector (second (re-find #"^(hover|focus\-within|focus|active|visited)-" s))]
    (str s ":" selector)
    s))

(defn class-selector [coll]
  (->> coll
       (mapv (comp utils/stringify stringify-negatives))
       (str/join \-)
       (postpend-pseudo)
       (str \.)))

(defn emit-css [compiled-rules]
  (->> compiled-rules
       (utils/map-keys class-selector)
       (garden/css)))

(garden/css {".pad-top-1" {:padding "3px"}})

(defn emit-canonical-kws [compiled-rules]
  (->> compiled-rules
       (utils/map-keys canonical-kw)))

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

 (= (canonical-kw [:a 1])
    :a-1)
 (= (canonical-kw [:a :b])
    :a/b)
 (= (canonical-kw [:a-b :c :d])
    :a-b/c-d)
 (= (canonical-kw [:a/b :c :d])
    :a/b-c-d)
 (= (canonical-kw [:hover :a :b])
    :hover:a/b)


 (= (keyword->dashed-string :hover:text/color-black)
    "hover-text-color-black")


 (are [expr keywords]
   (is (= (->> (apply (spacing-fn (first expr)) (rest expr))
               (mapv canonical-kw))
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
   (is (= (canonical-kw expr) keyword))

   [:weight 2] :weight-2
   [:cursor :default] :cursor/default
   [:hover :cursor :default] :hover:cursor/default
   [:text :size 12] :text/size-12
   ["text" "size" "12"] :text/size-12)


 (= (join-classes ["a" :a/b [:a 1]])
    "a a-b a-1"))