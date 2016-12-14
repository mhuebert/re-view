(ns re-view.render-loop)

(def ^:private count-fps? false)
(def ^:private last-fps-time 1)
(def frame-rate 0)
(def frame-count 0)

(defn count-fps!
      [enable?]
      (set! count-fps? enable?))

(defonce _raf-polyfill
         (if-not (aget js/window "requestAnimationFrame")
                 (aset js/window "requestAnimationFrame"
                       (or
                         (aget js/window "webkitRequestAnimationFrame")
                         (aget js/window "mozRequestAnimationFrame")
                         (aget js/window "oRequestAnimationFrame")
                         (aget js/window "msRequestAnimationFrame")
                         (fn [cb]
                             (.call (aget js/window "setTimeout") js/window cb (/ 1000 60)))))))

(def to-render #{})
(def to-run [])

(defn force-update [this]
      (set! to-render (conj to-render this)))

(defn force-update! [this]
      (when-not (true? (.-unmounted this))
                (try (.forceUpdate this)
                     (catch js/Error e
                       (if-let [on-error (aget this "onError")]
                               (on-error e)
                               (do (.debug js/console "No :on-error method in component" this)
                                   (.error js/console e)))))))

(defn flush!
      []
      (when-not ^:boolean (empty? to-render)
                (doseq [c to-render]
                       (force-update! c))
                (set! to-render #{}))

      (when-not ^:boolean (empty? to-run)
                (doseq [f to-run] (f))
                (set! to-run [])))

(defn render-loop
      [frame-ms]
      (set! frame-count (inc frame-count))
      (when ^:boolean (and (true? count-fps?) (identical? 0 (mod frame-count 29)))
            (set! frame-rate (* 1000 (/ 30 (- frame-ms last-fps-time))))
            (set! last-fps-time frame-ms))

      (flush!)

      (js/requestAnimationFrame render-loop))

(defn schedule! [f]
      (set! to-run (conj to-run f)))

(defn init [] (js/requestAnimationFrame render-loop))
