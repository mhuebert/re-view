(defproject re-view "0.3.10-SNAPSHOT"
  :description "Tiny React wrapper"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.518"]
                 [org.clojars.mhuebert/re-db "0.1.8"]
                 [re-view-hiccup "0.1.1"]]

  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-2"]
            [lein-doo "0.1.6"]]

  :source-paths ["src" "example"]

  :doo {:build "test"}

  :lein-release {:deploy-via :clojars}

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
                        :compiler     {:output-to     "resources/public/js/test.js"
                                       :output-dir    "resources/public/js/test"
                                       :asset-path    "/base/resources/public/js/test"
                                       :main          tests.runner
                                       :infer-externs true
                                       :optimizations :none}}]}
  :profiles {:provided {:dependencies [[cljsjs/react-dom "15.5.0-0"]
                                       [cljsjs/react "15.5.0-0"]]}
             :dev      {:dependencies [[cljsjs/react-with-addons "15.5.0-0"]
                                       [cljsjs/react-dom "15.5.0-0"]]}
             :test     {:dependencies [[cljsjs/react-dom "15.5.0-0"]
                                       [cljsjs/react "15.5.0-0"]]}})
