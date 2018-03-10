(ns re-view.render-loop
  (:require ["react" :as react]
            ["react-dom" :as react-dom]))

(set! *warn-on-infer* true)
(defonce ^:dynamic *immediate-state-update* false)

(goog-define count-fps? false)
(defonce ^:private last-fps-time 1)
(defonce frame-rate 0)
(defonce frame-count 0)

(defonce fps-element
         (memoize (fn []
                    (-> js/document.body
                        (.appendChild (doto (js/document.createElement "div")
                                        (.setAttribute "style"  "padding: 3px 3px 0 0; font-size: 9px;")
                                        (.setAttribute "class" "fixed top-0 right-0 z-max monospace gray")))))))

(defn render-fps []
  (react-dom/render (react/createElement "div" #js {} (str (Math.floor frame-rate)))
                    (fps-element)))

(defn toggle-fps!
  [value]
  (set! count-fps? (if (some? value)
                     value (not count-fps?))))

(defonce _raf-polyfill
         (when (js* "typeof window !== 'undefined'")
           (if-not (.-requestAnimationFrame js/window)
             (set! (.-requestAnimationFrame js/window)
                   (or
                    (.-webkitRequestAnimationFrame js/window)
                    (.-mozRequestAnimationFrame js/window)
                    (.-oRequestAnimationFrame js/window)
                    (.-msRequestAnimationFrame js/window)
                    (fn [cb]
                      (js/setTimeout cb (/ 1000 60))))))))

(defonce to-render (volatile! #{}))
(defonce to-run (volatile! []))

(declare request-render)

(defn schedule! [f]
  (vswap! to-run conj f)
  (request-render))

(defn force-update-caught [^js this]
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
  (when-not ^boolean (empty? @to-render)
    (let [components @to-render]
      (vreset! to-render #{})
      (doseq [c components]
        (force-update-caught c))))

  (when-not ^boolean (empty? @to-run)
    (let [fns @to-run]
      (vreset! to-run [])
      (doseq [f fns] (f)))))

(defn render-loop
  [frame-ms]
  (when ^boolean (true? count-fps?)
    (set! frame-count (inc frame-count))
    (when (identical? 0 (mod frame-count 29))
      (set! frame-rate (* 1000 (/ 30 (- frame-ms last-fps-time))))
      (set! last-fps-time frame-ms)
      (render-fps)))
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