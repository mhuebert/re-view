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

   (defmacro measure [title repetitions & kvs]
     (let [[title repetitions] (if (string? title) [title repetitions] [nil title])
           [repetitions kvs] (if (number? repetitions) [repetitions kvs] [nil (cons repetitions kvs)])]
       (assert (even? (count kvs)))
       (let [repetitions (or repetitions 1000)
             per-round (max 1 (Math/floor (/ repetitions 100)))
             rounds (/ repetitions per-round)
             variants (apply hash-map kvs)
             ks (mapv first (partition 2 kvs))]
         `(let [ks# ~ks
                fns# ~(reduce-kv (fn [out k v] (assoc out k `(fn [] ~v))) {} variants)
                results# (atom {})]
            (dotimes [_# ~rounds]
              (doseq [k# (shuffle ks#)
                      :let [f# (fns# k#)
                            start# ~now]]
                (dotimes [_# ~per-round] (f#))
                (swap! results# update k# (fnil + 0) (- ~now start#))))
            ~(when title `(println (str "\nMeasured: " ~title)))
            (println (str ~repetitions " repetitions, " ~rounds " rounds, " ~per-round " per round."))
            (doseq [k# ks#
                    :let [res# (@results# k#)]]
              (println (str ";; "
                            (~'re-view.perf.bench/pad-right (str k#) 15) " "
                            (~'re-view.perf.bench/rounded res# 1)
                            "ms")))
            (when-not (apply = (map (comp cljs.core/js->clj #(%)) (vals fns#)))
                (print (map #(%) ((apply juxt ks#) fns#)))))))))