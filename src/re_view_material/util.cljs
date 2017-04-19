(ns re-view-material.util
  (:require [clojure.string :as string]))

(defn ensure-str [s]
      (when-not (contains? #{nil ""} s)
                s))

(defn keypress-value [^js/React.SyntheticEvent e]
      (let [target (.-target e)
            raw-value (.-value target)
            new-input (.fromCharCode js/String (.-which e))
            value (str (subs raw-value 0 (.-selectionStart target))
                       new-input
                       (subs raw-value (.-selectionEnd target) (.-length raw-value)))]
           value))

(defn keypress-action [^js/React.SyntheticEvent e]
      (let [str-char (ensure-str (.fromCharCode js/String (.-which e)))
            non-char-keys {13 "enter"
                           8  "backspace"}
            code (.-which e)]
           (string/join "+"
                        (cond-> []
                                (.-altKey e) (conj "alt")
                                (.-ctrlKey e) (conj "ctrl")
                                (.-metaKey e) (conj "meta")
                                (.-shiftKey e) (conj "shift")
                                true (conj (get non-char-keys code str-char))))))