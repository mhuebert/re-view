(ns re-view.index
  (:require [re-view.core :as view]
            [re-db.core :as d]))

(defn components-by-view-id [db id]
  (when id
    (->> (d/entities @db [:re-view/id id])
         (map :view/component))))

(defn register-view
  "Keep index to components based on id"
  ([db this indexes]
   (let [id (d/squuid)]
     (d/transact! db [(merge indexes
                             {:id             id
                              :view/component this})]))))

(defn deregister-view
  "Discard index"
  [db this]
  (js/setTimeout
    #(d/transact! db [[:db/retract-entity (:id (d/entity @db [:view/component this]))]]) 0))