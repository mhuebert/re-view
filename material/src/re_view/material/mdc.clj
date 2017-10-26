(ns re-view.material.mdc
  (:require [clojure.string :as string]))

(defmacro defadapter
  "Defines a material-components adapter implementation.

  `name` must be of the form 'ComponentNameAdapter' where `ComponentName` is a valid/existing component.

  Foundations and adapters are described here: https://github.com/material-components/material-components-web/blob/master/docs/integrating-into-frameworks.md#the-advanced-approach-using-foundations-and-adapters

  For additional help implementing a particular adapter, look at the source code for the component's
  foundation and reference adapter, eg:

  node_modules/@material/checkbox/adapter.js,
  node_modules/@material/checkbox/foundation.js
  "
  [name foundation-class args & body]
  (let [foundation-name (string/replace (str name) #"Adapter$" "")]
    `(def ~name {:name    ~foundation-name
                 :adapter (~'re-view.material.mdc/make-foundation
                            ~foundation-name
                            ~foundation-class
                            (~'fn ~args ~@body))})))