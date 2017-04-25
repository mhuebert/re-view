(ns re-view.routing
  (:require [goog.events :as events]
            [goog.dom :as gdom]
            [clojure.string :as string])
  (:import
    [goog History]
    [goog.history Html5History]
    [goog Uri]))


(def browser? (exists? js/window))
(def history-support? (when browser? (.isSupported Html5History)))

;; from http://www.lispcast.com/mastering-client-side-routing-with-secretary-and-goog-history
;; Replace this method:
;;  https://closure-library.googlecode.com/git-history/docs/local_closure_goog_history_html5history.js.source.html#line237
(set! (.. Html5History -prototype -getUrl_)
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

(defn link?
  "Return true if element is a link"
  [el]
  (some-> el .-tagName (= "A")))

(defn closest
  "Return element or first ancestor of element that matches predicate, like jQuery's .closest()."
  [el pred]
  (if (pred el)
    el
    (gdom/getAncestor el pred)))

(defn click-event-handler
  "Trigger navigation event for click within a link with a valid pushstate href"
  [e]
  (when-let [link (closest (.-target e) link?)]
    (let [location (.-location js/window)
          ;; check to see if we should let the browser handle the link
          ;; (eg. external link, or valid hash reference to an element on the page)
          handle-natively? (or (not= (.-host location) (.-host link))
                               (not= (.-protocol location) (.-protocol link))
                               ;; if only the hash has changed, & element exists on page, allow browser to scroll there
                               (and (.-hash link)
                                    (= (.-pathname location) (.-pathname link))
                                    (not= (.-hash location) (.-hash link))
                                    (.getElementById js/document (subs (.-hash link) 1))))]
      (when-not handle-natively?
        (.preventDefault e)
        (nav! (string/replace (.-href link) (.-origin link) ""))))))

(def intercept-clicks
  ; Intercept local links (handle with router instead of reloading page)
  (memoize (fn intercept
             ([]
              (when browser?
                (intercept js/document)))
             ([element]
              (when browser?
                (events/listen element "click" click-event-handler))))))

(defn parse-route [x]
  {:query  (query x)
   :tokens (tokenize x)
   :path   x})

(defn on-location-change
  ([cb]
   (on-location-change cb false))
  ([cb fire-now?]
   (intercept-clicks)
   (when fire-now? (cb (parse-route (get-route))))
   (events/listen history "navigate" #(cb (parse-route (get-route))))))

