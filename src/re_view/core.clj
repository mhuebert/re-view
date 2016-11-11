(ns ^:figwheel-always re-view.core)

(defmacro defcomponent [name & body]
  `(def ~name
     (~'re-view.core/component ~@body)))

(defmacro reactive [bindings & body]
  `(let [patterns# (~'atom)
         cb# (~'atom)
         pattern-subscribe# (fn [next-patterns#]
                              (when (not= @patterns# next-patterns#)
                                (~'apply ~'re-db.d/unlisten! (concat @patterns# (list @cb#)))
                                (~'apply ~'re-db.d/listen! (concat next-patterns# (list @cb#)))
                                (reset! patterns# next-patterns#)))]
     (~'re-view.core/component
       :component-will-mount
       (fn [this#] (reset! cb# (fn []
                                 (~'re-view.core/force-update! this#))))
       :render
       (fn ~bindings
         (let [result# (~'re-db.core/capture-patterns ~@body)]
           (pattern-subscribe# (:patterns result#))
           (:value result#))))))