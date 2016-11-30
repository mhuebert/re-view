(ns ^:figwheel-always re-view.core)

(defmacro defview
  ([name render] `(~'re-view.core/defview ~name {} ~render))
  ([name methods render]
   `(def ~name
      (~'re-view.core/view ~(assoc methods :render render)))))
