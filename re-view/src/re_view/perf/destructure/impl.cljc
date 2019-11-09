(ns re-view.perf.destructure.impl
  (:refer-clojure :exclude [destructure])
  (:require [clojure.string :as str]
            #?(:clj [net.cgrand.macrovich :as macros]))
  #?(:cljs (:require-macros [net.cgrand.macrovich :as macros])))

(defn dequote [x]
  (if (and (list? x) (= 'quote (first x)))
    (second x)
    x))

(defn dot-sym? [x]
  (let [x (dequote x)]
    (and (symbol? x) (str/starts-with? (name x) ".-"))))

(defn dot-name [x]
  (str/replace-first (name (dequote x)) #"^\.\-" ""))

(defn dot-sym [s]
  (symbol (str ".-" (dot-name s))))

(defn array-access? [x]
  (macros/case
    :clj false
    :cljs
    (and (symbol? x)
         (contains? '#{array js/Array js} x))))

(defn obj-access? [x]
  (macros/case
    :clj false
    :cljs
    (and (symbol? x)
         (or (contains? '#{Object js} x)
             (= "js" (namespace x))))))

(defn get-meta [x k]
  (when #?(:cljs (satisfies? IMeta x) :clj true)
    (get (meta x) k)))

(declare process-pair)

(defn bind-nth [tag value n]
  (if (array-access? tag)
    (list 'cljs.core/aget value n)
    (list `nth value n nil)))

(defn process-vec [out bind-as value]
  (let [tag (or (get-meta value :tag)
                (get-meta bind-as :tag))
        gvec (gensym "vec__")
        gseq (gensym "seq__")
        gfirst (gensym "first__")
        has-rest (some #{'&} bind-as)]
    (loop [ret (let [ret (conj out gvec value)]
                 (if has-rest
                   (conj ret gseq (list `seq gvec))
                   ret))
           n 0
           bs bind-as
           seen-rest? false]
      (if (seq bs)
        (let [firstb (first bs)]
          (cond
            (= firstb '&) (recur (process-pair ret (second bs) gseq)
                                 n
                                 (nnext bs)
                                 true)
            (= firstb :as) (process-pair ret (second bs) gvec)
            :else (if seen-rest?
                    (throw (new Exception "Unsupported binding form, only :as can follow & parameter"))
                    (recur (process-pair (if has-rest
                                           (conj ret
                                                 gfirst `(first ~gseq)
                                                 gseq `(next ~gseq))
                                           ret)
                                         firstb
                                         (if has-rest
                                           gfirst
                                           (bind-nth tag gvec n)))
                           (inc n)
                           (next bs)
                           seen-rest?))))
        ret))))

(defn process-map [out {:as      bind-as
                        defaults :or
                        alias    :as} value]
  (let [tag (or (get-meta value :tag)
                (get-meta bind-as :tag))
        js-tag? (atom (obj-access? tag))
        gmap (gensym "map__")
        gmapseq (with-meta gmap {:tag 'clojure.lang.ISeq})]
    (loop [ret (-> out
                   (conj gmap value
                         gmap `(if (seq? ~gmap) (clojure.lang.PersistentHashMap/create (seq ~gmapseq)) ~gmap))
                   (cond-> alias (conj alias gmap)))
           bes (let [transforms
                     (reduce-kv
                       (fn [transforms mk form]
                         (when (obj-access? (get-meta form :tag))
                           (reset! js-tag? true))
                         (if (keyword? mk)
                           (let [mkns (namespace mk)
                                 mkn (name mk)
                                 transform (cond (= mkn "keys") #(keyword (or mkns (namespace %)) (name %))
                                                 (= mkn "syms") #(if @js-tag? (symbol (name %))
                                                                              (list `quote (symbol (or mkns (namespace %)) (name %))))
                                                 (= mkn "strs") str)]
                             (cond-> transforms
                                     transform (assoc mk transform)))
                           transforms))
                       {}
                       bind-as)]
                 (reduce
                   (fn [bes entry]
                     (reduce #(assoc %1 %2 ((val entry) %2))
                             (dissoc bes (key entry))
                             ((key entry) bes)))
                   (-> bind-as (dissoc :as :or))
                   transforms))]
      (if (seq bes)
        (let [bb (key (first bes))
              bk (val (first bes))
              local (if (instance? clojure.lang.Named bb) (with-meta (symbol nil (name bb)) (meta bb)) bb)
              js? (or @js-tag?
                      (dot-sym? bk))
              get-sym (if js? 'applied-science.js-interop/get `get)
              format-key (if js? #(cond-> % (symbol? (dequote %)) (dot-sym)) identity)
              bv (if (contains? defaults local)
                   (list get-sym gmap (format-key bk) (defaults local))
                   (list get-sym gmap (format-key bk)))]
          (recur (if (ident? bb)
                   (-> ret (conj local bv))
                   (process-pair ret bb bv))
                 (next bes)))
        ret))))

(defn process-pair
  ([out pair]
   (process-pair out (first pair) (second pair)))
  ([out binding-form value]
   (cond
     (symbol? binding-form) (conj out binding-form value)
     (vector? binding-form) (process-vec out binding-form value)
     (map? binding-form) (process-map out binding-form value)
     :else (throw (new Exception (str "Unsupported binding form: " binding-form))))))

(defn destructure
  "Destructure with direct array and object access on ^js hinted values.

  Hints may be placed on the binding or value:
  (let [^js {:keys [a]} obj] ...)
        ^
  (let [{:keys [a]} ^js obj] ...)
                    ^

  Keywords compile to static keys, symbols to renamable keys,
  and array access to `aget`."
  [bindings]
  (reduce process-pair [] (partition 2 bindings)))

(comment
  (destructure '[^js [n1 n2 n3] [:my-thing]])

  (destructure '[{:keys [n1 n2 n3]                          ; static keys
                  :syms [a1 a2 a3]                          ; renamable keys
                  a     '.-a
                  b     :b} {}]))

