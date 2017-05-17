(ns app.docs
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]))


(defview toolbar []
  (ui/ToolbarSection
    {:class "flex items-center"}
    (ui/ToolbarTitle "Docs")
    [:.flex-auto]))

(defview home
  []
  [:div "Docsx"])