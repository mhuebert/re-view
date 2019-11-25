(ns re-view.macros
  (:refer-clojure :exclude [defmacro time])
  (:require [clojure.core :as c]
            [cljs.env :as env]
            [cljs.analyzer :as ana]
            [net.cgrand.macrovich :as mv]
            chivorcam.core))

(defn ensure-self-require! [name]
  (when-not (get-in @env/*compiler* [::ana/namespaces ana/*cljs-ns* :require-macros ana/*cljs-ns*])
    (throw (ex-info
             (str "Missing self-require: #?(:cljs (:require-macros " ana/*cljs-ns* "))") {:ns ana/*cljs-ns*
                                                                                          :macro name}))))
(c/defmacro defmacro [name & args]
  (when &env
    (ensure-self-require! name))
  `(~'chivorcam.core/defmacro ~name ~@args))

(c/defmacro defn-deftime [name & args]
  `(~'chivorcam.core/defmacfn ~name ~@args))

(c/defmacro deftime
  "body will be evaluated at macro-definition time"
  [& body]
  (when-not &env
    `(do ~@body)))

(c/defmacro runtime
  "body will be evaluated at runtime"
  [& body]
  (when &env
    `(do ~@body)))

(c/defmacro time [& {:keys [deftime runtime]}]
  (if &env runtime deftime))

(c/defmacro target-lang [& args]
  `(mv/case ~@args))
