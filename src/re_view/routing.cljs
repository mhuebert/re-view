(ns re-view.routing
  (:require [goog.events]
            [goog.dom :as gdom]
            [secretary.core :as secretary :refer [*routes* add-route!]]
            [re-view.core :as v])
  (:import
    [goog.history Html5History EventType]
    [goog History]))

(defn log [x]
  (prn x) x)

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
                        (.preventDefault %)
                        (nav! path))))

(defonce _ (intercept-clicks))

(defn compile-routes
  "Returns a list of compiled Secretary routes, for use with `match-route`."
  [route-pairs]
  (binding [*routes* (atom [])]
    (doseq [[path matched-view] (partition 2 route-pairs)]
      (add-route! path #(if (fn? matched-view)
                         (v/partial matched-view %)
                         matched-view)))
    @*routes*))

(defn match-route
  "Matches a token (path) to a route in a list of compiled secretary routes"
  [routes token]
  (binding [*routes* (atom routes)]
    (secretary/dispatch! token)))

(defn router
  "A subscription which takes pairs of path patterns and view functions and returns the matched route.
  A final view may be supplied as a default/catch-all view."
  [& pairs]
  (fn
    [this st-key]
    (let [pairs (if (even? (count pairs)) pairs (concat (drop-last pairs) (list "*" (last pairs))))
          compiled-routes (atom (compile-routes pairs))
          current-match #(match-route @compiled-routes (get-token))
          navigate! #(swap! this assoc st-key (current-match))]
      {:default     current-match
       :subscribe   #(do
                      (reset! compiled-routes (compile-routes pairs))
                      (goog.events/listen history EventType.NAVIGATE navigate!)
                      (navigate!))
       :unsubscribe #(goog.events/unlisten history EventType.NAVIGATE navigate!)})))