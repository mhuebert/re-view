(ns re-view.subscriptions
  (:require [re-db.d :as d]
            [re-db.core :include-macros true])
  (:require-macros [re-view.subscriptions]))

(defn get-id [id-selector props]
  (cond (fn? id-selector) (id-selector props)
        (and (keyword? id-selector)
             (contains? props id-selector)) (get props id-selector)
        :else id-selector))

(defn db [[id-selector attr :as pattern]]
  (fn [_ props cb]
    (let [id (atom (get-id id-selector props))
          prev-id (atom @id)]
      {:default       #(cond (and id attr) (d/get @id attr)
                             id (d/entity @id)
                             :else nil)
       :subscribe     #(d/listen! [@id attr] cb)
       :unsubscribe   #(do
                        (d/unlisten! [@prev-id attr] cb)
                        (reset! prev-id @id))
       :should-update #(let [next-id (get-id id-selector %2)]
                        (when (not= next-id @id)
                          (reset! id next-id)
                          true))})))


(defn initialize-subscriptions
  "If component has specified subscriptions, initialize them"
  [{initial-props :props :as this}]
  (reduce-kv (fn [m k sub-fn]
               (let [{:keys [default] :as sub} (sub-fn this initial-props #(swap! this assoc k %))]
                 (cond-> m
                         sub (assoc-in [:subscriptions k] sub)
                         default (assoc k (default)))))
             {}
             (aget this "subscriptions")))

(defn end-subscriptions [{:keys [prev-props state] :as this}]
  (doseq [{:keys [unsubscribe]} (vals (:subscriptions state))]
    (when unsubscribe (unsubscribe prev-props))))

(defn begin-subscriptions [{:keys [props state] :as this}]
  (end-subscriptions this)
  (doseq [{:keys [subscribe]} (vals (:subscriptions state))]
    (subscribe props)))

(defn update-subscriptions [{:keys [prev-props props] :as this}]
  (when (seq (keep identity (filter (fn [{:keys [should-update]}] (and should-update (should-update prev-props props))) (vals (:subscriptions (:state this))))))
    (swap! this merge (initialize-subscriptions this))
    (begin-subscriptions this)))

(def subscription-mixin
  {:initial-state      initialize-subscriptions
   :will-mount         begin-subscriptions
   :will-unmount       end-subscriptions
   :will-receive-props update-subscriptions})