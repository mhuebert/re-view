(defproject re-view "0.3.34"
  :description "Tiny React wrapper"
  :url "https://www.github.com/braintripping/re-view/tree/master/re_view"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [re-db "0.1.13"]
                 [re-view-hiccup "0.1.15"]]

  :plugins [[lein-doo "0.1.6"]]

  :source-paths ["src" "example"]

  :doo {:build "test"}

  :lein-release {:deploy-via :clojars
                 :scm        :git}

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     {:on-jsload "app.test/run"}

                        :compiler     {:main                 app.core
                                       ;:parallel-build       true
                                       :install-deps true
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
                                       :main          re-view.runner
                                       :infer-externs true
                                       :install-deps true
                                       :optimizations :none}}]})
