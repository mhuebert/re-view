(ns re-view.material.components.select
  (:require [re-view.core :as v]))

(v/defview Select
  "Native select element"
  {:spec/children [:& :Element]}
  [{:keys [view/props]} & items]
  (into [:select.mdc-select props] items))