(defproject re-view/website "0.1.0-SNAPSHOT"

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :min-lein-version "2.7.1"

  :dependencies [[thheller/shadow-cljs "2.0.20"]
                 [org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]

                 [re-view "0.4.0-SNAPSHOT"]
                 [re-db "0.1.13"]
                 [re-view/prosemirror "0.2.0-SNAPSHOT"]
                 [re-view/material "0.2.0-SNAPSHOT"]

                 [org.clojure/core.match "0.3.0-alpha5"]]

  :plugins [[lein-figwheel "0.5.12"]
            [lein-cljsbuild "1.1.6" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :source-paths ["src"]

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main                 "app.core"
                                       :closure-defines      {re-view.core/INSTRUMENT! true}
                                       :output-to            "resources/public/js/compiled/app.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :asset-path           "/js/compiled/out"
                                       :source-map-timestamp true
                                       :source-map           true
                                       :language-in          :ecmascript5
                                       :optimizations        :none}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:main          "app.core"
                                       :infer-externs true
                                       :language-in   :ecmascript5
                                       ;:language-out  :es5

                                       ;:pseudo-names  true
                                       :asset-path    "/js/out"
                                       :output-dir    "resources/public/js/compiled/out-prod"
                                       :output-to     "resources/public/js/compiled/app.js"
                                       :source-map    "resources/public/js/compiled/app.js.map"
                                       :optimizations :advanced
                                       }}]})
