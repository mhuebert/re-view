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