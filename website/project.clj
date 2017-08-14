(defproject re-view/website "0.1.0-SNAPSHOT"

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.854"]

                 [re-view "0.3.26"]
                 [re-db "0.1.11"]
                 [re-view-routing "0.1.4-SNAPSHOT"]
                 [re-view-prosemirror "0.1.7"]
                 [re-view-material "0.1.7"]

                 [org.clojure/core.match "0.3.0-alpha4"]
                 [cljsjs/react "16.0.0-beta.2-0"]
                 [cljsjs/react-dom "16.0.0-beta.2-0"]
                 [cljsjs/react-dom-server "16.0.0-beta.2-0"]
                 [cljsjs/markdown-it "7.0.0-0"]
                 [cljsjs/highlight "9.6.0-0"]
                 ]

  :plugins [[lein-figwheel "0.5.12"]
            [lein-cljsbuild "1.1.6" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]


  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]
                                  [figwheel-pushstate-server "0.1.2"]]}}
  :source-paths ["src"]

  :figwheel {:ring-handler figwheel-server.core/handler
             :server-port 5301}

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
