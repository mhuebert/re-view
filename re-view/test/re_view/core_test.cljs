(ns re-view.core-test
  (:refer-clojure :exclude [let])
  (:require [cljs.test :refer [deftest is are testing]]
            [re-view.perf.bench :as bench]
            [re-view.hiccup.impl :as hiccup]
            [applied-science.js-interop.destructure :refer [let]]
            [clojure.string :as str]
            [applied-science.js-interop :as j]
            [goog.object :as gobj]))

(deftest core
  (is (= 1 1)))

(testing "benchmarks"

  (let [s "a-cat-is-here"]
    (bench/measure "camel-case" 1000
      :perf (.replace s (js/RegExp. "-(.)" "g")
                      (fn [match group index]
                        (str/upper-case group)))
      :idiomatic (str/replace s #"-(.)"
                              (fn [[_ s]]
                                (str/upper-case s)))))
  ;; :perf           2ms
  ;; :idiomatic      47ms


  (let [s (name :div#hi.a.b)]
    (bench/measure "parse-hiccup-keys" 10000
      :perf (let [^js [_ tag id classes] (.exec hiccup/tag-pattern s)]
              #js{:tag (or tag "div")
                  :id id
                  :classes (some-> classes (.replace "." " "))})
      :idiomatic (let [[_ tag id classes] (re-matches hiccup/tag-pattern s)]
                   #js{:tag (or tag "div")
                       :id id
                       :classes (some-> classes (.replace "." " "))})))
  ;; :perf           11ms
  ;; :idiomatic      29ms


  (let [s "dwhatever"]
    (bench/measure "react-key" 100000
      ;; no difference
      :perf (.startsWith s "data-")
      :idiomatic (str/starts-with? s "data-")))
  ;; :perf           8ms
  ;; :idiomatic      7ms

  (let [f identity
        v ["a" "b" "c" "d"]]
    (bench/measure "str/join with map or mapv" 10000
      :mapv (str/join " " (mapv f v))
      :map (str/join " " (map f v))))
  ;; :mapv           19ms
  ;; :map            31ms

  (bench/measure "str/replace vs .replace" 100000
    :str/replace (str/replace "hello.there.my.friend" "." " ")
    :replace (.replace "hello.there.my.friend" (js/RegExp. "\\." "g") " "))
  ;; :str/replace    106ms
  ;; :replace        27ms


  )