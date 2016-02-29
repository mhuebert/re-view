(ns re-view.index
  (:require [re-view.core :as view]
            [re-db.core :as d]))

(defn components-by-view-id [db id]
  (when id
    (->> (d/entities @db [:view/id id])
         (map :view/component)
         (filter view/mounted?))))

(defn components-by-e [db e]
  (when e (components-by-view-id db (d/get @db e :id))))

(defn register-view
  "Keep index to components based on id"
  ([db this indexes]
   (d/transact! db [(merge indexes
                           {:db/id          -1
                            :view/component this})])))

(defn deregister-view
  "Discard index"
  [db this]
  (js/setTimeout
    #(do
      (d/transact! db [[:db/retract-entity (:db/id (d/entity @db [:view/component this]))]])) 0))