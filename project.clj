(defproject org.clojars.mhuebert/re-view "0.2.2"
  :description "Tiny React wrapper"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.clojars.mhuebert/re-db "0.1.5"]
                 [org.clojars.mhuebert/sablono "0.7.4"]
                 [cljsjs/react-with-addons "15.3.1-0"]
                 [cljsjs/react-dom-server "15.3.1-0" :exclusions [cljsjs/react]]
                 [cljsjs/react-dom "15.3.1-0" :exclusions [cljsjs/react]]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-2"]
            [lein-doo "0.1.6"]]

  :source-paths ["src"]

  :doo {:build "test"}

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     {:on-jsload "app.test/run"}
                        :compiler     {:main                 app.core
                                       ;:parallel-build       true
                                       :asset-path           "/js/compiled/out"
                                       :output-to            "resources/public/js/compiled/outliner.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :source-map-timestamp true
                                       :cache-analysis       true}}

                       {:id           "test"
                        :source-paths ["src" "test"]
                        :compiler     {:output-to      "resources/public/js/test.js"
                                       :output-dir     "resources/public/js/test"
                                       :main           tests.runner
                                       :optimizations  :none
                                       :source-map-dir "resources/public/js/"}}]})
