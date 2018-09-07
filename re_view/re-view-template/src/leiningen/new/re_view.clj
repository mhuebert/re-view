(ns leiningen.new.re-view
    (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
      [leiningen.core.main :as main]))

(def render (renderer "re-view"))

(defn re-view
      "Create a fresh re-view project with basic example page"
      [name]
      (let [data {:name            name
                  :munged-name     (munge name)
                  :re-view-version "0.4.15"
                  :sanitized       (name-to-path name)}]
           (main/info "Generating fresh 'lein new' re-view project.")
           (->files data
                    ["src/{{sanitized}}/core.cljs" (render "core.cljs" data)]
                    ["src/{{sanitized}}/examples.cljs" (render "examples.cljs" data)]
                    ["deps.edn" (render "deps.edn" data)]
                    ["shadow-cljs.edn" (render "shadow-cljs.edn" data)]
                    ["package.json" (render "package.json" data)]
                    ["resources/public/index.html" (render "index.html" data)]
                    [".gitignore" (render ".gitignore" data)]
                    ["README.md" (render "README.md" data)])))
