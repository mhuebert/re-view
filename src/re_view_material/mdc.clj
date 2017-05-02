(ns re-view-material.mdc)

(defmacro defadapter
  ""
  ([name] `(~'re-view-material.mdc/defadapter ~name nil))
  ([name body]
   `(def ~name {:name    ~(str name)
                :adapter (~'re-view-material.mdc/make-foundation
                           ~(str name)
                           (~'re-view-material.mdc/foundation-class ~(str name))
                           ~body)})))

