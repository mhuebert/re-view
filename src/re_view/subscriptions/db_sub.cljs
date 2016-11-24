(ns re-view.subscriptions.db-sub
  (:require [re-view.shared :refer [*lookup-log*]]
            [re-db.d :as d]))

(defn db-fn [f should-update?]
  (fn [this st-key]
    (let [capture-patterns #(let [patterns (binding [*lookup-log* (atom #{})]
                                             (assoc (d/capture-patterns (f this))
                                               :prop-fn? (contains? @*lookup-log* :props)))]
                             (update patterns :patterns (partial remove (fn [p] (= (first p) this)))))
          pattern-result (atom (capture-patterns))
          unsub (atom)]
      {:default       #(:value @pattern-result)
       :subscribe     #(reset! unsub (apply d/listen! (concat (:patterns @pattern-result)
                                                              (list (fn []
                                                                      (let [{:keys [value] :as next-pattern-result} (capture-patterns)]
                                                                        (reset! pattern-result next-pattern-result)
                                                                        (swap! this assoc st-key value)))))))
       :unsubscribe   #(do (@unsub))
       :should-update should-update?})))