(ns re-db.d)

(def *db-log* 're-db.core/*db-log*)
(def *db* 're-db.d/*db*)
(def *mute* 're-db.core/*mute*)

(defmacro branch [db & body]
  `(let [db-before# ~db]
     (binding [~*db* (~'atom db-before#)
               ~*mute* true
               ~*db-log* (~'atom (or (~'some-> ~*db-log* ~'deref)
                                     {:db-before db-before#
                                      :db-after  db-before#
                                      :reports   {}}))]
       (do ~@body)
       @~*db-log*)))

(defmacro try-branch [db on-error & body]
  `(let [db-before# ~db]
     (try (branch db-before# ~@body)
          (catch :default e#
            (branch db-before# e# (~on-error e#))))))