(ns re-view.material.components.text
  (:require [re-view.core :as v]
            [re-view.material.mdc :as mdc]
            [re-view.material.util :as util]

            ["@material/textfield/foundation" :as foundation]

            [clojure.string :as string]
            [goog.object :as gobj]))

(v/defview Input
  {:spec/props              {:element {:doc     "Base element type"
                                       :spec    #{:input :textarea}
                                       :default :input}
                             :mask    {:spec :Function
                                       :doc  "Function to restrict input"}}
   :view/initial-state      #(get % :value)
   :view/will-receive-props (fn [{{prev-value :value} :view/prev-props
                                  {value :value}      :view/props
                                  state               :view/state :as this}]
                              (when-not (= prev-value value)
                                (reset! state value)))
   :view/did-mount          (fn [{:keys [auto-focus] :as this}]
                              (when (true? auto-focus) (-> (v/dom-node this)
                                                           (util/closest (fn [^js el]
                                                                           (#{"INPUT" "TEXTAREA"} (.-tagName el))))
                                                           (.focus))))}
  [{:keys [view/props
           view/state
           element
           on-change
           mask
           on-key-press
           auto-focus
           class] :as this}]
  [element (cond-> (-> (v/pass-props this)
                       (update :classes conj "outline-0"))
                   (contains? props :value)
                   (merge
                     {:value        (or @state "")
                      :on-key-press (fn [^js e]
                                      (when (and mask (= (mask (.-key e)) @state))
                                        (.preventDefault e))
                                      (when on-key-press (on-key-press e)))
                      :on-change    (fn [e]
                                      (binding [re-view.render-loop/*immediate-state-update* true]
                                        (let [target (.. e -target)
                                              value (.. target -value)
                                              new-value (cond-> value
                                                                mask (mask))
                                              cursor (.. target -selectionEnd)]
                                          (when mask (aset target "value" new-value))
                                          (when on-change (on-change e))
                                          (doto target
                                            (aset "selectionStart" cursor)
                                            (aset "selectionEnd" cursor)))))}))])

(mdc/defadapter TextfieldAdapter
  foundation/default
  []
  {:addClassToLabel               (mdc/class-handler :add "label")
   :removeClassFromLabel          (mdc/class-handler :remove "label")
   :addClassToHelptext            (mdc/class-handler :add "help")
   :removeClassFromHelptext       (mdc/class-handler :remove "help")
   :helptextHasClass              #(this-as this (contains? (:mdc/help-classes @(gobj/get this "state")) %))
   :registerInputFocusHandler     (mdc/interaction-handler :listen "nativeInput" "focus")
   :deregisterInputFocusHandler   (mdc/interaction-handler :unlisten "nativeInput" "focus")
   :registerInputBlurHandler      (mdc/interaction-handler :listen "nativeInput" "blur")
   :deregisterInputBlurHandler    (mdc/interaction-handler :unlisten "nativeInput" "blur")
   :registerInputInputHandler     (mdc/interaction-handler :listen "nativeInput" "input")
   :deregisterInputInputHandler   (mdc/interaction-handler :unlisten "nativeInput" "input")
   :registerInputKeydownHandler   (mdc/interaction-handler :listen "nativeInput" "keydown")
   :deregisterInputKeydownHandler (mdc/interaction-handler :unlisten "nativeInput" "keydown")
   :setHelptextAttr               #(this-as this (swap! (gobj/get this "state") update :mdc/help-attrs assoc (keyword %1) %2))
   :removeHelptextAttr            #(this-as this (swap! (gobj/get this "state") update :mdc/help-attrs dissoc %1))
   :getNativeInput                #(this-as this (gobj/get this "nativeInput"))})

(v/defview Text
  "Allow users to input, edit, and select text. [More](https://material.io/guidelines/components/text-fields.html)"
  {:key                :name
   :spec/props         {:props/keys           [::mdc/label ::mdc/dense ::mdc/auto-focus ::mdc/dirty]
                        :floating-label       {:spec    :Boolean
                                               :default true}
                        :help-text-persistent :Boolean
                        :multi-line           :Boolean
                        :full-width           :Boolean
                        :expandable           :Boolean

                        :hint                 :String
                        :error                :String
                        :info                 :String
                        :placeholder          {:spec         :String
                                               :pass-through true}

                        :on-save              :Function
                        :in-progress          :Boolean
                        :input-styles         :Map
                        :container-props      :Map
                        :field-props          :Map

                        :value                {:spec         ::mdc/value
                                               :pass-through true}
                        :default-value        {:spec         ::mdc/default-value
                                               :pass-through true}}
   :view/initial-state {:dirty                 false
                        :mdc/Textfield-classes #{"mdc-textfield--upgraded"}
                        :mdc/label-classes     #{"mdc-textfield__label"}
                        :mdc/help-classes      #{"mdc-textfield-helptext"}}
   :view/did-mount     (fn [this]
                         (mdc/init this TextfieldAdapter))
   :view/will-unmount  (fn [this]
                         (mdc/destroy this TextfieldAdapter))
   :reset              #(swap! (:view/state %) assoc :dirty false)}
  [{:keys [id
           label
           floating-label
           help-text-persistent
           dense
           multi-line
           full-width
           expandable
           focused
           dirty
           hint
           error
           info
           on-save
           in-progress
           input-styles
           container-props
           field-props

           value
           default-value

           view/props
           view/state
           ] :as this}]
  (let [{:keys [focused
                mdc/Textfield-classes
                mdc/label-classes
                mdc/help-classes
                mdc/help-attrs]} @state
        dirty (or (:dirty @state) dirty)

        field-id (or id name)]
    [:div container-props
     [:.mdc-textfield
      (update field-props :classes into (into Textfield-classes
                                              [(when multi-line "mdc-textfield--multiline")
                                               (when (:disabled props) "mdc-textfield--disabled")
                                               (when full-width "mdc-textfield--fullwidth")
                                               (when (:dense props) "mdc-textfield--dense")]))
      (Input
        (-> (v/pass-props this)
            (cond-> full-width (assoc :aria-label label))
            (merge {:element       (if multi-line :textarea :input)
                    :class         "w-100 mdc-textfield__input"
                    :aria-controls (str field-id "-help")
                    :id            field-id
                    :style         input-styles}
                   (util/collect-handlers props {:on-key-down (util/handle-on-save on-save)}))))
      (when (and (not full-width) (util/ensure-str label))
        (let [floatingLabel (boolean (or (:floating-label props)
                                         (util/ensure-str value)
                                         (util/ensure-str default-value)
                                         (util/ensure-str (:placeholder props))))]
          [:label {:class (cond-> (string/join " " label-classes)
                                  floatingLabel (str " mdc-textfield__label--float-above"))
                   :for   field-id}
           label]))
      #_(when in-progress (ProgressIndeterminate {:class "absolute w-100"}))]

     (let [error (seq (util/collect-text (:error props)))
           info (seq (util/collect-text (:info props)))
           hint (seq (util/collect-text (:hint props)))]
       [:div (merge {:class (cond-> (string/join " " help-classes)
                                    (or help-text-persistent
                                        error
                                        info) (str " mdc-textfield-helptext--persistent"))
                     :id    (str field-id "-help")}
                    help-attrs)
        (some->> error (conj [:.dark-red.pb1]))
        (some->> info (conj [:.black.pb1]))
        hint])]))