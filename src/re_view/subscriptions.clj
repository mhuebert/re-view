(ns re-view.subscriptions)

(defmacro db
  ([expr]
   `(~'re-view.subscriptions.db-sub/db-fn (~'fn [this#] ~expr) nil))
  ([prop-binding expr]
   `(~'re-view.subscriptions.db-sub/db-fn (~'fn ~prop-binding ~expr) nil))
  ([prop-binding expr should-update?]
   `(~'re-view.subscriptions.db-sub/db-fn (~'fn ~prop-binding ~expr) ~should-update?)))