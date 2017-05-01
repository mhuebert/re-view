(defproject re-view-hiccup "0.1.3-SNAPSHOT"
  :description "Hiccup parser for re-view"

  :url "https://www.github.com/mhuebert/re-view-hiccup"

  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.518"]]

  :profiles {:test     {:dependencies [[cljsjs/react-dom "15.5.0-0"]
                                       [cljsjs/react "15.5.0-0"]]}
             :provided {:dependencies [[cljsjs/react-dom "15.5.0-0"]
                                       [cljsjs/react "15.5.0-0"]]}}

  :lein-release {:deploy-via :clojars}

  :cljsbuild {:builds [{:id           "test"
                        :source-paths ["src" "test"]
                        :compiler     {:output-to     "resources/public/js/test.js"
                                       :output-dir    "resources/public/js/test"
                                       :asset-path    "/base/resources/public/js/test"
                                       :main          tests.runner
                                       :optimizations :none}}]}
  :plugins [[lein-doo "0.1.6"]]
  :source-paths ["src"])