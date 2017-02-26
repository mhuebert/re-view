(ns re-view.routing
  (:require [goog.events]
            [goog.dom :as gdom]
            [clojure.string :as string])
  (:import
    [goog.history Html5History EventType]
    [goog History]))

(defn get-route []
  (if (Html5History.isSupported)
    (str js/window.location.pathname js/window.location.search)
    js/window.location.pathname))

(defn tokenize
  "Split route into tokens, ignoring leading and trailing slashes."
  [route]
  (let [segments (-> route
                     (string/replace #"[#?].*" "")
                     (string/split \/ -1))]
    (cond-> segments
            (= "" (first segments)) (subvec 1)
            (= "" (last segments)) (pop))))

(comment (assert (= (tokenize "/") []))
         (assert (= (tokenize "//") [""]))
         (assert (= (tokenize "///") ["" ""]))
         (assert (= (tokenize "/a/b")
                    (tokenize "a/b/")
                    (tokenize "a/b") ["a" "b"])))

(def browser? (exists? js/window))

(defn make-history []
  (when browser?
    (if (Html5History.isSupported)
      (doto (Html5History.)
        (.setPathPrefix (str js/window.location.protocol
                             "//"
                             js/window.location.host))
        (.setUseFragment false))
      (if (not= "/" js/window.location.pathname)
        (aset js/window "location" (str "/#" (get-route)))
        (History.)))))



(def history
  (make-history))

(when history
  (doto history
    (.setEnabled true)))

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
  "Intercept local links (handle with router instead of reloading page)"
  []
  (when browser?
    (goog.events/listen js/document goog.events.EventType.CLICK
                        #(when-let [href (some-> (.-target %) (closest link?) .-attributes .-href .-value)]
                           (when (or (not (string/starts-with? href "http"))
                                     (string/starts-with? href
                         (.. js/window -location -origin)))
                             (.preventDefault %)
                             (nav! href))))))

(defonce _ (intercept-clicks))

(defn on-route-change [cb]
  (cb (get-route))
  (goog.events/listen history EventType.NAVIGATE #(cb (get-route))))

