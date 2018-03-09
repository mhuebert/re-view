(ns re-view.material.components.checkbox
  (:require [re-view.core :as v]
            [re-view.util :refer [update-attrs]]

            [re-view.material.mdc :as mdc]
            [re-view.material.util :as util]
            [re-view.material.components.ripple :refer [Ripple]]
            [re-view.material.components.ripple :as ripple]
            [re-view.material.components.form-field :refer [FormFieldAdapter]]

            ["@material/checkbox/foundation" :as foundation]
            ["@material/animation" :refer [getCorrectEventName]]

            [goog.dom.classes :as classes]
            [goog.object :as gobj]))

(mdc/defadapter CheckboxAdapter
  foundation/default
  [component]
  {:root                          (util/find-node (v/dom-node component) #(classes/has "mdc-checkbox"))
   :registerAnimationEndHandler   (mdc/interaction-handler :listen "root" (getCorrectEventName mdc/Window "animationend"))
   :deregisterAnimationEndHandler (mdc/interaction-handler :unlisten "root" (getCorrectEventName mdc/Window "animationend"))
   :registerChangeHandler         (mdc/interaction-handler :listen "nativeInput" "change")
   :deregisterChangeHandler       (mdc/interaction-handler :unlisten "nativeInput" "change")
   :forceLayout                   #(this-as this (gobj/get (gobj/get this "nativeInput") "offsetWidth"))
   :isAttachedToDOM               #(this-as this (boolean (gobj/get this "root")))
   :getNativeControl              #(this-as this (gobj/get this "nativeInput"))})

(defn- formField-attrs [{:keys [mdc/FormField-classes align-end rtl field-classes]}]
  {:classes (cond-> (into field-classes FormField-classes)
                    align-end (conj "mdc-form-field--align-end"))
   :dir     (when rtl "rtl")})

(v/defview Checkbox
  "Allow the selection of multiple options from a set. [More](https://material.io/guidelines/components/selection-controls.html#)"
  {:key :id
   :spec/props {:props/keys #{::mdc/disabled
                              ::mdc/dense
                              ::mdc/value
                              ::mdc/rtl
                              ::mdc/label
                              ::mdc/id}
                :checked :Boolean
                :align-end :Boolean}
   :view/did-mount #(mdc/init % ripple/RippleAdapter CheckboxAdapter FormFieldAdapter)
   :view/will-unmount #(mdc/destroy % ripple/RippleAdapter CheckboxAdapter FormFieldAdapter)
   :view/did-update (mdc/mdc-style-update :Ripple :root)}
  [{:keys [id name label view/props view/state
           dense
           label-class
           input-class] :as this}]
  (when (contains? props :label-class) (throw "label-class in Checkbox not supported"))
  (let [{:keys [mdc/Checkbox-classes
                mdc/Ripple-classes]} @state]
    [:.mdc-form-field
     (formField-attrs props)
     [:.mdc-checkbox.mdc-ripple-target
      {:classes                      (into Checkbox-classes Ripple-classes)
       :data-mdc-ripple-is-unbounded true
       :style                        (when dense {:margin "-0.5rem 0"})}
      [:input.mdc-checkbox__native-control (-> (v/pass-props this)
                                               (merge {:type  "checkbox"
                                                       :class input-class
                                                       :id    (or id name)}))]
      [:div.mdc-checkbox__background
       [:svg.mdc-checkbox__checkmark
        {:view-box "0 0 24 24"}
        [:path.mdc-checkbox__checkmark__path
         {:fill   "none"
          :stroke "white"
          :d      "M1.73,12.91 8.1,19.28 22.79,4.59"}]]
       [:.mdc-checkbox__mixedmark]]]
     (when label
       (-> label
           (cond->> (string? label) (conj [:label]))
           (update-attrs #(-> %
                              (assoc :for (or id name))
                              (update :classes conj label-class)))))]))