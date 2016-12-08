(ns re-db.core
  (:refer-clojure :exclude [peek]))

(defmacro capture-patterns [& body]
  `(binding [~'re-db.core/*access-log* (~'atom ~'re-db.core/blank-access-log)]
     (let [value# (do ~@body)
           patterns# (~'deref ~'re-db.core/*access-log*)]
       {:value    value#
        :patterns (~'re-db.core/access-log-patterns patterns#)})))

(defmacro peek [& body]
  `(binding [~'re-db.core/*access-log* nil]
     (do ~@body)))