(ns re-view.subscriptions)

(defmacro db
  [prop-binding & body]
  `(~'re-view.subscriptions.db-sub/db-fn ~(if (vector? prop-binding)
                                            `(fn ~prop-binding ~@body)
                                            `(fn [this#] ~@(cons prop-binding body)))))