(ns re-view.perf.util
  (:require #?(:cljs [applied-science.js-interop :as j])
            #?(:clj [net.cgrand.macrovich :as macros]))
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

#?(:cljs
   (defn replace-pattern [s pattern replacement]
     (.replace s (js/RegExp. pattern "g") replacement)))

#?(:cljs
   (defn defined? [x] (not (undefined? x))))

#?(:cljs
   (defn to-obj
     "conversion to javascript object "
     [m keyfn valfn]
     (reduce-kv
       (fn [obj k v] (j/!set obj ^string (keyfn k) (valfn v)))
       #js{} m)))