(ns re-view.material.components.switch
  (:require [re-view.core :as v]
            [re-view.material.mdc :as mdc]))


(v/defview Switch
  "Allow a selection to be turned on or off. [More](https://material.io/guidelines/components/selection-controls.html#selection-controls-radio-button)"
  {:key :id
   :spec/props {:props/keys #{::mdc/rtl ::mdc/color}
                :align-end :Keyword}}
  [{:keys [disabled rtl label color] :as this}]
  (let [color-class (case color :primary "mdc-theme--primary-bg"
                                :accent "mdc-theme--accent-bg"
                                nil)]
    [:.mdc-switch
     (cond-> {}
             disabled (assoc :class "mdc-switch--disabled")
             rtl (assoc :dir "rtl"))
     [:input.mdc-switch__native-control (-> (v/pass-props this)
                                            (assoc :type "checkbox")
                                            (update :checked boolean))]
     [:.mdc-switch__background
      {:class color-class}
      [:.mdc-switch__knob {:class color-class}]]]))

(v/defview SwitchField
  "Allow a selection to be turned on or off. [More](https://material.io/guidelines/components/selection-controls.html#selection-controls-radio-button)"
  {:key :id
   :spec/props {:props/keys #{::mdc/label ::mdc/rtl}
                :id {:spec ::mdc/id
                     :pass-through true
                     :required true}
                :field-classes :Vector
                :align-end :Boolean}}
  [{:keys [id label view/props field-classes rtl align-end] :as this}]
  [:.mdc-form-field
   (cond-> {:classes (cond-> []
                             field-classes (into field-classes)
                             align-end (conj "mdc-form-field--align-end"))}
           rtl (assoc :dir "rtl"))
   (Switch (v/pass-props this))
   [:label {:for   id
            :style {:margin "0 0.25rem"}} label]])