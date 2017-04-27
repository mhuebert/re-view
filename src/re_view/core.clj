(ns ^:figwheel-always re-view.core
  (:require [clojure.string :as string]
            [clojure.core.match :refer [match]]))

(defn parse-name [n]
  (str (name (ns-name *ns*)) "/" n))

(defn parse-view-args [args]
  (let [[prelim [args & body]] (split-with (complement vector?) args)]
    (match (vec prelim)
           [] [{} args body nil]
           [(docstring :guard string?)] [{} args body docstring]
           [methods] [methods args body]
           [(docstring :guard string?) methods] [methods args body docstring]
           :else (throw (Error. (str "Invalid arguments passed as view: " (vec prelim))))
           )))

(defn display-name
  ([ns] (str (gensym (display-name ns "view"))))
  ([ns given-name]
   (str (last (string/split (name (ns-name ns)) #"\.")) "/" given-name)))

(defn wrap-body [args body]
  `(~'fn ~args (~'re-view-hiccup.core/element (do ~@body))))

(defmacro defview
  "Defines view with name, optional docstring, optional lifecycle methods, required arguments, and body."
  [view-name & methods]
  (let [[methods args body docstring] (parse-view-args methods)]
    `(def ~view-name ~docstring (~'re-view.core/view* ~(assoc methods
                                                         :docstring docstring
                                                         :render (wrap-body args body)
                                                         :display-name (display-name *ns* view-name))))))

(defmacro view
  "Returns anonymous view"
  [& args]
  (let [[methods args body docstring] (parse-view-args args)]
    `(~'re-view.core/view* ~(assoc methods
                              :docstring docstring
                              :display-name (display-name *ns*)
                              :render (wrap-body args body)))))