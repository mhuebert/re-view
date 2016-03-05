(ns tests.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-view.core-test]))

(doo-tests 're-view.core-test)
