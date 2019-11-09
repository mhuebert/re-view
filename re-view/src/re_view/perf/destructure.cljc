(ns re-view.perf.destructure
  (:refer-clojure :exclude [fn let])
  (:require [clojure.core :as core]
            #?(:clj [re-view.perf.destructure.impl :as d]))
  #?(:cljs (:require-macros re-view.perf.destructure)))

#?(:clj
   (defmacro let
     "`let` with destructuring that supports js arrays"
     [bindings & body]
     `(core/let ~(d/destructure bindings) ~@body)))