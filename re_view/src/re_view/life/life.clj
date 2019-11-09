(ns re-view.life)

(defmacro as-life [this & body]
  `(let [this# ~this]
    (binding [~'re-view.life/*current* this#]
      (~'re-view.life/before-eval this#)
      (let [value# (do ~@body)]
      (~'re-view.life/after-eval this#)
      value#))))