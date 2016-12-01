(ns re-view.routing
  (:require [goog.events]
            [goog.dom :as gdom]
            [secretary.core :as secretary :refer [*routes* add-route!]]
            [re-view.shared :as shared])
  (:import
    [goog.history Html5History EventType]
    [goog History]))

(defn get-token []
  (if (Html5History.isSupported)
    (str js/window.location.pathname js/window.location.search)
    js/window.location.pathname))

(defn make-history []
  (if (Html5History.isSupported)
    (doto (Html5History.)
      (.setPathPrefix (str js/window.location.protocol
                           "//"
                           js/window.location.host))
      (.setUseFragment false))
    (if (not= "/" js/window.location.pathname)
      (aset js/window "location" (str "/#" (get-token)))
      (History.))))

(def history (make-history))

(doto history
  (.setEnabled true))

(defn nav!
  "Trigger pushstate navigation to token (path)"
  [token]
  (.setToken history token))

(defn link? [el]
  (some-> el .-tagName (= "A")))

(defn closest [el pred]
  (if (pred el)
    el
    (gdom/getAncestor el pred)))

(defn intercept-clicks
  "Intercept clicks to use push-state for local links."
  []
  (goog.events/listen js/document goog.events.EventType.CLICK
                      #(when-let [path (some-> (.-target %) (closest link?) .-attributes .-href .-value)]
                         (when-not (.test #"http.*" path)
                           (.preventDefault %)
                           (nav! path)))))

(defonce _ (intercept-clicks))

(defn compile-routes*
  "Returns a list of compiled Secretary routes, for use with `match-route`"
  [route-pairs]
  (binding [*routes* (atom [])]
    (doseq [[path matched-view] (partition 2 route-pairs)]
      (add-route! path #(if (fn? matched-view)
                          (shared/partial matched-view %)
                          matched-view)))
    @*routes*))

(def compile-routes (memoize compile-routes*))

(defn on-route [cb]
  (cb (get-token))
  (goog.events/listen history EventType.NAVIGATE #(cb (get-token))))

(defn match-route*
  "Matches a token (path) to a route in a list of compiled secretary routes"
  [routes token]
  (binding [*routes* (atom routes)]
    (secretary/dispatch! token)))

(defn router* [token & pairs]
  (let [pairs (if (even? (count pairs)) pairs (concat (drop-last pairs) (list "*" (last pairs))))
        compiled-routes (compile-routes pairs)]
    (match-route* compiled-routes token)))

(defn router [& args]
  ((apply router* args)))

