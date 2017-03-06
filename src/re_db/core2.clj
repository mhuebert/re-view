(ns re-db.core2
  (:refer-clojure :exclude [peek]))

(defmacro capture-patterns [& body]
  `(binding [~'re-db.core2/*access-log* ~'re-db.core2/blank-access-log]
     (let [value# (do ~@body)
           patterns# ~'re-db.core2/*access-log*]
       {:value    value#
        :patterns (~'re-db.core2/access-log-patterns patterns#)})))

(defmacro peek [& body]
  `(binding [~'re-db.core2/*access-log* nil]
     (do ~@body)))

(defmacro get-in*
  "Compile to threaded get expressions, small performance boost"
  ([m ks]
   (if-not (vector? ks)
     `(clojure.core/get-in ~m ~ks)
     `(-> ~m
          ~@(for [k ks]
              (list 'clojure.core/get k)))))
  ([m ks not-found]
   (if-not (vector? ks)
     `(clojure.core/get-in ~m ~ks ~not-found)
     `(-> ~m
          ~@(for [k (drop-last ks)]
              (list 'clojure.core/get k))
          (clojure.core/get ~(last ks) ~not-found)))))