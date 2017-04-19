(defproject org.clojars.mhuebert/re-view-prosemirror "0.1.0-SNAPSHOT"
            :description "prosemirror editor in re-view"
            :url "https://www.github.com/mhuebert/re-view-prosemirror"
            :license {:name "MIT License"
                      :url  "http://www.opensource.org/licenses/mit-license.php"}
            :min-lein-version "2.7.1"
            :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                           [org.clojure/clojurescript "1.9.518"]]
            :provided {:dependencies [[org.clojars.mhuebert/re-view "0.3.7"]
                                      [org.clojars.mhuebert/re-view-material "0.1.0-SNAPSHOT"]]}
            :cljsbuild {:builds []}
            :plugins [[lein-npm "0.6.2"]]
            :npm {:dependencies [[prosemirror-view "0.20.3"]
                                 [prosemirror-state "0.20.0"]
                                 [prosemirror-keymap "0.20.0"]
                                 [prosemirror-model "0.20.0"]
                                 [prosemirror-commands "0.20.0"]
                                 [prosemirror-history "0.20.0"]
                                 [prosemirror-markdown "0.20.0"]
                                 [prosemirror-transform "0.20.0"]
                                 [prosemirror-schema-list "0.20.0"]
                                 [prosemirror-inputrules "0.20.0"]
                                 [markdown-it "6.0.4"]

                                 [rollup-plugin-commonjs "8.0.2"]
                                 [rollup-plugin-json "2.1.1"]
                                 [rollup-plugin-node-builtins "2.1.0"]
                                 [rollup-plugin-node-resolve "3.0.0"]
                                 [rollup-watch "3.2.2"]]}
            :source-paths ["src"])