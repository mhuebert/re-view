(ns re-view.hiccup
  (:require [clojure.string :as string]
            [cljsjs.react]))

(defonce handlers (reduce (fn [m k]
                            (assoc m k (aget (.-DOM js/React) k))) {} (.keys js/Object (.-DOM js/React))))

(defn register-handlers! [m]
  (set! handlers (merge handlers m)))

(def parse-key
  (memoize (fn [x]
             (re-find #":([^#.]*)(?:#([^.]+))?(.*)?" (str x)))))

(defn js-conj [c x]
  (doto c
    (.push x)))

(defn reduce-concat [add f init coll]
  (reduce (fn my-f [c x]
            (cond (vector? x)
                  (add c (apply f x))
                  (seq? x)
                  (reduce-concat add f c x)
                  :else (add c x))) init coll))

(defn parse-args [children]
  (let [props (first children)]
    (if (map? props)
      [props (rest children)]
      [{} children])))

(declare element)

(defn render-hiccup-node
  [view & args]
  {:pre [(keyword? view)]}
  (let [[props children] (parse-args args)
        children (reduce-concat js-conj render-hiccup-node #js [] children)]
    (let [[_ k id classes] (parse-key view)
          props (cond-> (clj->js props)
                        id (doto (aset "id" id))
                        classes (doto (aset "className" (str (get props :className) (string/replace classes "." " ")))))
          view (get handlers (if (= k "") "div" k))]
      (apply view (doto children
                    (.unshift props))))))


(defn element [form]
  (if (vector? form)
    (apply render-hiccup-node form)
    form))