(ns app.views.layout
  (:require [re-view.core :as v :refer [defview]]
            [re-db.d :as d]
            [re-view.material.util :as util]
            [clojure.string :as string]))

(defn page-meta []
  (list
    ;; sets properties on document
    (util/sync-element!
      {:class       (if (d/get :ui/globals :theme/dark?)
                      "mdc-theme--dark bg-mid-gray"
                      "bg-near-white")
       :style       {:min-height "100%"}
       :get-element #(when (exists? js/document)
                       (.-documentElement js/document))})

    ;; stylesheets for code
    [:link {:rel  "stylesheet"
            :type "text/css"
            :href (if (d/get :ui/globals :theme/dark?) "/styles/railscasts.css"
                                                       "/styles/github.css")}]))

(defn active? [href]
  (let [path (d/get :router/location :path)]
    (case href "/" (= path href)
               (string/starts-with? path href))))

