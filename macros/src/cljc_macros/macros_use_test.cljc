(ns cljc-macros.macros-use-test
  (:require [cljc-macros.macros-test :as mt]))

(println (mt/A) (.call mt/A))