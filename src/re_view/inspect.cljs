(ns re-view.inspect)

(defn traverse-react-tree
  "Experimental. Traverses entire mounted DOM tree, similar to React DevTools, to inspect state."
  [c]
  (let [instance (or (aget c "_reactInternalInstance") c)]
    (if (aget instance "_stringText")
      "<string>"
      (let [display-name (or (some-> instance
                                     (aget "_currentElement")
                                     (aget "type")
                                     (aget "displayName"))
                             (aget instance "_tag"))
            rendered-children (or (aget instance "_renderedChildren")
                                  (let [c (aget instance "_renderedComponent")]
                                    (some-> c
                                            (cond-> (aget c "_renderedComponent") (aget "_renderedComponent"))
                                            (aget "_renderedChildren"))))
            re-view-js (some-> instance (aget "_instance") (aget "re$view"))

            re-view (when re-view-js (cond-> {}
                                             (aget re-view-js "props") (assoc :view/props (keys (aget re-view-js "props")))
                                             (aget re-view-js "children") (assoc :view/children (count (aget re-view-js "children")))
                                             (aget re-view-js "dbPatterns") (assoc :view/db-patterns (aget re-view-js "dbPatterns"))))]
        (into (cond-> [display-name]
                      re-view (conj re-view))
              (some->> rendered-children
                       (.values js/Object)
                       (mapv traverse-react-tree)))))))

(defn ancestor-names [c]
  )