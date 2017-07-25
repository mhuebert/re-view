(defproject {{name}} "0.1.0-SNAPSHOT"

            :url "https://www.github.com/re-view/re-view"

            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}

            :min-lein-version "2.7.1"

            :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                           [org.clojure/clojurescript "1.9.671"]
                           [re-view "0.3.20-SNAPSHOT"]

                           [cljsjs/react "15.6.1-0"]
                           [cljsjs/react-dom "15.6.1-0"]]

            :plugins [[lein-cljsbuild "1.1.5"]
                      [lein-figwheel "0.5.10"]]

            :source-paths ["src"]

            :figwheel {:ring-handler figwheel-server.core/handler
                       :server-port  5300}

            :profiles {:dev {:dependencies [[figwheel-pushstate-server "0.1.1-SNAPSHOT"]]}}

            :cljsbuild {:builds [{:id           "dev"
                                  :source-paths ["src"]
                                  :figwheel     true
                                  :compiler     {:main           "{{name}}.core"
                                                 :parallel-build true
                                                 :infer-externs  true
                                                 :source-map     true
                                                 :asset-path     "/js/compiled/out-dev"
                                                 :output-to      "resources/public/js/compiled/{{name}}.js"
                                                 :output-dir     "resources/public/js/compiled/out-dev"
                                                 :optimizations  :none}}

                                 {:id           "advanced"
                                  :source-paths ["src"]
                                  :compiler     {:main           "{{name}}.core"
                                                 :infer-externs  true
                                                 :parallel-build true
                                                 :output-to      "resources/public/js/compiled/{{name}}.js"
                                                 :output-dir     "resources/public/js/compiled/out-prod"
                                                 :optimizations  :advanced}}]}

            :eval-in-leiningen true)
