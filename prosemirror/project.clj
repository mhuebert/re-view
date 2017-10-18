(defproject re-view/prosemirror "0.2.2-SNAPSHOT"

  :description "Rich text editors built with ProseMirror in Re-View"

  :url "https://www.github.com/braintripping/re-view/tree/master/re_view_prosemirror"

  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}

  :min-lein-version "2.7.1"

  :dependencies [[thheller/shadow-cljs "2.0.20"]
                 [org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [re-view "0.3.34"]]

  :cljsbuild {:builds []}

  :plugins [[lein-npm "0.6.2"]]

  :lein-release {:deploy-via :clojars
                 :scm        :git}

  :source-paths ["src"])