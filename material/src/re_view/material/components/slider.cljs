(ns re-view.material.components.slider
  (:require [re-view.core :as v]

            [re-view.material.mdc :as mdc]
            [re-view.material.util :as util]

            ["@material/slider/foundation" :as foundation]

            [goog.dom.classes :as classes]
            [goog.object :as gobj]
            [cljs.spec.alpha :as s]))


(defn- attr-name->kw [attr-name]
  (case attr-name
    "aria-valuenow" ::value-now
    "aria-valuemin" ::value-min
    "aria-valuemax" ::value-max))


(mdc/defadapter SliderAdapter
  foundation/default
  [component]
  (let [^js dom-node (v/dom-node component)

        ^js root (util/find-node dom-node #(classes/has % "mdc-slider"))

        ^js thumb-container (util/find-node root #(classes/has % "mdc-slider__thumb-container"))]

    {:root                                       root

     :getAttribute                               (fn [attr-name]
                                                   (this-as this
                                                     (let [*state (aget this "state")

                                                           kw (attr-name->kw attr-name)]

                                                       (get @*state kw))))

     :setAttribute                               (fn [attr-name attr-value]
                                                   (this-as this
                                                     (let [*state (aget this "state")

                                                           kw (attr-name->kw attr-name)]

                                                       (swap! *state assoc kw attr-value))))

     :removeAttribute                            (fn [attr-name]
                                                   (this-as this
                                                     (let [*state (aget this "state")

                                                           kw (attr-name->kw attr-name)]

                                                       (swap! *state dissoc kw))))


     ;; TODO
     :computeBoundingRect                        (fn []
                                                   (js/console.log "TODO" :computeBoundingRect)

                                                   (.getBoundingClientRect root))

     ;; TODO
     :getTabIndex                                (fn []
                                                   (js/console.log "TODO" :getTabIndex)

                                                   (.-tabIndex root))

     :registerInteractionHandler                 (mdc/general-interaction-handler :listen root)
     :deregisterInteractionHandler               (mdc/general-interaction-handler :unlisten root)

     :registerThumbContainerInteractionHandler   (mdc/general-interaction-handler :listen thumb-container)
     :deregisterThumbContainerInteractionHandler (mdc/general-interaction-handler :unlisten thumb-container)

     :registerBodyInteractionHandler             (mdc/general-interaction-handler :listen mdc/Body {:passive? false})
     :deregisterBodyInteractionHandler           (mdc/general-interaction-handler :unlisten mdc/Body {:passive? false})

     ;; TODO
     :registerResizeHandler                      (fn [handler]
                                                   (js/console.log "TODO" :registerResizeHandler))

     ;; TODO
     :deregisterResizeHandler                    (fn [handler]
                                                   (js/console.log "TODO" :deregisterResizeHandler))

     ;; TODO
     :notifyInput                                (fn []
                                                   nil)

     ;; TODO
     :notifyChange                               (fn []
                                                   nil)

     ;; TODO this is bad :|
     :setThumbContainerStyleProperty             (fn [property-name value]
                                                   (this-as this
                                                     (let [*state (aget this "state")]
                                                       (swap! *state assoc ::thumb-container-style {property-name value}))))

     ;; TODO this is also bad :|
     :setTrackStyleProperty                      (fn [property-name value]
                                                   (this-as this
                                                     (let [*state (aget this "state")]
                                                       (swap! *state assoc ::track-style {property-name value}))))

     ;; TODO
     :setMarkerValue                             (fn [value]
                                                   (js/console.log "TODO" :setMarkerValue value))

     ;; TODO
     :appendTrackMarkers                         (fn [num-markers]
                                                   (js/console.log "TODO" :appendTrackMarkers num-markers))

     ;; TODO
     :removeTrackMarkers                         (fn []
                                                   (js/console.log "TODO" :removeTrackMarkers))

     ;; TODO
     :setLastTrackMarkersStyleProperty           (fn [property-name value]
                                                   (js/console.log "TODO" :setLastTrackMarkersStyleProperty property-name value))}))


(v/defview Slider
  "Let users select from a range of values by moving the slider thumb. [More](https://material.io/guidelines/components/sliders.html)"
  {:spec/props         {:value     {:doc      "The current value of the slider."
                                    :spec     :Number
                                    :required true}

                        :min       {:doc      "The minimum value a slider can have."
                                    :spec     :Number
                                    :required true}

                        :max       {:doc      "The maximum value a slider can have."
                                    :spec     :Number
                                    :required true}

                        :step      {:doc      "Specifies the increments at which a slider value can be set."
                                    :spec     :Number
                                    :required false}

                        :disabled? {:doc     "Whether or not the slider is disabled."
                                    :spec    :Boolean
                                    :default false}

                        :on-input  {:doc      "Whenever the slider value is changed by way of a user event."
                                    :spec     :Function
                                    :required false}

                        :on-change {:doc      "Whenever the slider value is changed and committed by way of a user event."
                                    :spec     :Function
                                    :required false}}

   :view/initial-state (fn [{:keys [value min max]}]
                         {::value-min min
                          ::value-max max
                          ::value-now value})

   :view/did-mount     (fn [{*state :view/state :as this}]
                         (mdc/init this SliderAdapter)

                         (let [{value-min ::value-min
                                value-max ::value-max
                                value-now ::value-now} @*state

                               foundation (aget this "mdcSlider")]

                           ;; Just like `initialSyncWithDOM` in `MDCComponent`
                           (.setMin foundation value-min)
                           (.setMax foundation value-max)
                           (.setValue foundation value-now)))

   :view/did-update    (fn [{:keys [value min max view/state]}]

                         ;(js/console.log :view/did-update value min max @state)

                         )

   :view/will-unmount  #(mdc/destroy % SliderAdapter)}

  [{*state :view/state}]

  (let [{value-min             ::value-min
         value-max             ::value-max
         value-now             ::value-now
         thumb-container-style ::thumb-container-style
         track-style           ::track-style} @*state]

    [:.mdc-slider
     {:tab-index     "0"
      :role          "slider"
      :aria-valuemin value-min
      :aria-valuemax value-max
      :aria-valuenow value-now
      :aria-label    "Select Value"}

     [:.mdc-slider__track-container
      [:div.mdc-slider__track
       {:style (or track-style {})}]]

     [:div.mdc-slider__thumb-container
      {:style (or thumb-container-style {})}

      [:div.mdc-slider__pin
       [:span.mdc-slider__pin-value-marker value-now]]

      [:svg
       {:class  "mdc-slider__thumb"
        :width  "21"
        :height "21"}
       [:circle
        {:cx "10.5"
         :cy "10.5"
         :r  "7.875"}]
       [:.mdc-slider__focus-ring]]]]))
