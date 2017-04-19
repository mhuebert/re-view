(defproject org.clojars.mhuebert/re-view-material "0.1.0-SNAPSHOT"
            :description "material-design-lite components in re-view"
            :url "https://www.github.com/mhuebert/re-view-material"
            :license {:name "MIT License"
                      :url  "http://www.opensource.org/licenses/mit-license.php"}
            :min-lein-version "2.7.1"
            :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                           [org.clojure/clojurescript "1.9.518"]]
            :provided {:dependencies [[org.clojars.mhuebert/re-view "0.3.7"]]}
            :cljsbuild {:builds []}
            :source-paths ["src"])