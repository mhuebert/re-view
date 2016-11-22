(ns re-view.subscriptions
  (:require [re-view.subscriptions.router-sub :as router-sub]
            [re-view.subscriptions.db-sub])
  (:require-macros [re-view.subscriptions]))

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

(def router router-sub/router)