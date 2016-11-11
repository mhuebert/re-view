(ns re-db.core
  (:refer-clojure :exclude [peek]))

(defmacro capture-patterns [& body]
  `(binding [~'re-db.core/*access-log* (~'atom {})]
     (let [value# (do ~@body)
           patterns# (~'deref ~'re-db.core/*access-log*)]
       {:value    value#
        :patterns (~'re-db.core/access-log-patterns patterns#)})))

(defmacro peek [& body]
  `(binding [~'re-db.core/*access-log* nil]
     (do ~@body)))

(defmacro compute! [db [id attr] & body]
  `(let [f# (fn [] (do ~@body))]
     (~'re-db.core/compute-value! ~db [~id ~attr] f#)))