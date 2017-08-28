(ns re-db.patterns)

(defmacro capture-patterns
  "Evaluates body, returning map with evaluation result and read patterns."
  [& body]
  `(binding [~'re-db.patterns/*pattern-log* {}]
     (let [{value#     :value
            tx-report# :tx-report} (~'re-db.core/db-log (do ~@body))
           patterns# ~'re-db.patterns/*pattern-log*]
       (~'re-db.core/notify-listeners tx-report#)
       {:value    value#
        :patterns patterns#})))