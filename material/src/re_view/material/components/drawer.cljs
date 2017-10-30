(ns re-view.material.components.drawer
  (:require [re-view.core :as v]
            [re-view.material.mdc :as mdc]
            [re-view.routing :as routing]
            [re-view.material.util :as util]
            [re-view.material.ext :as ext]

            ["@material/animation" :refer [getCorrectEventName]]
            ["@material/drawer/util" :as drawer-util :refer [remapEvent
                                                             getTransformPropertyName
                                                             supportsCssCustomProperties
                                                             saveElementTabState
                                                             restoreElementTabState]]
            ["@material/drawer/temporary/foundation" :as temporary-foundation]

            [re-view.material.components.list :as list-views]

            [goog.dom.classes :as classes]
            [goog.object :as gobj]
            [clojure.string :as string]))

(defn remap-event [f]
  (fn [event-type handler]
    (f (remapEvent event-type) handler)))

(mdc/defadapter TemporaryDrawerAdapter
  temporary-foundation/default
  [{:keys [view/state] :as ^react/Component component}]
  (let [root (v/dom-node component)
        ^js drawer (util/find-node root (fn [el] (classes/has el "mdc-temporary-drawer__drawer")))]
    (cond-> {:drawer                             drawer
             :hasNecessaryDom                    #(do drawer)
             :registerInteractionHandler         (remap-event (mdc/interaction-handler :listen root))
             :deregisterInteractionHandler       (remap-event (mdc/interaction-handler :unlisten root))
             :registerDrawerInteractionHandler   (remap-event (mdc/interaction-handler :listen drawer))
             :deregisterDrawerInteractionHandler (remap-event (mdc/interaction-handler :unlisten drawer))
             :registerTransitionEndHandler       (mdc/interaction-handler :listen drawer "transitionend")
             :deregisterTransitionEndHandler     (mdc/interaction-handler :unlisten drawer "transitionend")
             :getDrawerWidth                     #(util/force-layout drawer)
             :setTranslateX                      (fn [n]
                                                   (swap! state assoc-in [:mdc/root-styles (getTransformPropertyName)]
                                                          (when n (str "translateX(" n "px)"))))
             :updateCssVariable                  (fn [value]
                                                   (when (supportsCssCustomProperties)
                                                     (swap! state assoc-in [:mdc/root-styles (gobj/getValueByKeys temporary-foundation/default "strings" "OPACITY_VAR_NAME")] value)))
             :getFocusableElements               #(.querySelectorAll drawer (gobj/getValueByKeys temporary-foundation/default "strings" "FOCUSABLE_ELEMENTS"))
             :saveElementTabState                saveElementTabState
             :restoreElementTabState             restoreElementTabState
             :makeElementUntabbable              #(.setAttribute ^js % "tabindex" -1)
             :isDrawer                           #(= % drawer)}
            (.-notifyOpen component) (assoc :notifyOpen (.-notifyOpen component))
            (.-notifyClose component) (assoc :notifyClose (.-notifyClose component)))))

(def PermanentDrawerToolbarSpacer [:.mdc-permanent-drawer__toolbar-spacer])
(v/defview PermanentDrawer
  "Permanent navigation drawers are always visible and pinned to the left edge, at the same elevation as the content or background. They cannot be closed. The recommended default for desktop. [More](https://material.io/guidelines/patterns/navigation-drawer.html#navigation-drawer-behavior)"
  {:spec/children [:& :Element]}
  [this & content]
  [:.mdc-permanent-drawer
   (v/pass-props this)
   (into [:.mdc-permanent-drawer__content] content)])

(defn TemporaryDrawerHeader [& content]
  [:.mdc-temporary-drawer__header
   (into [:.mdc-temporary-drawer__header-content] content)])

(v/defview TemporaryDrawer
  "Slides in from the left and contains the navigation destinations for your app. [More](https://material.io/guidelines/patterns/navigation-drawer.html)"
  {:view/did-mount          (fn [{:keys [open? view/state] :as this}]
                              (mdc/init this TemporaryDrawerAdapter)
                              (swap! state assoc :route-listener (routing/listen (aget this "close") {:fire-now? false}))
                              (when open? (.open this)))
   :view/will-receive-props (fn [{open? :open? {prev-open? :open?} :view/prev-props :as this}]
                              (cond (and open? (not prev-open?)) (.open this)
                                    (and prev-open? (not open?)) (.close this)))
   :view/will-unmount       #(do (mdc/destroy % TemporaryDrawerAdapter)
                                 (routing/unlisten (:route-listener @(:view/state %))))
   :view/did-update         (mdc/mdc-style-update :TemporaryDrawer :drawer)
   :foundation              (fn [this]
                              (let [^js foundation (gobj/get this "mdcTemporaryDrawer")]
                                foundation))
   :open                    (fn [this] (.open (.foundation this)))
   :close                   (fn [this] (.close (.foundation this)))
   :notifyOpen              (fn [{:keys [on-open]}] (when on-open (on-open)))
   :notifyClose             (fn [{:keys [on-close]}] (when on-close (on-close)))
   :spec/props              {:toolbar-spacer? :Boolean
                             :header-content  :Element
                             :open?           :Boolean
                             :on-open         :Function
                             :on-close        :Function}
   :spec/children           [:& :Element]
   }
  [{:keys      [toolbar-spacer? header-content view/state]
    list-props :view/props
    :as        this} & list-items]
  [:.mdc-temporary-drawer
   {:class (string/join " " (:mdc/TemporaryDrawer-classes @state))}
   [:.mdc-temporary-drawer__drawer
    (when header-content
      [:.mdc-temporary-drawer__header
       [:.mdc-temporary-drawer__header-content header-content]])
    (apply list-views/List (-> (v/pass-props this)
                               (update :classes conj "mdc-temporary-drawer__content")) list-items)]])

(def TemporaryDrawerWithTrigger (ext/with-trigger TemporaryDrawer))
