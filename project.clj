(defproject re-view-prosemirror "0.1.4-SNAPSHOT"
  :description "Rich text editors built with ProseMirror in Re-View"
  :url "https://www.github.com/re-view/re-view-prosemirror"
  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}
  :min-lein-version "2.7.1"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.518"]
                 [cljsjs/markdown-it "7.0.0-0"]]
  :provided {:dependencies [[re-view "0.3.13"]
                            [re-view-material "0.1.1-SNAPSHOT"]]}
  :cljsbuild {:builds []}
  :plugins [[lein-npm "0.6.2"]]
  :lein-release {:deploy-via :clojars}
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

                       [rollup-plugin-commonjs "8.0.2"]
                       [rollup-plugin-json "2.1.1"]
                       [rollup-plugin-node-builtins "2.1.0"]
                       [rollup-plugin-node-resolve "3.0.0"]
                       [rollup-plugin-uglify "1.0.2"]
                       [rollup-watch "3.2.2"]]}
  :source-paths ["src" "example"])