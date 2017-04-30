(ns tests.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [cljsjs.react]
            [cljsjs.react.dom]
            [re-view.core-test]
            [re-view.state-test]))

(doo-tests 're-view.core-test
           're-view.state-test)
