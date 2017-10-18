(defproject re-view "0.4.1-SNAPSHOT"
  :description "ClojureScript React Library"
  :url "https://www.github.com/braintripping/re-view/tree/master/re_view"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [re-db "0.1.13"]]

  :plugins [[lein-doo "0.1.8"]]

  :source-paths ["src"
                 "example"]

  :doo {:build "test"}

  :lein-release {:deploy-via :clojars
                 :scm        :git}

  :cljsbuild {:builds [{:id           "test"
                        :source-paths ["src" "test"]
                        :compiler     {:output-to     "resources/public/js/test.js"
                                       :output-dir    "resources/public/js/test"
                                       :asset-path    "/base/resources/public/js/test"
                                       :main          re-view.runner
                                       :infer-externs true
                                       :install-deps  true
                                       :optimizations :none}}]})
