(ns leiningen.new.re-view
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "re-view"))

(defn re-view
  "FIXME: write documentation"
  [name]
  (let [data {:name      name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh 'lein new' re-view project.")
    (->files data
             ["src/{{sanitized}}/core.cljs" (render "core.cljs" data)]
             ["project.clj" (render "project.clj" data)]
             ["resources/public/index.html" (render "index.html" data)]
             [".gitignore" (render ".gitignore" data)]
             ["README.md" (render "README.md" data)])))
