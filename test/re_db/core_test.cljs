(ns re-db.core-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [re-db.core :as d :include-macros true])
  (:require-macros [tests.helpers :refer [throws]]))

(deftest basic
  (let [db (d/create {})]

    (is (satisfies? cljs.core/IDeref db)
        "DB is an atom")

    (d/transact! db [{:id "herman"}])

    (is (false? (d/has? @db "herman"))
        "Inserting an entity without attributes is no-op")

    (d/transact! db [{:id "herman" :occupation "teacher"}])

    (is (= "herman" (d/get @db "herman" :id))
        "Entity is returned with :id attribute")

    (is (= "teacher" (d/get @db "herman" :occupation))
        "d/get an attribute of an entity")

    (is (= 1 (count (d/entities @db [:occupation "teacher"])))
        "Query on non-indexed attr")

    (d/transact! db [{:id "fred" :occupation "teacher"}])

    (is (= 2 (count (d/entities @db [:occupation "teacher"])))
        "Verify d/insert! and query on non-indexed field")

    (d/transact! db [[:db/retract-attr "herman" :occupation]])

    (is (nil? (d/get @db "herman" :occupation))
        "Retract attribute")

    (is (= 1 (count (d/entities @db [:occupation "teacher"])))
        "Retract non-indexed field")

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

    (is (= :error (try (d/transact! db [[:db/add "fred" :id "some-other-id"]])
                       nil
                       (catch js/Error e :error)))
        "Cannot change :id of entity")))

(deftest lookup-refs
  (let [db (-> (d/create {:email {:db/unique true}})
               (d/transact! [{:id    "fred"
                              :email "fred@example.com"}]))]

    (is (= (d/entity @db "fred")
           (d/entity @db [:email "fred@example.com"]))
        "Can substitute unique attr for id (à la 'lookup refs')")))

(deftest cardinality-many
  (let [db (-> (d/create {:id       {:db/unique :db.unique/identity}
                          :children {:db/cardinality :db.cardinality/many
                                     :db/index       true}})
               (d/transact! [{:id       "fred"
                              :children "pete"}]))]

    (is (true? (contains? (get-in @db [:index :children]) "pete"))
        "cardinality/many attribute can be indexed")

    ;; second child
    (d/transact! db [{:id "fred" :children "sally"}])

    (is (= #{"sally" "pete"} (d/get @db "fred" :children))
        "cardinality/many attribute returned as set")

    (is (= #{"fred"}
           (d/entity-ids @db [:children "sally"])
           (d/entity-ids @db [:children "pete"]))
        "look up via cardinality/many index")


    (testing "remove value from cardinality/many attribute"
      (d/transact! db [[:db/retract-attr "fred" :children "sally"]])

      (is (= #{} (d/entity-ids @db [:children "sally"]))
          "index is removed on retraction")
      (is (= #{"fred"} (d/entity-ids @db [:children "pete"]))
          "index remains for other value")
      (is (= #{"pete"} (d/get @db "fred" :children))
          "attribute has correct value"))

    (testing "unique cardinality/many attribute"

      (d/merge-schema! db {:pets {:db/cardinality :db.cardinality/many
                                  :db/index       true
                                  :db/unique      true}})

      (d/transact! db [[:db/add "fred" :pets "fido"]])

      (is (= "fred" (:id (d/entity @db [:pets "fido"]))))

      (throws (d/transact! db [[:db/add "herman" :pets "fido"]])
              "some message"))))

(deftest listeners
  (let [db (d/create {:person/children {:db/cardinality :db.cardinality/many
                                        :index          true
                                        :db/unique      true}})
        log (atom {})
        cb (fn [path] (partial swap! log assoc path))]

    (d/transact! db [{:id   "mary"
                      :name "Mary"}
                     [:db/add "mary" :person/children "john"]
                     {:id   "john"
                      :name "John"}])

    (throws (d/listen! db '[_ :person/pets "fido"] (cb :val-listener-2))
            "attr-val listener requires unique attribute")

    (is (= "Mary" (d/get @db [:person/children "john"] :name))
        )

    (d/listen! db ["mary"] (cb :id-mary))
    (d/listen! db [[:person/children "peter"]] (cb :children-peter))
    #_(d/listen! db '[_ :person/children "peter"] (cb :val-listener))

    (d/transact! db [{:id   "peter"
                      :name "Peter"}
                     [:db/add "mary" :person/children "peter"]
                     [:db/add "mary" :name "MMMary"]])

    (is (= (d/entity @db "mary") (:id-mary @log)))
    (is (= (d/entity @db "mary") (:children-peter @log)))
    #_(is (= "mary" (:val-listener @log)))

    (d/transact! db [[:db/retract-attr "mary" :person/children "peter"]])

    (is (nil? (:children-peter @log)))


    ;; attr-val listeners - look for `nil` on retraction, id on added



    ))