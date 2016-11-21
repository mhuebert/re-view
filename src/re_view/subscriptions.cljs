(ns re-view.subscriptions
  (:require [re-db.d :as d]
            [re-db.core :include-macros true])
  (:require-macros [re-view.subscriptions]))

(defn get-id [id-selector props]
  (cond (and (keyword? id-selector)
             (contains? props id-selector)) (get props id-selector)
        (fn? id-selector) (id-selector props)
        :else id-selector))

(defn db [[id-selector attr]]
  (fn [this st-key]
    (let [id (atom (get-id id-selector (:props this)))
          cb #(swap! this assoc st-key %)]
      {:default       #(cond (and id attr) (d/get @id attr)
                             id (d/entity @id)
                             :else nil)
       :subscribe     #(let [next-id (get-id id-selector (:props this))]
                        (reset! id next-id)
                        (swap! this update-in [:subscriptions st-key :unsubscribe-cbs] (fnil conj []) (fn [] (d/unlisten! [next-id attr] cb)))
                        (d/listen! [next-id attr] cb))
       :unsubscribe   #(do
                        (doseq [unsub (get-in this [:state :subscriptions st-key :unsubscribe-cbs])]
                          (unsub))
                        (swap! this update-in [:state :subscriptions st-key] dissoc :unsubscribe-cbs))
       :should-update #(not= @id (get-id id-selector (:props this)))})))


(defn initialize-subscriptions
  "If component has specified subscriptions, initialize them"
  [this]
  (reduce-kv (fn [m k sub-fn]
               (let [{:keys [default] :as sub} (sub-fn this k)]
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