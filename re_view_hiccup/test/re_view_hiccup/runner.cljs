(ns re-view-hiccup.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-view-hiccup.hiccup-test]))

(doo-tests 're-view-hiccup.hiccup-test)
