(ns re-view-hiccup.core
  (:require [re-view-hiccup.hiccup :as hiccup]))

(enable-console-print!)
(set! *warn-on-infer* true)

;; patch IPrintWithWriter to print javascript symbols without throwing errors
(when (exists? js/Symbol)
  (extend-protocol IPrintWithWriter
    js/Symbol
    (-pr-writer [sym writer _]
      (-write writer (str "\"" (.toString sym) "\"")))))

(defprotocol IElement
  (to-element [this] "Returns a React element representing `this`"))

(defprotocol IHiccup
  (to-hiccup [this] "Returns a hiccup form representing `this`"))

(defn -to-element [form]
  (cond (vector? form)
        (try
          (let [[_ tag id classes] (hiccup/parse-key-memoized (form 0))
                [props children] (hiccup/parse-args form)
                args (hiccup/reduce-flatten-seqs -to-element [tag (hiccup/props->js tag id classes props)] children)]
            (apply hiccup/*create-element* args))

          (catch js/Error e
            (println "Error in render-hiccup-node:")
            (println form)
            (.error js/console e)))


        (satisfies? IElement form)
        (to-element form)

        (satisfies? IHiccup form)
        (-to-element (to-hiccup form))

        :else form))

(defn element
  "Converts Hiccup form into a React element. If a non-vector form
   is supplied, it is returned untouched. Attribute and style keys
   are converted from `dashed-names` to `camelCase` as spec'd by React.

   - optional -
   :wrap-props (fn) is applied to all props maps during parsing.
   :create-element (fn) overrides React.createElement."
  ([form]
   (-to-element form))
  ([form {:keys [wrap-props
                 create-element]}]
   (binding [hiccup/*wrap-props* wrap-props
             hiccup/*create-element* (or hiccup/*create-element* create-element)]
     (-to-element form))))