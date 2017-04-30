(ns ^:figwheel-always re-view.core
  (:require [clojure.string :as string]))

(def lifecycle-keys #{:constructor
                      :initial-state
                      :will-mount
                      :did-mount
                      :will-receive-props
                      :will-receive-state
                      :should-update
                      :will-update
                      :did-update
                      :will-unmount
                      :render})

(defn- camelCase
  "Return camelCased string, eg. hello-there to helloThere. Does not modify existing case."
  [s]
  (clojure.string/replace s #"-(.)" (fn [[_ match]] (clojure.string/upper-case match))))

(defn- js-obj-with-set!
  "Return a javascript object for m using `(set! (.-someKey the-obj))`, to play well with Closure Compiler.
  Keys are converted to camelCase. Shallow."
  [m]
  (when-let [m (seq m)]
    (let [sym (gensym)
          exprs (map (fn [[k v]]
                       `(~'set! (~(symbol (str ".-" (camelCase (name k)))) ~sym) ~v)) m)]
      `(let [~sym (~'js-obj)]
         ~@exprs
         ~sym))))

(comment
  (defn- js-obj-camelCase
    "Return a javascript object for m with keys as camelCase strings (keys will not be recognized by Closure compiler)."
    [m]
    `(~'js-obj ~@(map (fn [[k v]] [(camelCase (name k)) v]) (seq m)))))

(defn group-methods
  "Groups methods by role in a React component."
  [methods]
  (-> (reduce-kv (fn [m k v]
                   (cond (contains? lifecycle-keys k)
                         (assoc-in m [:lifecycle-methods k] v)
                         (#{:key :display-name :docstring} k) (assoc m k v)
                         (= "static" (namespace k))
                         (assoc-in m [:static-keys (camelCase (name k))] v)
                         :else
                         (assoc-in m [:element-keys k] v))) {} methods)
      (update :element-keys js-obj-with-set!)))

(defn parse-name [n]
  (str (name (ns-name *ns*)) "/" n))

(defn parse-view-args
  "Parse args with optional docstring and method map"
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
  "A meaningful name to identify React components while debugging"
  ([ns] (str (gensym (display-name ns "view"))))
  ([ns given-name]
   (str (last (string/split (name (ns-name ns)) #"\.")) "/" given-name)))

(defn wrap-body [args body]
  `(~'fn ~args (~'re-view-hiccup.core/element (do ~@body))))

(defmacro defview
  "Defines a view function which returns a React element.

   Expects optional docstring and methods map, followed by
    the argslist and body for the render function. Body must
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
  "Returns anonymous view, same args as `defview`."
  [& args]
  (let [[docstring methods args body] (parse-view-args args)]
    `(~'re-view.core/view* ~(-> methods
                                (merge {:docstring    docstring
                                        :display-name (display-name *ns*)
                                        :render       (wrap-body args body)})
                                (group-methods)))))