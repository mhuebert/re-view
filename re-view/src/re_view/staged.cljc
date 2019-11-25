(ns re-view.staged
  (:require [re-view.inf :as inf]
            [re-view.macros :as m]))

(m/defn-deftime literal-primitive? [x]
  (or (string? x)
      (keyword? x)
      (number? x)))

(m/defn-deftime literal-coll? [x]
  (or (vector? x)
      (map? x)
      (set? x)))

(m/defn-deftime literal? [x]
  (or (literal-primitive? x)
      (literal-coll? x)))

(defn qualified-name [name] (symbol (str *ns*) (str name)))

(m/defmacro defstaged
  "returns a 'staged' function for evaluating arguments at compile-time when possible.

   :literal? [form] - return true if form should be compiled statically.
   :skip-interpret? [inferred-tags, form]  - return true if form is already compiled - will not interpret at runtime
   :macrotime - macro-time function
   :runtime - run-time function

   During macro-expansion, if literal? returns true, the argument is compiled and returned.
   Otherwise, a form is returned which will interpret the argument at runtime.

   :compile may be omitted if the :interpret function can run in both stages.

   (defstaged upper-case
     :literal? string?
     :runtime clojure.string/upper-case)"
  [name doc & args]
  (let [[doc args] (if (string? doc) [doc args] [nil (cons doc args)])
        [opts [f]] (if (map? (first args)) [(first args) (rest args)] [nil args])
        {:keys [literal?
                skip-tags
                compile
                interpret]
         :or {literal? `literal?}} opts
        form (gensym "form")
        impl (symbol (str name "-impl"))
        util-fn (symbol (str name "-fn"))]

    `(do

       (m/runtime
         ;; at runtime we def the implementation fn twice:
         ;; 1. implementation function, doesn't collide with the macro's name
         (def ~impl ~(or interpret f))
         ;; 2. same name as macro (for function application)the same name can be applied as a function
         (def ~name ~@(when doc [doc]) ~impl))

       ;; utility function for use in other macros
       (m/defn-deftime ~util-fn [~form]
         (let [literal?# ~literal?
               skip-tags# ~skip-tags
               compile# ~(or compile f)]
           (cond (literal?# ~form) (compile# ~form)

                 (some-> skip-tags# (inf/superset? (inf/infer-tags ~form)))
                 ~form

                 :else
                 (list '~(qualified-name impl) ~form))))

       ;; standalone macro
       (m/defmacro ~name [~form]
         (binding [inf/*&env* ~'&env]
           (~util-fn ~form))))))