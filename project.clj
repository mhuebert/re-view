(defproject re-view-material "0.1.1-SNAPSHOT"
  :description "Material design components in re-view"
  :url "https://www.github.com/re-view/re-view-material"
  :license {:name "MIT License"
            :url  "http://www.opensource.org/licenses/mit-license.php"}
  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.518"]
                 [re-view-routing "0.1.2"]]
  :profiles {:provided {:dependencies [[re-view "0.3.13"]]}
             :dev      {:dependencies [[org.clojure/test.check "0.9.0"]]}}
  :cljsbuild {:builds []}
  :lein-release {:deploy-via :clojars}
  :source-paths ["src" "example"])