(ns re-db.core-test
  (:require [cljs.test :refer-macros [deftest is]]
            [re-db.core :as d]))


(deftest basic
  (let [db (d/create {:id {:db/unique :db.unique/identity}})]

    (is (satisfies? cljs.core/IDeref db)
        "DB is an atom")

    (d/transact! db [{:db/id -1 :id "herman"}])

    (is (= "herman" (d/get @db [:id "herman"] :id))
        "Upsert with :db.unique/identity")

    (is (contains? (get-in @db [:index :id]) "herman")
        "Unique index exists")

    (d/add! db [:id "herman"] :occupation "teacher")

    (is (= "teacher" (d/get @db [:id "herman"] :occupation))
        "Set and read attributes, via lookup ref")

    (is (number? (d/get-in @db [:id "herman"] :db/id))
        "Retrieve entity; check for presence of numeric :db/id")

    (is (= 1 (count (d/entities @db [:occupation "teacher"])))
        "Query on non-indexed attr")

    (d/add! db {:id "fred" :occupation "teacher"})

    (is (= 2 (count (d/entities @db [:occupation "teacher"])))
        "Verify d/insert! and query on non-indexed field")

    (d/retract! db [:id "herman"] :occupation)
    (is (nil? (d/get @db [:id "herman"] :occupation))
        "Retract attribute")

    (d/retract! db [:id "herman"] :id)
    (is (nil? (d/entity @db [:id "herman"]))
        "Entity with no attributes is removed")

    (is (false? (contains? (get-in @db [:index :id]) "herman"))
        "Index has been removed")))

(deftest cardinality-many
  (let [db (d/create {:id       {:db/unique :db.unique/identity}
                      :children {:db/cardinality :db.cardinality/many
                                 :db/index       true}})]

    (d/add! db {:id "fred" :children "pete"})

    (is (true? (contains? (get-in @db [:index :children]) "pete"))
        "cardinality/many attribute can be indexed")

    ;; second child
    (d/add! db [:id "fred"] :children "sally")

    (is (= #{"sally" "pete"} (d/get @db [:id "fred"] :children))
        "cardinality/many attribute returned as set")

    (is (true? (contains? (get-in @db [:index :children "sally"]) (d/resolve-id @db [:id "fred"]))))
    (is (true? (contains? (get-in @db [:index :children "pete"]) (d/resolve-id @db [:id "fred"])))
        "cardinality/many indexes point to entity")

    (d/retract! db [:id "fred"] :children "sally")

    (is (false? (contains? (get-in @db [:index :children "sally"]) (d/resolve-id @db [:id "fred"])))
        "removed many-index")

    (is (empty? (get-in @db [:index :children "sally"]))
        "many-index for attr is empty")

    (is (contains? (get-in @db [:index :children]) "pete"))
    (is (= #{"pete"} (d/get @db [:id "fred"] :children)))))

(deftest join-temp-ids
  (let [db (d/create {})]
    (let [n (->> [{:db/id -1 :id "Matt"}
                  {:db/id -2 :id "Herman"}]
                 (d/resolve-ids db)
                 (mapcat d/map->txs)
                 set)]
      (is (= n (set [[:db/add 2 :db/id 2]
                     [:db/add 2 :id "Matt"]
                     [:db/add 3 :db/id 3]
                     [:db/add 3 :id "Herman"]])))))

  (let [db (d/create {:id {:db/unique :db.unique/identity}})]
    (d/transact! db [{:db/id -1 :id "Matt"}
                     {:db/id -2 :id "Herman"}
                     {:db/id -1 :last-name "Huebert"}
                     [:db/add -2 :last-name "Hughes"]])

    (is (= {:db/id 2 :id "Matt" :last-name "Huebert"}
           (d/entity @db [:id "Matt"]))
        "Join tempids")

    (is (= {:db/id 3 :id "Herman" :last-name "Hughes"}
             (d/entity @db [:id "Herman"]))
          "Use tempids in non-map txs"))

  )