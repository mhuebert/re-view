(ns re-view.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-view.core-test]
            [re-view.state-test]
            [re-view.view-spec-test]))

(doo-tests 're-view.core-test
           're-view.state-test
           're-view.view-spec-test)
