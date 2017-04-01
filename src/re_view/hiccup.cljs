(ns re-view.hiccup
  (:require [clojure.string :as string]))

(set! *warn-on-infer* true)

(defn parse-key
  "Parses a hiccup key like :div#id.class1.class2 to return the base name, id, and classes.
   If base-name is ommitted, defaults to 'div'. Class names are padded with spaces."
  [x]
  (-> (re-find #":([^#.]*)(?:#([^.]+))?(.*)?" (str x))
      (update 1 #(if (= "" %) "div" %))
      (update 3 #(when %
                   (str (string/replace % "." " ") " ")))))

;; parse-key is an ideal target for memoization, because composite keyword forms are
;; frequently reused (eg. in lists) and would/should not be generated dynamically.
(def parse-key-memoized (memoize parse-key))

(defn js-conj
  "Adds x to end of a javascript array, returning the array."
  [^js/Array c x]
  (doto c
    (.push x)))

(defn reduce-concat
  "Recursively parse nested vectors. Like recursive `mapcat` but outputs a vector."
  [add f init coll]
  (reduce (fn my-f [^js/Array c x]
            (cond (vector? x)
                  (add c (f x))
                  (seq? x)
                  (reduce-concat add f c x)
                  :else (add c x))) init coll))

(defn parse-args
  "Return props and children for a hiccup form. If the second element is not a map, supplies an empty map as props."
  [form]
  (let [len (count form)]
    (cond (= len 1) [{} []]
          (map? (form 1)) [(form 1) (if (> len 2) (subvec form 2 len) [])]
          :else [{} (subvec form 1 len)])))

(defn js-props
  "Apply id & classes to js-obj props"
  [id classes {:keys [className] :as props}]
  (if props
    (cond-> (if props (clj->js props) #js {})
            id (doto (aset "id" id))
            classes (doto (aset "className" (str classes className))))))

(defn clj-props
  "Apply id & classes to clj-map props"
  [id classes props]
  (cond-> props
          id (assoc :id id)
          classes (update :className str classes)))

(defonce handlers {})

(defn register-handlers!
  ;; currently disabled
  "Register custom views for hiccup keyword base-names.

  Eg. register:  {:ui/Text (.. some view function)}
      usage:     [:ui/Text \"My Text Element\"]"
  [m]
  (set! handlers (merge handlers (reduce-kv (fn [m k v]
                                              ;; names are stored as strings, without preceding :'s
                                              (assoc m (subs (str k) 1) v)) {} m))))

(defn render-hiccup-node
  [form]
  (try
    (let [[_ k id classes] (parse-key-memoized (form 0))
          [props children] (parse-args form)
          args (reduce-concat conj render-hiccup-node [k (js-props id classes props)] children)]
      (apply (.-createElement js/React) args)
      #_(if-let [handler (get handlers k)]
          (apply (get handlers k) (clj-props id classes props) children)
          (apply (.-createElement js/React) k (js-props id classes props) children)))
    (catch js/Error e
      (when (exists? js/window)
        (println :hiccup-render-fail form)))))


(defn element
  "Convert vector hiccup forms to React elements. Non-vector forms/children are passed through untouched."
  [form]
  (if (vector? form)
    (render-hiccup-node form)
    form))