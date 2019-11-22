(ns re-view.perf.util
  (:require #?(:cljs [applied-science.js-interop :as j])
            #?(:clj [net.cgrand.macrovich :as macros])
            [clojure.string :as str])
  #?(:cljs (:require-macros re-view.perf.util
                            [net.cgrand.macrovich :as macros])))

#?(:cljs
   (defn js-memo-1
     "Memoizes function `f` of one argument, using a javascript
      object as cache (so the argument will be stringified)"
     [f]
     (let [cache #js{}]
       (fn [x]
         (j/get cache x
                (let [v (f x)]
                  (j/!set cache x v)
                  v))))))
#?(:clj
   (defmacro cljs-> [x & forms]
     (macros/case
       :cljs `(-> ~x ~@forms)
       :clj x)))


(defn replace-pattern [s pattern replacement]
  #?(:clj (str/replace s (re-pattern pattern) replacement)
     :cljs (.replace s (js/RegExp. pattern "g") replacement)))

#?(:cljs
   (defn defined? [x] (not (undefined? x))))

#?(:cljs
   (defn to-obj
     "conversion to javascript object "
     [m keyfn valfn]
     (reduce-kv
       (fn [obj k v] (j/!set obj ^string (keyfn k) (valfn v)))
       #js{} m)))

#?(:cljs
   (defonce lookup-sentinel #js{})
   :clj
   (def lookup-sentinel 're-view.perf.util/lookup-sentinel))

(defmacro if-found
  "bindings => binding-form lookup-expr

  lookup-expr must be an expression with a missing not-found argument,

   (get-in m [:k :l])
   or
   (m :k)

  If the lookup expression is found, evaluates then with binding-form
  bound to the found value. If not, yields else.

  (This is a performance optimization.)"
  ([binding lookup-expr]
   `(~'re-view.perf.util/if-found ~binding ~lookup-expr nil))
  ([[var-name lookup-expr]
    then
    else]
   (assert (symbol? var-name))
   `(let [~var-name ~(concat lookup-expr (list lookup-sentinel))]
      (if (identical? ~var-name ~lookup-sentinel)
        ~else
        ~then))))

(defmacro when-found
  "Like `if-found` but evaluates all expressions in `then` when found."
  [binding & then]
  `(~'re-view.perf.util/if-found ~binding
     (do ~@then)))