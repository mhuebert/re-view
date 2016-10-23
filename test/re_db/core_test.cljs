(ns re-db.core-test
  (:require [cljs.test :refer-macros [deftest is]]
            [re-db.core :as d]))


(deftest basic
  (let [db (d/create {})]

    (is (satisfies? cljs.core/IDeref db)
        "DB is an atom")

    (d/transact! db [{:id "herman"}])

    (is (false? (d/has? @db "herman"))
        "Inserting an entity without attributes is no-op")

    (d/transact! db [{:id "herman" :occupation "teacher"}])

    (is (= "herman" (d/get @db "herman" :id))
        "Upsert with :db.unique/identity")

    (is (= "teacher" (d/get @db "herman" :occupation))
        "Set and read attributes, via lookup ref")


    (is (= 1 (count (d/entities @db [:occupation "teacher"])))
        "Query on non-indexed attr")

    (d/transact! db [{:id "fred" :occupation "teacher"}])

    (is (= 2 (count (d/entities @db [:occupation "teacher"])))
        "Verify d/insert! and query on non-indexed field")

    (d/transact! db [[:db/retract-attr "herman" :occupation]])

    (is (nil? (d/get @db "herman" :occupation))
        "Retract attribute")

    (d/transact! db [[:db/retract-attr "herman" :id]])
    (is (nil? (d/entity @db "herman"))
        "Entity with no attributes is removed")

    (is (false? (contains? (get-in @db [:index :id]) "herman"))
        "Index has been removed")

    (d/transact! db [{:id   "me"
                      :dog  "herman"
                      :name "Matt"}])
    (d/transact! db [{:id  "me"
                      :dog nil}])

    (is (= (d/entity @db "me") {:id "me" :name "Matt"})
        "Setting a value to nil is equivalent to retracting it")

    ))

(deftest cardinality-many
  (let [db (d/create {:id       {:db/unique :db.unique/identity}
                      :children {:db/cardinality :db.cardinality/many
                                 :db/index       true}})]

    (d/transact! db [{:id "fred" :children "pete"}])

    (is (true? (contains? (get-in @db [:index :children]) "pete"))
        "cardinality/many attribute can be indexed")

    ;; second child
    (d/transact! db [{:id "fred" :children "sally"}])

    (is (= #{"sally" "pete"} (d/get @db "fred" :children))
        "cardinality/many attribute returned as set")

    (is (true? (contains? (get-in @db [:index :children "sally"]) (d/resolve-id @db "fred"))))
    (is (true? (contains? (get-in @db [:index :children "pete"]) (d/resolve-id @db "fred")))
        "cardinality/many indexes point to entity")

    (d/transact! db [[:db/retract-attr "fred" :children "sally"]])

    (is (false? (contains? (get-in @db [:index :children "sally"]) (d/resolve-id @db "fred")))
        "removed many-index")

    (is (empty? (get-in @db [:index :children "sally"]))
        "many-index for attr is empty")

    (is (contains? (get-in @db [:index :children]) "pete"))
    (is (= #{"pete"} (d/get @db "fred" :children)))))