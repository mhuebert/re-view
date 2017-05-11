(ns tests.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-view.hiccup-test]))

(doo-tests 're-view.hiccup-test)
