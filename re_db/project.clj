(defproject re-db "0.1.13"
  :description "in-memory javascript key-value store inspired by Datomic and DataScript"
  :url "https://www.github.com/braintripping/re-view/tree/master/re_db"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.946"  :scope "provided"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]]

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-figwheel "0.5.11"]
            [lein-doo "0.1.6"]]

  :source-paths ["src" "example"]

  :doo {:build "test"}

  :lein-release {:deploy-via :clojars
                 :scm        :git}

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     {:on-jsload "app.test/run"}
                        :compiler     {:main                 app.core
                                       :parallel-build       true
                                       :asset-path           "/js/compiled/out"
                                       :output-to            "resources/public/js/compiled/outliner.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :source-map-timestamp true
                                       :cache-analysis       true}}
                       {:id           "test"
                        :source-paths ["src" "test"]
                        :compiler     {:output-to      "resources/public/js/test.js"
                                       :output-dir     "resources/public/js/test"
                                       :main           re-db.runner
                                       :optimizations  :none
                                       :source-map-dir "resources/public/js/"}}]})
