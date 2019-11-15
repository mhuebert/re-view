(ns re-view.perf.bench
  #?(:cljs (:require-macros re-view.perf.bench)))

#?(:cljs
   (do
     (defn pad-right
       "Returns string `s` with minimum length `n`, adding spaces to the right to
        fill. At least one space is always added."
       [^string s n]
       (str s (.repeat " " (Math/max 1 (- n (count s))))))

     (defn rounded
       "Returns number rounded to n digits"
       [n digits]
       (let [multiplier (Math/pow 10 digits)]
         (-> n
             (* multiplier)
             (Math/round)
             (/ multiplier))))

     (defn normalize [m]
       (let [slowest (apply Math/max (vals m))]
         (reduce-kv (fn [m k v] (assoc m k (/ slowest v))) {} m)))

     ))

#?(:clj
   (def now '(js/Date.now)))

#?(:clj

   (defmacro measure [title runs & kvs]
     (let [[title runs] (if (string? title) [title runs] [nil title])
           [runs kvs] (if (number? runs) [runs kvs] [nil (cons runs kvs)])]
       (assert (even? (count kvs)))
       (let [runs (or runs 1000)
             variants (apply hash-map kvs)
             ks (mapv first (partition 2 kvs))
             rounds 6
             each-round (max 1 (Math/floor (/ runs rounds)))]
         `(let [fns# ~(reduce-kv (fn [out k v] (assoc out k `(fn [] ~v))) {} variants)
                results# (atom {})]
            (dotimes [_# ~rounds]
              (doseq [k# (shuffle (keys fns#))
                      :let [f# (fns# k#)
                            start# ~now]]
                (dotimes [_# ~each-round] (f#))
                (swap! results# update k# (fnil + 0) (- ~now start#))))
            ~(when title `(println (str "\nMeasured: " ~title)))
            (doseq [k# ~ks
                    :let [res# (@results# k#)]]
              (println (str ";; "
                            (~'re-view.perf.bench/pad-right (str k#) 15) " "
                            (~'re-view.perf.bench/rounded res# 1)
                            "ms")))
            (when-not (apply = (map (comp cljs.core/js->clj #(%)) (vals fns#)))
                (print (map #(%) (vals fns#)))))))))