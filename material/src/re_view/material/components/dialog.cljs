(ns re-view.material.components.dialog
  (:require [re-view.core :as v]
            [re-view.material.mdc :as mdc]
            [re-view.material.util :as util]

            ["@material/dialog/foundation" :as foundation]
            ["@material/dialog/util" :refer [createFocusTrapInstance]]

            [re-view.material.components.button :refer [Button]]

            [goog.dom.classes :as classes]
            [goog.dom :as gdom]
            [goog.object :as gobj]
            [re-view.material.ext :as ext]))

(mdc/defadapter DialogAdapter
  foundation/default
  [{:keys [view/state on-accept on-cancel] :as ^js component}]
  (let [root (v/dom-node component)
        accept-btn (gdom/findNode root #(classes/has % "mdc-dialog__footer__button--accept"))
        surface (gdom/findNode root #(classes/has % "mdc-dialog__surface"))
        ^js focus-trap (createFocusTrapInstance surface accept-btn)]
    {:surface                             surface
     :acceptButton                        accept-btn
     :eventTargetHasClass                 (fn [target class-name]
                                            (util/closest target #(classes/has % class-name)))
     :registerSurfaceInteractionHandler   (mdc/interaction-handler :listen "surface")
     :deregisterSurfaceInteractionHandler (mdc/interaction-handler :unlisten "surface")
     :registerDocumentKeydownHandler      (mdc/interaction-handler :listen mdc/Document "keydown")
     :deregisterDocumentKeydownHandler    (mdc/interaction-handler :unlisten mdc/Document "keydown")
     :notifyAccept                        (or on-accept #(println :accept))
     :notifyCancel                        (or on-cancel #(println :cancel))
     :trapFocusOnSurface                  #(.activate focus-trap)
     :untrapFocusOnSurface                #(.deactivate focus-trap)
     :registerTransitionEndHandler        (mdc/interaction-handler :listen surface "transitionend")
     :deregisterTransitionEndHandler      (mdc/interaction-handler :unlisten surface "transitionend")
     :isDialog                            #(= % surface)}))

(v/defview Dialog
  {:spec/props         {:label-accept   :String
                        :label-cancel   :String
                        :scrollable?    :Boolean
                        :content-header :Element}
   :spec/children      [:& :Element]
   :view/initial-state {:mdc/styles {}}
   :view/did-mount     [#(mdc/init % DialogAdapter)
                        (mdc/mdc-style-update :Dialog :root)]
   :view/did-update    (mdc/mdc-style-update :Dialog :root)
   :view/will-unmount  #(mdc/destroy % DialogAdapter)
   :open               (fn [this]
                         (.open (gobj/get this "mdcDialog")))
   :close              (fn [this]
                         (.close (gobj/get this "mdcDialog")))
   }
  [{:keys [label-accept
           label-cancel
           view/state
           scrollable?
           content-header]
    :or   {label-accept "OK"
           label-cancel "Cancel"}} & body]
  [:aside#mdc-dialog.mdc-dialog
   {:classes          (:mdc/Dialog-classes @state)
    :role             "alertdialog"
    :aria-hidden      "true"
    :aria-labelledby  "mdc-dialog-label"
    :aria-describedby "mdc-dialog-body"}
   [:.mdc-dialog__surface
    (some->> content-header (conj [:header.mdc-dialog__header]))
    (into [:section#mdc-dialog-body.mdc-dialog__body
           {:class (when scrollable? "mdc-dialog__body--scrollable")}]
          body)
    [:footer.mdc-dialog__footer
     (Button {:classes ["mdc-dialog__footer__button"
                        "mdc-dialog__footer__button--cancel"]
              :label   label-cancel})
     (Button {:classes ["mdc-dialog__footer__button"
                        "mdc-dialog__footer__button--accept"]
              :color   :primary
              :label   label-accept})]]
   [:.mdc-dialog__backdrop]])

(def DialogWithTrigger (ext/with-trigger Dialog))