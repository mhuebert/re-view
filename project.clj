(defproject org.clojars.mhuebert/re-view-routing "0.1.0-SNAPSHOT"

  :description "ClojureScript routing tools"

  :url "https://www.github.com/mhuebert/re-view-prosemirror"

  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.518"]]

  :profiles {:test {:dependencies [[org.clojars.mhuebert/re-view "0.3.9"]
                                   [org.clojars.mhuebert/re-db "0.1.8"]
                                   [org.clojure/core.match "0.3.0-alpha4"]
                                   [cljsjs/react-dom "15.5.0-0"]
                                   [cljsjs/react "15.5.0-0"]]}}

  :cljsbuild {:builds [{:id           "test"
                        :source-paths ["src" "test"]
                        :compiler     {:output-to     "resources/public/js/test.js"
                                       :output-dir    "resources/public/js/test"
                                       :asset-path    "/base/resources/public/js/test"
                                       :main          tests.runner
                                       :optimizations :none}}]}
  :plugins [[lein-doo "0.1.6"]]
  :source-paths ["src"])