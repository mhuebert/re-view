(ns re-view.subscriptions)

(defmacro reactive
  [prop-binding & body]
  (let [f `(fn ~prop-binding ~@body)]
    `(fn [_# props# cb#]
       (let [pattern-result# (~'atom (~'re-db.core/capture-patterns (~f props#)))
             prev-result# pattern-result#]
         {:default       #(:value @pattern-result#)
          :subscribe     #(apply ~'re-db.d/listen! (concat (:patterns @pattern-result#) (list cb#)))
          :unsubscribe   #(do (apply ~'re-db.d/unlisten! (concat (:patterns @prev-result#) (list cb#)))
                              (reset! prev-result# pattern-result#))
          :should-update (fn [_# next-props#]
                           (let [next-pattern-result# (~'re-db.core/capture-patterns (~f next-props#))]
                             (when (not= (:patterns next-pattern-result#)
                                         (:patterns @pattern-result#))
                               (reset! pattern-result# next-pattern-result#)
                               true)))}))))