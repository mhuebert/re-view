(ns tests.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-db.core-test]))

(doo-tests 're-db.core-test)
