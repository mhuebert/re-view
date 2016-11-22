(ns re-view.subscriptions.db-sub
  (:require [re-view.util :refer [*lookup-log*]]
            [re-db.d :as d]))

(defn db-fn [f]
  (fn [this st-key]
    (let [capture-patterns #(binding [*lookup-log* (atom #{})]
                             (assoc (d/capture-patterns (f this))
                               :prop-fn? (contains? @*lookup-log* :props)))
          pattern-result (atom (capture-patterns))]
      {:default       #(:value @pattern-result)
       :subscribe     #(swap! this update-in [:subscriptions st-key :unsubscribe-cbs] conj
                              (apply d/listen! (concat (:patterns @pattern-result) (list (partial swap! this assoc st-key)))))
       :unsubscribe   #(do (doseq [f# (get-in this [:subscriptions st-key :unsubscribe-cbs])]
                             (f#))
                           (swap! this update-in [:subscriptions st-key] dissoc :unsubscribe-cbs))
       :should-update #(when ^:boolean (:prop-fn? @pattern-result)
                        (let [next-pattern-result (capture-patterns)]
                          (when (not= (:patterns next-pattern-result)
                                      (:patterns @pattern-result))
                            (reset! pattern-result next-pattern-result)
                            true)))})))