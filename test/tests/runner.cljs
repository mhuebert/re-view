(ns tests.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-view.core-test]
            [re-view.router-test]
            [re-view.subscriptions-test]))

(doo-tests 're-view.core-test
           're-view.router-test
           're-view.subscriptions-test)
