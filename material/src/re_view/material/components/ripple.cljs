(ns re-view.material.components.ripple
  (:require [re-view.core :as v]
            [re-view.material.mdc :as mdc :refer [defadapter]]
            [re-view.util :refer [update-attrs]]
            [re-view.material.util :as util]

            ["@material/ripple/foundation" :as foundation]
            ["@material/ripple/util" :refer [supportsCssVariables, getMatchesProperty]]

            [goog.dom.classes :as classes]
            [goog.dom.dataset :as dataset]
            [goog.object :as gobj]))

(def MatchesProperty (when mdc/browser? (getMatchesProperty (.-prototype js/HTMLElement))))

(mdc/defadapter RippleAdapter
  foundation/default
  [component]
  (let [^js target (or (util/find-node (v/dom-node component) #(or (classes/has % "mdc-ripple-surface")
                                                                   (classes/has % "mdc-ripple-target")))
                       (v/dom-node component))]
    {:root                         target
     :rippleTarget                 target
     :updateCssVariable            (mdc/style-handler target)
     :registerInteractionHandler   (mdc/general-interaction-handler :listen "rippleTarget")
     :deregisterInteractionHandler (mdc/general-interaction-handler :unlisten "rippleTarget")
     :browserSupportsCssVars       #(supportsCssVariables mdc/Window)
     :isUnbounded                  #(dataset/has target "mdcRippleIsUnbounded")
     :isSurfaceActive              #(let [^js f (gobj/get target MatchesProperty)]
                                      (.call f target ":active"))
     :registerResizeHandler        #(.addEventListener mdc/Window "resize" %)
     :deregisterResizeHandler      #(.removeEventListener mdc/Window "resize" %)
     :getWindowPageOffset          #(do #js {"x" (gobj/get mdc/Window "pageXOffset")
                                             "y" (gobj/get mdc/Window "pageYOffset")})
     :computeBoundingRect          #(.getBoundingClientRect target)}))

(v/defview Ripple
  "Applies ripple effect to a single child view."
  {:spec/children      [:Hiccup]
   :key                (fn [_ element]
                         (or (get-in element [1 :key])
                             (get-in element [1 :id])))
   :view/did-mount     #(mdc/init % RippleAdapter)
   :view/should-update #(do true)
   :view/did-update    (mdc/mdc-style-update :Ripple :rippleTarget)
   :view/will-unmount  #(mdc/destroy % RippleAdapter)}
  [{:keys [view/state]} hiccup-element]
  (update-attrs hiccup-element update :classes into (:mdc/Ripple-classes @state)))