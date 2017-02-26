(ns ^:figwheel-always re-view.core
  (:require [clojure.string :as string]))

(defn parse-name [n]
  (str (name (ns-name *ns*)) "/" n))

(defmacro defview
  ([view-name methods]
   `(def ~view-name
      (~'re-view.core/view ~(assoc methods
                              :display-name (str (name (ns-name *ns*)) "/" view-name)))))
  ([view-name args render]
   `(~'re-view.core/defview ~view-name
      {}
      ~args ~render))
  ([view-name methods args render]
   `(~'re-view.core/defview ~view-name
      ~(assoc methods :render `(~'fn ~args (~'re-view.hiccup/element ~render))))))
