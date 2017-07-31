(ns tests.runner
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [doo.runner :refer-macros [doo-tests]]
            [re-view.router-test]))

(doo-tests 're-view.router-test)
