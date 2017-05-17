(defproject re-view/website "0.1.0-SNAPSHOT"

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.518"]

                 [re-view "0.3.12"]
                 [re-db "0.1.11-SNAPSHOT"]
                 [re-view-routing "0.1.1"]
                 [re-view-prosemirror "0.1.3-SNAPSHOT"]
                 [re-view-material "0.1.0"]

                 [org.clojure/core.match "0.3.0-alpha4"]
                 [cljsjs/react "15.5.0-0"]
                 [cljsjs/react-dom "15.5.0-0"]
                 [cljsjs/react-dom-server "15.5.0-0"]
                 [cljsjs/markdown-it "7.0.0-0"]
                 [cljsjs/highlight "9.6.0-0"]]

  :plugins [[lein-figwheel "0.5.10"]
            [lein-cljsbuild "1.1.5" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]


  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :source-paths ["src"]

  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main                 "app.core"
                                       :output-to            "resources/public/js/app.js"
                                       :output-dir           "resources/public/js/out"
                                       :asset-path           "/js/out"
                                       :source-map-timestamp true
                                       :source-map           true

                                       :language-in          :es5


                                       :optimizations        :none}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:main          "app.core"
                                       :infer-externs true
                                       :language-in   :es5
                                       ;:language-out  :es5

                                       ;:pseudo-names  true
                                       :asset-path    "/js/out"
                                       :output-dir    "resources/public/js/out-prod"
                                       :output-to     "resources/public/js/app.min.js"
                                       :source-map    "resources/public/js/app.min.js.map"
                                       :optimizations :advanced
                                       }}]})
