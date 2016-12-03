(ns re-db.reactions
  (:require [re-db.core :refer [listen! unlisten!]]))

#_(defn compute-value!
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

#_(defmacro compute! [db [id attr] & body]
    `(let [f# (fn [] (do ~@body))]
       (~'re-db.core/compute-value! ~db [~id ~attr] f#)))