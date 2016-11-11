(ns re-db.d)

(defmacro branch [db & body]
  `(let [db-before# ~db]
     (binding [~'re-db.d/*db* (~'atom db-before#)
               ~'re-db.core/*mute* true
               ~'re-db.core/*db-log* (~'atom (or (~'some-> ~'re-db.core/*db-log* ~'deref)
                                                 {:db-before db-before#
                                                  :db-after  db-before#
                                                  :reports   {}}))]
       (do ~@body)
       @~'re-db.core/*db-log*)))

(defmacro capture-patterns [& body]
  `(~'re-db.core/capture-patterns ~@body))

(defmacro try-branch [db on-error & body]
  `(let [db-before# ~db]
     (try (branch db-before# ~@body)
          (catch :default e#
            (branch db-before# e# (~on-error e#))))))

(defmacro compute! [& args]
  `(~'re-db.core/compute! ~'re-db.d/*db* ~@args))