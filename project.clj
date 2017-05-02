(defproject re-view-material "0.1.1-SNAPSHOT"
  :description "material-design-lite components in re-view"
  :url "https://www.github.com/mhuebert/re-view-material"
  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}
  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.518"]
                 [org.clojars.mhuebert/re-view-routing "0.1.0"]]
  :profiles {:provided {:dependencies [[org.clojars.mhuebert/re-view "0.3.9"]]}
             :dev      {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :cljsbuild {:builds []}
  :lein-release {:deploy-via :clojars}
  :source-paths ["src" "example"])