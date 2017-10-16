(defproject re-view-prosemirror "0.1.11-SNAPSHOT"

  :description "Rich text editors built with ProseMirror in Re-View"

  :url "https://www.github.com/braintripping/re-view/tree/master/re_view_prosemirror"

  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [cljsjs/markdown-it "7.0.0-0"]]

  :provided {:dependencies [[re-view "0.3.19"]
                            [re-view-material "0.1.4"]]}

  :cljsbuild {:builds []}

  :plugins [[lein-npm "0.6.2"]]

  :lein-release {:deploy-via :clojars
                 :scm        :git}

  :source-paths ["src" "example"])