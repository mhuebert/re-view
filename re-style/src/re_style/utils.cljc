(ns re-style.utils
  (:require [clojure.string :as str]))

(defn spacing-fn
  ;; [:pad 0], [:pad 2 4], [:pad 0 2 3 1]
  [root]
  (fn
    ([n]
     [[root :all n]])
    ([v h]
     (cond-> []
             v (into [[root :top v]
                      [root :bottom v]])
             h (into [[root :left h]
                      [root :right h]])))
    ([top right bottom left]
     (cond-> []
             top (conj [root :top top])
             right (conj [root :right right])
             bottom (conj [root :bottom bottom])
             left (conj [root :left left])))))

(def color-rules*
  {:re/pseudos [:hover]
   :re/rule (fn [{:keys [re/path re/value]}]
              {(case (first path)
                 :bg :background-color
                 :border :border-color
                 :text :color) value})})

(defn color-rules [m]
  {[:text
    :bg
    :border] {m color-rules*}})

(defn keyword->dashed-string
  [k]
  (str (when-let [ns (namespace k)]
         (-> ns
             (str/replace ":" "-")
             (str "-")))
       (name k)))

(defn stringify [x]
  (cond (keyword? x) (keyword->dashed-string x)
        (string? x) x
        (number? x) (str x)))

(defn ->map [{:keys [key value]} coll]
  (reduce (fn [m item]
            (assoc m (key item) (value item)))
          {}
          coll))