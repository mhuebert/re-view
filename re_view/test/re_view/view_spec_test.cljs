(ns re-view.view-spec-test
  (:require [re-view.view-spec :as s]
            [cljs.test :refer [deftest is are testing]]))

(deftest view-specs
  (testing "resolve-spec"

    (s/defspecs {::color {:spec #{:primary :accent}
                          :doc  "Specifies color variable from theme."}
                 ::even? even?})


    (are [name result]
      (= (s/resolve-spec name) result)

      ;; def'd function spec
      ::even? {:spec      even?
               :spec-name ::even?}

      ;; def'd set spec with :doc
      ::color {:spec #{:primary :accent}
               :doc  "Specifies color variable from theme."}

      ;; inline spec
      {:spec odd?} {:spec odd?}

      ;; primitive
      :Boolean {:spec      boolean?
                :spec-name :Boolean})

    (is (= (s/spec-kind (s/resolve-spec ::color))
           :Set))
    (is (= (s/spec-kind (s/resolve-spec ::even?))
           ::even?))
    (is (= (s/spec-kind (s/resolve-spec :Function))
           :Function))

    (is (= (s/resolve-spec {:spec :Function})
           {:spec      fn?
            :spec-name :Function}))


    (is (= (s/normalize-props-map {:x ::color
                                   :y {:spec         :Function
                                       :required     true
                                       :pass-through true
                                       :default      "y-value"}})
           {:x              {:spec #{:primary :accent}
                             :doc  "Specifies color variable from theme."}
            :y              {:spec         fn?
                             :spec-name    :Function
                             :required     true
                             :pass-through true
                             :default      "y-value"}
            :props/consumed [:x]
            :props/required [:y]
            :props/defaults {:y "y-value"}}))

    (is (thrown? js/Error (s/validate-spec :x (s/resolve-spec :Function) "s")))
    (is (nil? (s/validate-spec :x (s/resolve-spec :Function) even?)))





    ))