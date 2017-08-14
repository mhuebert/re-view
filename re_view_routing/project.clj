(defproject re-view-routing "0.1.4-SNAPSHOT"

  :description "ClojureScript routing tools"

  :url "https://www.github.com/braintripping/re-view/tree/master/re_view_routing"

  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.518"]]

  :profiles {:test {:dependencies [[re-view "0.3.21"]
                                   [re-db "0.1.11"]
                                   [org.clojure/core.match "0.3.0-alpha4"]
                                   [cljsjs/react-dom "15.5.0-0"]
                                   [cljsjs/react "15.5.0-0"]]}}

  :lein-release {:deploy-via :clojars
                 :scm        :git}

  :cljsbuild {:builds [{:id           "test"
                        :source-paths ["src" "test"]
                        :compiler     {:output-to     "resources/public/js/compiled/test.js"
                                       :output-dir    "resources/public/js/compiled/out"
                                       :asset-path    "/base/resources/public/js/compiled/out"
                                       :main          tests.runner
                                       :optimizations :none}}]}
  :plugins [[lein-doo "0.1.6"]]
  :source-paths ["src"])