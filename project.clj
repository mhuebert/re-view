(defproject org.clojars.mhuebert/re-db "0.1.0-SNAPSHOT"
  :description "Tiny data store"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-2"]
            [lein-doo "0.1.6"]]

  :source-paths ["src"]
  :doo {:build "test"}


  :cljsbuild {:test-commands {"test" ["phantom"
                                      "resources/test/test.js"
                                      "resources/test/test.html"]}
              :builds        [{:id           "dev"
                               :source-paths ["src" "test"]
                               :figwheel     {:on-jsload "app.test/run"}
                               :compiler     {:main                 app.core
                                              ;:parallel-build       true
                                              :asset-path           "/js/compiled/out"
                                              :output-to            "resources/public/js/compiled/outliner.js"
                                              :output-dir           "resources/public/js/compiled/out"
                                              :source-map-timestamp true
                                              :cache-analysis       true}}

                              {:id           "js-test",
                               :compiler     {:main           firebase-test.core
                                              :output-to      "js-test/compiled.js",
                                              :output-dir     "js-test/out"
                                              :optimizations  :simple
                                              :source-map-dir "js-test/"
                                              :target         :nodejs},
                               :source-paths ["src" "js-test"]}
                              {:id           "test"
                               :source-paths ["src" "test"]
                               :compiler     {:output-to      "resources/public/js/test.js"
                                              :output-dir     "resources/public/js/test"
                                              :main           tests.runner
                                              :optimizations  :none
                                              :source-map-dir "resources/public/js/"}}]})
