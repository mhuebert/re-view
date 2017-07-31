(ns app.views.code
  (:require [re-view.core :as v :refer [defview]]
            [app.views :as views]
            [re-view-material.core :as ui]
            [re-view-material.icons :as icons]
            [app.util :as util]
            [goog.string.path :as path]))


(defview repo-file-page
  [this repo file-path]
  (views/markdown-page (merge {:read (-> (str "https://raw.githubusercontent.com/braintripping/re-view/master/" (munge repo))
                                         (path/join file-path))
                               :edit (-> (str "https://github.com/braintripping/re-view/edit/master/" (munge repo))
                                         (path/join file-path))}
                              (v/pass-props this))))


(defview repository-row
  {:key (fn [_ repo] repo)}
  [_ repo]
  [:.f6.flex.items-center
   [:a.mr2 {:href (str "https://www.github.com/braintripping/re-view/tree/master/" (munge repo))} "source"]
   [:a.mr2 {:href (str "/code/" repo "/CHANGELOG.md")} "changelog"]
   (views/clickable-version repo)])

(defn repository-page [repo]
  (views/page nil [:.pv3 (repository-row repo)]))

(defview repositories-index []

  (views/page nil
              [:div.pb3

               [:.f4.o-50.mt4 "Core Libraries"]

               [:p "The basic tools for building an app."]

               [:.f5.b.mt3 "Re-View"]
               (repository-row "re-view")

               [:.f5.b.mt3 "Routing"]
               (repository-row "re-view-routing")

               [:.f4.o-50.mt4 "Component Libraries"]

               [:p "Drop-in components for intuitive, attractive user interfaces."]

               [:.f5.b.mt3 "Material Design Components"]
               (repository-row "re-view-material")

               [:.f5.b.mt3 "Rich Text Components"]
               (repository-row "re-view-prosemirror")

               [:.f4.o-50.mt4 "Dependencies"]

               [:p "Foundational tools used by Re-View"]

               [:.f5.b.mt3 "Re-DB"]
               (repository-row "re-db")

               [:.f5.b.mt3 "Hiccup"]
               (repository-row "re-view-hiccup")]))

