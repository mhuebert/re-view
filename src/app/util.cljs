(ns app.util
  (:require [goog.net.XhrIo :as xhr]))


(def cache {})

(defn GET [format url cb]
  (if-let [value (get cache url)]
    (cb {:value value})
    (xhr/send url (fn [e]
                    (let [value (case format :text (.getResponseText (.-target e))
                                             :json (.getResponseJson (.-target e)))]
                      (set! cache (assoc cache url value))
                      (cb {:value value}))))))