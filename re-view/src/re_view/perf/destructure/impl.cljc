(ns re-view.perf.destructure.impl
  (:refer-clojure :exclude [destructure])
  (:require [clojure.string :as str]
            [clojure.core :as core]
            [clojure.spec.alpha :as s]
            #?@(:clj [[net.cgrand.macrovich :as macros]
                      [re-view.perf.type-inference :as inf]]))
  #?(:cljs (:require-macros [net.cgrand.macrovich :as macros])))

(macros/deftime

  (defn- cljs-target?
    "Take the &env from a macro, and tell whether we are expanding into cljs."
    [env]
    (boolean (:ns env)))

  (defn- dequote [x]
    (if (and (list? x) (= 'quote (first x)))
      (second x)
      x))

  (defn- dot-sym? [x]
    (let [x (dequote x)]
      (and (symbol? x) (str/starts-with? (name x) ".-"))))

  (defn- dot-name [x]
    (str/replace-first (name (dequote x)) #"^\.\-" ""))

  (defn- dot-sym [s]
    (symbol (str ".-" (dot-name s))))

  (defn- array-access? [x]
    (and (symbol? x)
         (contains? '#{array js/Array js} x)))

  (defn- obj-access [x]
    (and (symbol? x)
         (or (contains? '#{Object js} x)
             (= "js" (namespace x)))))

  (defn- get-meta [x k]
    (when #?(:cljs (satisfies? IMeta x) :clj true)
      (get (meta x) k)))

  (defn js-tag? [tag] (= 'js tag))

  (defn js-destruct? [s]
    (and (not (symbol? s))
         (js-tag? (get-meta s :tag))))

  (declare process-binding)

  (defn- process-vec [out bind-as value]
    (let [js? (js-destruct? bind-as)
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
              (= firstb '&) (recur (process-binding ret (second bs) gseq)
                                   n
                                   (nnext bs)
                                   true)
              (= firstb :as) (process-binding ret (second bs) gvec)
              :else (if seen-rest?
                      (throw #?(:clj  (new Exception "Unsupported binding form, only :as can follow & parameter")
                                :cljs (new js/Error "Unsupported binding form, only :as can follow & parameter")))
                      (recur (process-binding (if has-rest
                                                (conj ret
                                                      gfirst `(first ~gseq)
                                                      gseq `(next ~gseq))
                                                ret)
                                              firstb
                                              (if has-rest
                                                gfirst
                                                (if js?
                                                  (list 'cljs.core/aget value n)
                                                  (list `nth value n nil))))
                             (inc n)
                             (next bs)
                             seen-rest?))))
          ret))))

  (defn- process-map [out {:as      bind-as
                           defaults :or
                           alias    :as} value]
    (let [tag (get-meta bind-as :tag)
          js? (js-tag? tag)
          gmap (gensym "map__")]
      (loop [ret (-> out
                     (conj gmap value
                           gmap `(if ~(macros/case :clj `(seq? ~gmap)
                                                   :cljs `(implements? ISeq ~gmap))
                                   (apply hash-map ~gmap) ~gmap))
                     (cond-> alias (conj alias gmap)))
             bes (let [transforms
                       (reduce
                         (fn [transforms mk]
                           (if (keyword? mk)
                             (let [mkns (namespace mk)
                                   mkn (name mk)]
                               (if-some [transform (case mkn
                                                     "keys" #(keyword (or mkns (namespace %)) (name %))
                                                     "syms" #(list `quote (symbol (or mkns (namespace %)) (name %)))
                                                     "strs" str
                                                     nil)]
                                 (assoc transforms mk transform)
                                 transforms))
                             transforms))
                         {}
                         (keys bind-as))]
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
                local (if #?(:clj  (core/instance? clojure.lang.Named bb)
                             :cljs (cljs.core/implements? INamed bb))
                        (with-meta (symbol nil (name bb)) (meta bb))
                        bb)
                renamable-k? (let [k (dequote bk)]
                               (or (and js? (symbol? k))
                                   (some-> tag (inf/record-field? (symbol (name k))))))
                bk (cond-> bk renamable-k? (dot-sym))
                getf (if (or js? (dot-sym? bk))
                       'applied-science.js-interop/get
                       `get)
                bv (if (contains? defaults local)
                     (list getf gmap bk (defaults local))
                     (list getf gmap bk))]
            (recur (if (ident? bb)
                     (-> ret (conj local bv))
                     (process-binding ret bb bv))
                   (next bes)))
          ret))))

  (defn- process-binding
    ([out pair]
     (process-binding out (first pair) (second pair)))
    ([out binding-form value]
     (cond
       (symbol? binding-form) (conj out binding-form value)
       (vector? binding-form) (process-vec out binding-form value)
       (map? binding-form) (process-map out binding-form value)
       :else (throw #?(:clj  (new Exception (str "Unsupported binding form: " binding-form))
                       :cljs (new js/Error (str "Unsupported binding form: " binding-form)))))))

  (defn destructure
    "Destructure with direct array and object access on records, types, and ^js hinted values.

    Hints may be placed on the binding or value:
    (let [^js {:keys [a]} obj] ...)
          ^
    (let [{:keys [a]} ^js obj] ...)
                      ^

    Keywords compile to static keys, symbols to renamable keys,
    and array access to `aget`."
    [env bindings]
    (if (cljs-target? env)
      (reduce process-binding [] (partition 2 bindings))
      (core/destructure bindings)))

  (s/def ::argv+body
    (s/cat :params (s/and
                     vector?
                     (s/conformer identity vec)
                     (s/cat :params (s/* any?)))
           :body (s/alt :prepost+body (s/cat :prepost map?
                                             :body (s/+ any?))
                        :body (s/* any?))))

  (s/def ::function-args
    (s/cat :fn-name (s/? simple-symbol?)
           :docstring (s/? string?)
           :meta (s/? map?)
           :fn-tail (s/alt :arity-1 ::argv+body
                           :arity-n (s/cat :bodies (s/+ (s/spec ::argv+body))
                                           :attr-map (s/? map?)))))

  (core/defn spec-reform [spec args update-conf]
    (->> (s/conform spec args)
         (update-conf)
         (s/unform spec)))

  (core/defn update-argv+body [update-fn {[arity] :fn-tail :as conf}]
    (let [update-pair
          (fn [conf]
            (let [body-path (cond-> [:body 1]
                                    (= :prepost+body (first (:body conf))) (conj :body))
                  [params body] (update-fn [(get-in conf [:params :params])
                                            (get-in conf body-path)])]
              (-> conf
                  (assoc-in [:params :params] params)
                  (assoc-in body-path body))))]
      (case arity
        :arity-1 (update-in conf [:fn-tail 1] update-pair)
        :arity-n (update-in conf [:fn-tail 1 :bodies] #(mapv update-pair %)))))

  (comment
    (defrecord Hello [a b])
    (destructure '[^js [n1 n2 n3] [:my-thing]])

    (destructure '[{:keys [n1 n2 n3]                        ; static keys
                    :syms [a1 a2 a3]                        ; renamable keys
                    a     '.-a
                    b     :b} {}]))

  )
