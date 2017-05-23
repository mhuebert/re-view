(ns app.views.code
  (:require [re-view.core :as v :refer [defview]]
            [app.views :as views]
            [re-view-material.core :as ui]
            [re-view-material.icons :as icons]
            [app.util :as util]
            [goog.string.path :as path]))


(defview repo-file-page
  [this repo file-path]
  (views/page {:toolbar-items [(views/clojars-latest-version "re-view")
                               (views/edit-button (-> (str "https://github.com/re-view/" repo "/edit/master")
                                                      (path/join file-path)))]}
              (views/markdown-page (-> (str "https://raw.githubusercontent.com/re-view/" repo "/master/")
                                       (path/join file-path)))))


(defn repository-row [repo]
  [:div

   [:.flex
    [:.w5
     (views/clojars-latest-version repo)]

    (ui/Button {:href    (str "https://www.github.com/re-view/" repo)
                :label   [:span.di-ns.dn "source"]
                :icon    util/github-icon
                :class   "o-70 hover-o-100"
                :compact true
                :dense   true
                :target  "_blank"})
    (ui/Button {:href    (str "/code/" repo "/CHANGELOG.md")
                :label   [:span.di-ns.dn "changelog"]
                :class   "o-70 hover-o-100"
                :compact true
                :dense   true
                :icon    icons/ChangeHistory})
    ]])

(defn repository-page [repo]
  (views/page nil [:.pv3 (repository-row repo)]))

(defview repositories-index []

  (views/page nil
              [:div.pb3

               [:h2 "Core + Utilities"]

               (map repository-row ["re-view"
                                    "re-view-hiccup"
                                    "re-view-routing"
                                    "re-db"])
               [:h2 "Components"]

               (map repository-row ["re-view-material"
                                    "re-view-prosemirror"])]))

