(ns re-view.macros
  (:refer-clojure :exclude [defmacro])
  (:require [net.cgrand.macrovich :as m] [cljs.env :as env]
            [cljs.analyzer :as ana]))

(m/deftime

  (defn use-macro [env name]
    ;https://github.com/mfikes/chivorcam/blob/master/src/chivorcam/core.cljc#L40
    (prn :use name ana/*cljs-ns*)
    (swap! env/*compiler* update-in [::ana/namespaces ana/*cljs-ns* :require-macros] assoc ana/*cljs-ns* ana/*cljs-ns*)
    (swap! env/*compiler* update-in [::ana/namespaces ana/*cljs-ns* :use-macros] assoc name ana/*cljs-ns*))

  (clojure.core/defmacro defmacro [name & args]
    (use-macro &env name)
    `(clojure.core/defmacro ~name ~@args))

  )