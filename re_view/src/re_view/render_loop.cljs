(ns re-view.render-loop
  (:require [goog.object :as gobj]))

(set! *warn-on-infer* true)
(def ^:dynamic *immediate-state-update* false)

(def ^:private count-fps? false)
(def ^:private last-fps-time 1)
(def frame-rate 0)
(def frame-count 0)

(defn count-fps!
  [enable?]
  (set! count-fps? enable?))

(defonce _raf-polyfill
         (when (js* "typeof window !== 'undefined'")
           (if-not (aget js/window "requestAnimationFrame")
             (gobj/set js/window "requestAnimationFrame"
                   (or
                     (aget js/window "webkitRequestAnimationFrame")
                     (aget js/window "mozRequestAnimationFrame")
                     (aget js/window "oRequestAnimationFrame")
                     (aget js/window "msRequestAnimationFrame")
                     (fn [cb]
                       (js/setTimeout cb (/ 1000 60))))))))

(def to-render (volatile! #{}))
(def to-run (volatile! []))

(declare request-render)

(defn schedule! [f]
  (vswap! to-run conj f)
  (request-render))

(defn force-update-caught [this]
  (when-not (true? (aget this "unmounted"))
    (try (.forceUpdate this)
         (catch js/Error e
           (if-let [catch-fn (aget this "catch")]
             (catch-fn e)
             (do (.debug js/console "No :catch method in component: " this)
                 (.error js/console e)))))))

(defn force-update! [^js/React.Component this]
  (vswap! to-render disj this)
  (force-update-caught this))

(defn force-update [this]
  (if (true? *immediate-state-update*)
    (force-update! this)
    (do
      (vswap! to-render conj this)
      (request-render))))

(defn flush!
  []
  (when-not ^:boolean (empty? @to-render)
    (let [components @to-render]
      (vreset! to-render #{})
      (doseq [c components]
        (force-update-caught c))))

  (when-not ^:boolean (empty? @to-run)
    (let [fns @to-run]
      (vreset! to-run [])
      (doseq [f fns] (f)))))

(defn render-loop
  [frame-ms]
  (set! frame-count (inc frame-count))
  (when ^:boolean (and (true? count-fps?) (identical? 0 (mod frame-count 29)))
    (set! frame-rate (* 1000 (/ 30 (- frame-ms last-fps-time))))
    (set! last-fps-time frame-ms))
  (flush!))

(defn request-render []
  (js/requestAnimationFrame render-loop))


(defn apply-sync!
  "Wraps function `f` to flush the render loop before returning."
  [f]
  (fn [& args]
    (let [result (apply f args)]
      (flush!)
      result)))