(ns ^:figwheel-always re-view.core
  (:require [clojure.string :as string]))

(defn parse-name [n]
  (str (name (ns-name *ns*)) "/" n))

(defmacro defview
  ([view-name methods]
   `(def ~view-name
      (~'re-view.core/view (assoc ~methods
                             :display-name ~(str (last (string/split (name (ns-name *ns*)) #"\.")) "/" view-name)))))
  ([view-name a1 a2 & body]
   (let [[methods args body] (if (vector? a1)
                               [{} a1 (cons a2 body)]
                               [a1 a2 body])]
     `(~'re-view.core/defview ~view-name
        (~'assoc ~methods :render (~'fn ~args (~'re-view.hiccup/element (do ~@body))))))))
