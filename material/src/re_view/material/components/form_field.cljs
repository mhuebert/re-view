(ns re-view.material.components.form-field
  (:require [re-view.core :as v]
            [re-view.material.mdc :as mdc]
            [re-view.material.util :as util]

            ["@material/form-field/foundation" :as foundation]

            [goog.object :as gobj]))

(mdc/defadapter FormFieldAdapter
  foundation/default
  [component]
  {:root                  (util/find-tag (v/dom-node component) #"LABEL")
   :activateInputRipple   #(this-as this
                             (let [ripple (-> (gobj/get this "component")
                                              (gobj/get "mdcRipple"))]
                               (.activate ripple)))
   :deactivateInputRipple #(this-as this
                             (let [ripple (-> (gobj/get this "component")
                                              (gobj/get "mdcRipple"))]
                               (.deactivate ripple)))})