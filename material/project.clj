(defproject re-view/material "0.2.0"

  :description "Material design components in re-view"

  :url "https://www.github.com/braintripping/re-view/tree/master/re_view_material"

  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}

  :min-lein-version "2.7.1"


  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [re-view "0.4.6"]]

  :cljsbuild {:builds []}

  :lein-release {:deploy-via :clojars
                 :scm        :git}

  :source-paths ["src"])