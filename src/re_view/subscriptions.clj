(ns re-view.subscriptions)

(defmacro reactive
  [prop-binding & body]
  (let [[prop-binding body] (if (vector? prop-binding) [prop-binding body]
                                                       [[] (cons prop-binding body)])
        f `(fn ~prop-binding ~@body)]
    `(fn [this# st-key#]
       (let [pattern-result# (~'atom (~'re-db.core/capture-patterns (~f (:props this#))))]
         {:default       #(:value @pattern-result#)
          :subscribe     #(let [cb# (fn [x#] (~'swap! this# assoc st-key# x#))
                                listen-args# (concat (:patterns @pattern-result#) (list cb#))]
                           (~'apply ~'re-db.d/listen! listen-args#)

                           (~'swap! this# update-in [:subscriptions st-key# :unsubscribe-cbs] conj
                             (fn [] (~'apply ~'re-db.d/unlisten! listen-args#))))
          :unsubscribe   #(do (doseq [f# (get-in this# [:subscriptions st-key# :unsubscribe-cbs])]
                                (f#))
                              (swap! this# update-in [:subscriptions st-key#] dissoc :unsubscribe-cbs))
          :should-update (fn []
                           (let [next-pattern-result# (~'re-db.core/capture-patterns (~f (:props this#)))]
                             (when (not= (:patterns next-pattern-result#)
                                         (:patterns @pattern-result#))
                               (reset! pattern-result# next-pattern-result#)
                               true)))}))))