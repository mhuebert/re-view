(ns re-view.material.components.toolbar
  (:require [re-view.core :as v]
            [re-view.material.mdc :as mdc]
            [re-view.material.util :as util]

            ["@material/toolbar/foundation" :as foundation]
            ["@material/toolbar/constants" :as constants]
            [goog.dom :as gdom]
            [goog.dom.classes :as classes]
            [goog.object :as gobj]))


(mdc/defadapter ToolbarAdapter
  foundation/default
  [{:keys [with-content view/state] :as component}]
  (let [^js root (v/dom-node component)
        ^js toolbar-element (util/find-node root #(classes/has % "mdc-toolbar"))
        ^js first-row-element (util/find-node toolbar-element #(classes/has % "mdc-toolbar__row"))
        ^js title-element (util/find-node toolbar-element #(classes/has % "mdc-toolbar__title"))
        ^js parent-window (or (some-> root (gobj/get "ownerDocument") (gobj/get "defaultView"))
                              mdc/Window)
        ^js fixed-adjust-element (util/find-node root #(classes/has % "mdc-toolbar-fixed-adjust"))]
    {:root                           toolbar-element
     :firstRowElement                first-row-element
     :titleElement                   title-element
     :registerScrollHandler          (mdc/interaction-handler :listen parent-window "scroll")
     :deregisterScrollHandler        (mdc/interaction-handler :unlisten parent-window "scroll")
     :registerResizeHandler          (mdc/interaction-handler :listen parent-window "resize")
     :deregisterResizeHandler        (mdc/interaction-handler :unlisten parent-window "resize")
     :getViewportWidth               #(.-innerWidth parent-window)
     :getViewportScrollY             #(.-pageYOffset parent-window)
     :getOffsetHeight                #(.-offsetHeight toolbar-element)
     :getFirstRowElementOffsetHeight #(.-offsetHeight first-row-element)
     :notifyChange                   (fn [ratio])
     :setStyle                       (mdc/style-handler toolbar-element)
     :setStyleForTitleElement        (mdc/style-handler title-element)
     :setStyleForFlexibleRowElement  (mdc/style-handler first-row-element)
     :setStyleForFixedAdjustElement  (mdc/style-handler fixed-adjust-element)
     :fixedAdjustElement             fixed-adjust-element}))


(def update-toolbar-styles
  (v/compseq (mdc/mdc-style-update :Toolbar :root)
             (mdc/mdc-style-update :Toolbar :titleElement)
             (mdc/mdc-style-update :Toolbar :flexibleRowElement)
             (mdc/mdc-style-update :Toolbar :fixedAdjustElement)))

(defn reset-toolbar
  "Toolbar must be totally reset if props change."
  [{:keys [view/state] :as this}]
  (v/swap-silently! state (constantly {}))
  (update-toolbar-styles this)
  (mdc/destroy this ToolbarAdapter)
  (mdc/init this ToolbarAdapter))

(v/defview Toolbar
  "Toolbar."
  {:spec/props        {:fixed        {:doc  "Fixes the toolbar to top of screen."
                                      :spec #{true false :lastrow-only}}
                       :waterfall    {:spec :Boolean
                                      :doc  "On scroll, toolbar will gain elevation."}
                       :flexible     {:spec #{true false :default-behavior}
                                      :doc  "Toolbar starts out large, then shrinks gradually as user scrolls down."}
                       :rtl          ::mdc/rtl
                       :with-content {:spec :Boolean
                                      :doc  "If true, last child element will be rendered as sibling of Toolbar, with margin applied to adjust for fixed toolbar size."}}
   :spec/children     [:& :Element]
   :view/did-mount    (fn [this]
                        ;; free candy to the person who figures out why this only works after a delay
                        (js/setTimeout #(when (v/mounted? this)
                                          (mdc/init this ToolbarAdapter)) 500))
   :set-key-heights   (fn [^js this]
                        (.setKeyHeights_ ^js (.-mdcToolbar this)))
   :view/did-update   [(fn [{:keys [view/props
                                    view/prev-props
                                    view/children
                                    view/prev-children] :as ^js this}]
                         (when (and prev-props (not= props prev-props))
                           (reset-toolbar this))
                         (when (and prev-children (not= children prev-children))
                           (.setKeyHeights this)))
                       update-toolbar-styles]
   :view/will-unmount #(mdc/destroy % ToolbarAdapter)}
  [{:keys [view/state
           fixed
           waterfall
           flexible
           rtl
           with-content] :as this} & body]
  (let [[toolbar-content sibling-content] (if with-content [(drop-last body) (last body)] [body nil])
        toolbar-props (-> (v/pass-props this)
                          (cond-> rtl (assoc :dir "rtl"))
                          (assoc :key "toolbar")
                          (update :classes into (cond-> (:mdc/Toolbar-classes @state)
                                                        fixed (conj (.-FIXED constants/cssClasses))
                                                        (= fixed :lastrow-only) (conj (.-FIXED_LASTROW constants/cssClasses))
                                                        waterfall (conj "mdc-toolbar--waterfall")
                                                        flexible (conj (.-TOOLBAR_ROW_FLEXIBLE constants/cssClasses))
                                                        (= flexible :default-behavior) (conj (.-FLEXIBLE_DEFAULT_BEHAVIOR constants/cssClasses)))))
        toolbar [:header.mdc-toolbar toolbar-props toolbar-content]]

    [:div
     toolbar
     (when with-content
       [:div {:key   "toolbar-sibling"
              :class (when fixed "mdc-toolbar-fixed-adjust")} sibling-content])]))

(def ToolbarWithContent
  (v/partial Toolbar {:react-keys {:display-name "material/ToolbarWithContent"}} {:with-content true}))

(v/defview ToolbarSection
  "Toolbar section.

  :align (middle): :start or :end
  :shrink? (false): use for very long titles"
  {:spec/props {:align          {:spec    #{:start :center :end}
                                 :default :center}
                :shrink-to-fit? :Boolean}}
  [{:keys [align shrink-to-fit?] :as this} & content]
  [:section.mdc-toolbar__section
   (-> (v/pass-props this)
       (assoc :role "toolbar")
       (update :classes into (cond-> []
                                     align (conj (case align :center ""
                                                             :end "mdc-toolbar__section--align-end"
                                                             :start "mdc-toolbar__section--align-start"))
                                     shrink-to-fit? (conj "mdc-toolbar__section--shrink-to-fit"))))
   content])

(v/defn ToolbarTitle [{:keys [href] :as props} title]

  [(if href :a :span)
   (update props :classes conj "mdc-toolbar__title") title])

(defn ToolbarRow [& content]
  (into [:.mdc-toolbar__row] content))
