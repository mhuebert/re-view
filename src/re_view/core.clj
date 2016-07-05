(ns re-view.core)

(defmacro defcomponent [name & body]
  `(def ~name
     (~'re-view.core/component ~@body)))
