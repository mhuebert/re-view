(defproject re-view/website "0.1.0"

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :min-lein-version "2.7.1"

  :dependencies [[thheller/shadow-cljs "2.0.41"]
                 [org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :source-paths ["src"
                 "../re_db/src"
                 "../prosemirror/src"
                 "../re_view/src"
                 "../material/src"
                 "../material/example"
                 "../prosemirror/example"
                 "../re_view/example"])
