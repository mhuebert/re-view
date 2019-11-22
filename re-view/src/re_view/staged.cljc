(ns re-view.staged
  (:require #?(:clj [net.cgrand.macrovich :as macros])
            [re-view.perf.util :as perf]
            [re-view.inf :as inf])
  #?(:cljs (:require-macros [re-view.staged :refer [defstaged]]
                            [net.cgrand.macrovich :as macros])))

(defn unevaluated? [x] (or (symbol? x) (list? x)))
(def literal? (complement unevaluated?))

(macros/deftime

  (defn qualified-name [name] (symbol (str *ns*) (str name)))

  (defmacro defstaged
    "returns a 'staged' function for evaluating arguments at compile-time when possible.

     :literal? [form] - return true if form should be compiled statically.
     :skip-interpret? [inferred-tags, form]  - return true if form is already compiled - will not interpret at runtime
     :compile - macro-time function
     :interpret - run-time function

     During macro-expansion, if literal? returns true, the argument is compiled and returned.
     Otherwise, a form is returned which will interpret the argument at runtime.

     :compile may be omitted if the :interpret function can run in both stages.

     (defstaged upper-case
       :literal? string?
       :interpret clojure.string/upper-case)"
    [name doc & args]
    (let [[doc args] (if (string? doc) [doc args] [nil (cons doc args)])
          [opts args] (if (map? (first args)) [(first args) (rest args)] [nil args])
          [f] args
          {:keys [literal?
                  skip-interpret?
                  compile
                  interpret]
           :or {literal? `literal?}} opts
          macro-time? (boolean #?(:clj true :cljs (re-matches #".*\$macros" (clojure.core/name (ns-name *ns*)))))
          interpret (or interpret f)
          compile (or compile f)
          form (gensym "form")
          impl-name (symbol (str name "-impl"))]
      (if macro-time?
        `(let [literal?# ~literal?
               skip-interpret?# ~skip-interpret?]
           (def ~impl-name ~(or compile interpret))
           (defn ~name [~form]
             (cond (literal?# ~form) (~impl-name ~form)
                   (and skip-interpret?# (skip-interpret?# (inf/infer-tags ~form) ~form)) ~form
                   ;; here: if called from within a macro, we want the unevaluated form
                   :else (if inf/*&env*
                           (quote (list (quote ~(qualified-name name)) ~form))
                           (list (quote ~(qualified-name name)) ~form)))))
        `(def ~name ~@(when doc [doc]) ~interpret)))))

(comment

  (macroexpand '(defstaged upper-case clojure.string/upper-case))

  (defstaged upper-case
    clojure.string/upper-case)
  (binding [inf/*&env* nil]
    (macroexpand '(upper-case 'a)))
  (defmacro compile-stuff [x]
    (upper-case x))

  (let [a "a"]
    (compile-stuff a))

  [(assert (= (macroexpand '(compile-stuff a))
              `(upper-case ~'a))
           "macroexpand w/ symbol returns unevaluated form")
   (assert (= (macroexpand '(compile-stuff "a")) "A")
           "macroexpand w/ literal returns evaluated form")
   (assert (= (upper-case "a") "A")
           "calling staged-fn in JVM with literal returns evaluated form")
   (assert (= (upper-case 'a) `(upper-case ~'a))
           "calling staged-fn in JVM with symbol returns unevaluated interpretation form")
   (assert (= (compile-stuff "a") "A")
           "calling macro in JVM returns evaluated form")])