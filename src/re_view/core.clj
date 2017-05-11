(ns ^:figwheel-always re-view.core
  (:require [clojure.string :as string]
            [re-view.util :refer [camelCase]]))

(defn- js-obj-with-set!
  "Convert a Clojure map to javascript object using `set!`, to play well with Closure Compiler.
  Keys are converted to camelCase. Shallow."
  [m]
  (when-let [m (seq m)]
    (let [sym (gensym)
          exprs (map (fn [[k v]]
                       `(~'set! (~(symbol (str ".-" (camelCase (name k)))) ~sym) ~v)) m)]
      `(let [~sym (~'js-obj)]
         ~@exprs
         ~sym))))

(defn group-methods
  "Groups methods by role in a React component."
  [methods]
  (-> (reduce-kv (fn [m k v]
                   (cond
                     ;; lifecycle methods
                     (contains? #{:constructor
                                  :initial-state
                                  :will-mount
                                  :did-mount
                                  :will-receive-props
                                  :will-receive-state
                                  :should-update
                                  :will-update
                                  :did-update
                                  :will-unmount
                                  :render} k)
                     (assoc-in m [:lifecycle-methods k] v)

                     ;; class keys
                     (or (= "static" (namespace k))
                         (#{:key :display-name :docstring} k))
                     (assoc-in m [:class-keys k] v)
                     :else
                     ;; other keys are defined on the element
                     (assoc-in m [:element-keys k] v))) {} methods)
      (update :element-keys js-obj-with-set!)))

(defn parse-view-args
  "Parse args for optional docstring and method map"
  [args]
  (let [out [(if (string? (first args)) (first args) nil)]
        args (cond-> args (string? (first args)) (next))
        out (conj out (if (map? (first args)) (first args) nil))
        args (cond-> args (map? (first args)) (next))]
    (conj out (first args) (next args))))

(comment
  (assert (= (parse-view-args '("a" {:b 1} [c] 1 2))
             '["a" {:b 1} [c] (1 2)]))

  (assert (= (parse-view-args '({} [] 1 2))
             '[nil {} [] (1 2)]))

  (assert (= (parse-view-args '("a" [] 1 2))
             '["a" nil [] (1 2)]))

  (assert (= (parse-view-args '([] 1 2))
             '[nil nil [] (1 2)]))

  (assert (= (parse-view-args '([]))
             '[nil nil [] nil])))

(defn display-name
  "Generate a meaningful name to identify React components while debugging"
  ([ns] (str (gensym (display-name ns "view"))))
  ([ns given-name]
   (str (last (string/split (name (ns-name ns)) #"\.")) "/" given-name)))

(defn wrap-body
  "Wrap body in anonymous function form."
  [args body]
  `(~'fn ~args (~'re-view.hiccup/element (do ~@body))))

(defmacro defview
  "Define a view function.

   Expects optional docstring and methods map, followed by
    the argslist and body for the render function, which should
    return a Hiccup vector or React element."
  [view-name & args]
  (let [[docstring methods args body] (parse-view-args args)
        methods (-> methods
                    (merge {:docstring    docstring
                            :display-name (display-name *ns* view-name)
                            :render       (wrap-body args body)})
                    (group-methods))]
    `(def ~view-name ~docstring (~'re-view.core/view* ~methods))))

(defmacro view
  "Returns anonymous view, given the same args as `defview`."
  [& args]
  (let [[docstring methods args body] (parse-view-args args)
        methods (-> methods
                    (merge {:docstring    docstring
                            :display-name (display-name *ns*)
                            :render       (wrap-body args body)})
                    (group-methods))]
    `(~'re-view.core/view* ~methods)))

(defmacro defpartial
  "Returns partially applied view with given name & optional docstring."
  ([name base-view props]
   `(~'re-view.core/defpartial ~name nil ~base-view ~props))
  ([name docstring base-view props]
   `(~'re-view.core/partial ~base-view ~(cond-> {:name (str name)}
                                                docstring (assoc :docstring docstring)) ~props)))