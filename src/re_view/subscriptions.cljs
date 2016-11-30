(ns re-view.subscriptions
  (:require [re-view.subscriptions.db-sub])
  (:require-macros [re-view.subscriptions]))

(defn init-sub
  "Merge initial subscription data into state map"
  [this m st-key sub-fn]
  (let [{:keys [default] :as sub} (sub-fn this st-key)]
    (-> m
        (assoc-in [:subscriptions/state st-key] sub)
        (assoc st-key (default)))))

(defn init-subs
  [this]
  (reduce-kv (partial init-sub this) {} (aget this "subscriptions")))

(defn end-subscriptions [this]
  (doseq [{:keys [unsubscribe]} (vals (get-in this [:state :subscriptions/state]))]
    (when-not (nil? unsubscribe) (unsubscribe this))))

(defn begin-subscriptions [this]
  (doseq [{:keys [subscribe]} (vals (get-in this [:state :subscriptions/state]))]
    (subscribe)))

(defn update-subscriptions [this]
  (doseq [[st-key {:keys [should-update unsubscribe]}] (seq (get-in this [:state :subscriptions/state]))]
    (when (and (not (nil? should-update)) (should-update this))
      (when-not ^:boolean (nil? unsubscribe) (unsubscribe))
      (swap! this #(init-sub this % st-key (get (aget % "subscriptions") st-key)))
      (let [subscribe (get-in this [:state :subscriptions/state st-key :subscribe])]
        (subscribe)))))

(def subscription-mixin
  {:initial-state      init-subs
   :will-mount         begin-subscriptions
   :will-unmount       end-subscriptions
   :will-receive-props update-subscriptions})