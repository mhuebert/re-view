(ns re-view.core-test
  (:refer-clojure :exclude [let])
  (:require [cljs.test :refer [deftest is are testing]]
            [re-view.perf.bench :as bench]
            [re-view.hiccup.impl :as hiccup]
            [applied-science.js-interop :refer [let]]
            [clojure.string :as str]
            [applied-science.js-interop :as j]
            [goog.object :as gobj]
            [cljs-bean.core :as bean]))

(deftest bench
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
    (let [^js obj #js{:a 1 :b 2 :c 3 :d 4 :e 5 :abcdef (when (nth [false true] 1) 1)}
          amap (array-map :a 1 :b 2 :c 3 :abcdef 1)
          hmap (hash-map :a 1 :b 2 :c 3 :abcdef 1)
          beaned (bean/->clj obj)]
      (hmap :a 1)
      (bench/measure "cljs-bean" 1000000
        ;:to-clj/kw (:abcdef (js->clj obj))
        ;:to-clj/get (get (js->clj obj) :abcdef)
        :j/get (j/get obj :abcdef)
        :.- (.-abcdef obj)
        :gobj (gobj/get obj "abcdef")
        ;:->bean/kw (:abcdef (bean/->clj obj))
        :bean/kw (:abcdef beaned)
        :bean/get (get beaned :abcdef)
        :array-map/kw (:abcdef amap)
        :array-map/get (get amap :abcdef)
        :destructure/map (let [{:keys [abcdef]} amap] abcdef)
        :destructure/bean (let [{:keys [abcdef]} beaned] abcdef)
        :j/destructure (j/let [{:keys [abcdef]} obj] abcdef)
        :hash-map/kw (:abcdef hmap)
        :hash-map/get (get hmap :abcdef)
        ;:bean/->clj (bean/->clj obj)
        ;:js->clj (js->clj obj)
        ))

    ))