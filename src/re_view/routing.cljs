(ns re-view.routing
  (:require [goog.events]
            [goog.dom :as gdom]
            [clojure.string :as string])
  (:import
    [goog.history Html5History EventType]
    [goog History]
    [goog Uri]))


(def browser? (exists? js/window))
(def history-support? (when browser? (.isSupported Html5History)))

;; from http://www.lispcast.com/mastering-client-side-routing-with-secretary-and-goog-history
;; Replace this method:
;;  https://closure-library.googlecode.com/git-history/docs/local_closure_goog_history_html5history.js.source.html#line237
(aset js/goog.history.Html5History.prototype "getUrl_"
      (fn [token]
        (this-as this
          (if (.-useFragment_ this)
            (str "#" token)
            (str (.-pathPrefix_ this) token)))))

(defn get-route []
  (if history-support?
    (str js/window.location.pathname js/window.location.search)
    (if (= js/window.location.pathname "/")
      (.substring js/window.location.hash 1)
      (str js/window.location.pathname js/window.location.search))))

(defn tokenize
  "Split route into tokens, ignoring leading and trailing slashes."
  [route]
  (let [segments (-> route
                     (string/replace #"[#?].*" "")
                     (string/split \/ -1))
        segments (cond-> segments
                         (= "" (first segments)) (subvec 1))]
    (cond-> segments
            (= "" (last segments)) (pop))))

(defn query [path]
  (let [data (.getQueryData (Uri. path))]
    (reduce (fn [m k]
              (assoc m k (.get data k))) {} (.getKeys data))))

(comment (assert (= (tokenize "/") []))
         (assert (= (tokenize "//") [""]))
         (assert (= (tokenize "///") ["" ""]))
         (assert (= (tokenize "/a/b")
                    (tokenize "a/b/")
                    (tokenize "a/b") ["a" "b"])))



(defn make-history []
  (when browser?
    (if history-support?
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
                           (let [origin (.. js/window -location -origin)
                                 href (if (= href origin) "/" (string/replace href origin ""))]
                             (when-not (string/starts-with? href "http")
                               (.preventDefault %)
                               (nav! href)))))))

(defonce _ (intercept-clicks))

(defn on-route-change [cb fire-now?]
  (when fire-now? (cb (get-route)))
  (goog.events/listen history EventType.NAVIGATE #(cb (get-route))))

