(ns tests.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-view-hiccup.core-test]))

(doo-tests 're-view-hiccup.core-test)
