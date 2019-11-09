(ns re-view.core-test
  (:require [cljs.test :refer [deftest is are testing]]
            [re-view.perf.bench :as bench]
            [re-view.hiccup.impl :as hiccup]
            [re-view.perf.destructure :as dest]
            [clojure.string :as str]))

(deftest core
  (is (= 1 1)))

(testing "benchmarks"

  (let [s "a-cat-is-here"]
    (bench/measure "camel-case" 10000
                   :perf (.replace s (js/RegExp. "-(.)" "g") (fn [match group index] (.toUpperCase group)))
                   :idiomatic (str/replace s #"-(.)" (fn [[_ s]] (str/upper-case s)))))
  ;: perf           12ms
  ;: idiomatic      144ms


  (let [s (name :div#hi.a.b)]
    (bench/measure "parse-hiccup-keys" 100000
      :perf (dest/let [^js [_ tag id classes] (.exec hiccup/tag-pattern s)]
              #js{:tag     (or tag "div")
                  :id      id
                  :classes (some-> classes (.replace "." " "))})
      :idiomatic (let [[_ tag id classes] (re-matches hiccup/tag-pattern s)]
                   #js{:tag     (or tag "div")
                       :id      id
                       :classes (some-> classes (.replace "." " "))})))
  ;; :perf           33ms
  ;; :idiomatic      76ms


  (let [s "dwhatever"]
    (bench/measure "react-key" 100000
                   ;; no difference
                   :perf (.startsWith s "data-")
                   :idiomatic (str/starts-with? s "data-")))
  ;; :perf           8ms
  ;; :idiomatic      7ms

  )