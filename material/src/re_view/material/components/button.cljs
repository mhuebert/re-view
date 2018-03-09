(ns re-view.material.components.button
  (:require [re-view.core :as v]
            [re-view.material.icons :as icons]
            [re-view.material.util :as util]
            [re-view.material.mdc :as mdc]
            [re-view.material.components.ripple :refer [Ripple]]))

(v/defview Button
  "Communicates the action that will occur when the user touches it. [More](https://material.io/guidelines/components/buttons.html)"
  {:key :label
   :spec/props {:props/keys #{::mdc/color
                              ::mdc/compact
                              ::mdc/dense
                              ::mdc/disabled
                              ::mdc/label
                              ::mdc/raised
                              ::mdc/ripple}
                :icon :SVG
                :icon-end :SVG}}
  [{:keys [href
           label
           icon
           icon-end
           disabled
           dense
           raised
           compact
           ripple
           color]
    :as   this}]
  (let [only-icon? (and icon (contains? #{nil ""} label))]
    ((if ripple Ripple identity)
      [(if (and (not disabled)
                href) :a :button)
       (-> (v/pass-props this)
           (cond-> disabled (dissoc :href :on-click)
                   only-icon? (update :style assoc :min-width "auto"))
           (update :style merge (when (or icon icon-end)
                                  {:display     "inline-flex"
                                   :align-items "center"}))
           (update :classes into (-> ["mdc-button"
                                      (when ripple "mdc-ripple-target")
                                      (when dense "mdc-button--dense")
                                      (when raised "mdc-button--raised")
                                      (when compact "mdc-button--compact")
                                      (when color (str "mdc-button--" (name color)))])))
       (when icon
         (cond-> (icons/style icon {:flex-shrink 0})
                 (not only-icon?) (icons/style {:margin-right "0.5rem"})
                 dense (icons/size 20)))
       [:span {:style {:flex-shrink 0}}
        (when-let [label (util/ensure-str label)]
          label)]
       (when icon-end
         (cond-> (icons/style icon-end {:margin-left "0.5rem"})
                 dense (icons/size 20)))])))

(defn Submit [label]
  (Button
    {:type  :raised
     :color :primary
     :class "w-100 f4 mv3 pv2"
     :label label
     :style {:height "auto"}}))