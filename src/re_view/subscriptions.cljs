(ns re-view.subscriptions
  (:require [re-db.d :as d]))

(defn get-id [id-selector props]
  (cond (fn? id-selector) (id-selector props)
        (keyword? id-selector) (get props id-selector)
        :else id-selector))

(defn entity [id-selector]
  (fn [_ props cb]
    (let [id (atom (get-id id-selector props))
          prev-id (atom @id)]
      {:default       #(d/entity @id)
       :subscribe     #(when @id
                        (d/listen! @id cb))
       :unsubscribe   #(when @prev-id
                        (d/unlisten! @prev-id cb)
                        (reset! prev-id @id))
       :should-update #(let [next-id (get-id id-selector %2)]
                        (when (not= next-id @id)
                          (reset! id next-id)
                          true))})))

(defn attr [attr]
  (fn [_ _ cb]
    {:subscribe   #(d/listen-attr! attr cb)
     :unsubscribe #(d/unlisten-attr! attr cb)}))

(defn entity-attr [id-selector attr]
  (fn [_ props cb]
    (let [id (atom (get-id id-selector props))
          prev-id (atom @id)]
      {:default       #(some-> @id (d/get attr))
       :subscribe     #(some-> @id (d/listen! attr cb))
       :unsubscribe   #(when @prev-id
                        (d/unlisten! @prev-id attr cb)
                        (reset! prev-id @id))
       :should-update #(let [next-id (get-id id-selector %2)]
                        (when (not= next-id @id)
                          (reset! id next-id)
                          true))})))

(defn root []
  (fn [_ _ cb]
    {:subscribe #(d/listen! cb)
     :unsubscribe #(d/unlisten! cb)}))

(defn subscribe [id-selector attribute]
  (cond (= ['_ '_] [id-selector attribute]) (root)
        (= '_ id-selector) (attr attribute)
        (= '_ attribute) (entity id-selector)
        :else
        (entity-attr id-selector attribute)))