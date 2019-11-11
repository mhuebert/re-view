(ns re-view.perf.destructure
  (:refer-clojure :exclude [defn fn let maybe-destructured])
  (:require [clojure.core :as core]
            #?(:cljs [applied-science.js-interop :as j])
            #?(:clj [re-view.perf.destructure.impl :as d]))
  #?(:cljs (:require-macros [re-view.perf.destructure :as d])))

#?(:clj
   (do
     (defmacro let
       "`let` with destructuring that supports js arrays"
       [bindings & body]
       (if (empty? bindings)
         `(do ~@body)
         `(core/let ~(d/destructure &env (take 2 bindings))
            (let ~(vec (drop 2 bindings)) ~@body))))

     (core/defn- maybe-destructured
       [[params body]]
       (if (every? (complement d/js-destruct?) params)
         [params body]
         (loop [params params
                new-params (with-meta [] (meta params))
                lets []]
           (if params
             (if-not (d/js-destruct? (first params))
               (recur (next params) (conj new-params (first params)) lets)
               (core/let [gparam (gensym "p__")]
                 (recur (next params) (conj new-params gparam)
                        (conj lets (first params) gparam))))
             [new-params
              `[(let ~lets
                  ~@body)]]))))

     (defmacro fn [& args]
       (cons `core/fn (d/spec-reform ::d/function-args args #(d/update-argv+body maybe-destructured %))))

     (defmacro defn [& args]
       (cons `core/defn (d/spec-reform ::d/function-args args #(d/update-argv+body maybe-destructured %))))))

#?(:cljs
   (comment
     ;; records store their fields, so we can recognize them.
     ;; only defined fields use direct lookup.
     (do
       (defrecord Hello [record-field])
       (macroexpand-1 '(d/let [{:keys [record-field
                                       other-field]} ^re-view.perf.destructure/Hello {}])))


     (macroexpand '(d/let [{:keys [a]} obj] a))

     (macroexpand-1 '(d/fn [x & xs] {:pre []} x))
     (macroexpand-1 '(d/fn ([y] y) ([x & xs] x)))

     (macroexpand '(d/fn [^js {:keys [y]}] y))

     (macroexpand '(d/defn my-fn [^js {:keys [y]}] y))


     (= 10 ((d/fn [^js {:keys [aaaaa]}] aaaaa)
            #js{aaaaa 10}))

     (= nil ((d/fn [{:keys [aaaaa]}] aaaaa)
             #js{:aaaaa 10}))

     (= 10 ((d/fn [^js [_ a]] a)
            #js[0 10]))
     (= 10 ((d/fn [[_ a]] a)
            #js[0 10]))

     (d/let [{:keys [aaaaa]} #js{aaaaa 10}]
       (= nil aaaaa))

     (d/let [^js {:keys [aaaaa]} #js{aaaaa 10}]
       (= 10 aaaaa))

     (d/let [^js {:syms [aaaaa]} (j/obj aaaaa 10)]
       (= 10 aaaaa))




     (macroexpand-1 '(defn hello "docstring" {:pre [(constantly true)]} ([y] y) ([x & xs] x)))
     ))