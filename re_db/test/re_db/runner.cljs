(ns re-db.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-db.core-test]
            [re-db.reactivity-test]))

(doo-tests 're-db.core-test
           're-db.reactivity-test)
