(ns ^:figwheel-always re-view.core
  (:require [clojure.string :as string]
            [clojure.core.match :refer [match]]))

(defn parse-name [n]
  (str (name (ns-name *ns*)) "/" n))

(defn parse-defview-args [args]
  (let [[prelim [args & body]] (split-with (complement vector?) args)]
    (match (vec prelim)
           [] [{} args body]
           [(docstring :guard string?)] [{:docstring docstring} args body]
           [methods] [methods args body]
           [(docstring :guard string?) methods] [`(~'assoc ~methods :docstring ~docstring) args body]
           :else (throw (Error. (str "Invalid arguments passed as view: " (vec prelim))))
           )))

(defn display-name
  ([ns] (str (gensym (display-name ns "view"))))
  ([ns given-name]
   (str (last (string/split (name (ns-name ns)) #"\.")) "/" given-name)))

(defn wrap-body [args body]
  `(~'fn ~args (~'re-view.hiccup/element (do ~@body))))

(defmacro defview
  "Defines view with name, optional docstring, optional lifecycle methods, required arguments, and body."
  ([view-name methods]
   `(def ~view-name
      (~'re-view.core/view* (assoc ~methods
                              :display-name ~(display-name *ns* view-name)))))
  ([view-name a1 & methods]
   (let [[methods args body] (parse-defview-args (cons a1 methods))]
     `(~'re-view.core/defview ~view-name
        (~'assoc ~methods :render ~(wrap-body args body))))))

(defmacro view
  "Returns anonymous view"
  [& args]
  (let [[methods args body] (parse-defview-args args)]
    `(~'re-view.core/view* (assoc ~methods
                             :display-name ~(display-name *ns*)
                             :render ~(wrap-body args body)))))