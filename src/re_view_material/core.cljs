(ns re-view-material.core
  (:refer-clojure :exclude [List])
  (:require
    [goog.net.XhrIo :as xhr]
    [goog.events :as events]
    [goog.dom :as gdom]
    [re-view.core :as v :refer [defview]]
    [re-view.routing :as routing]
    [re-view-material.icons :as icons]
    [re-view-material.util :as util]
    [clojure.string :as string]
    [material-mdl])
  (:import [goog Promise]))

(def upgrade-component (fn [this]
                         (.upgradeElement js/componentHandler (v/dom-node this))))

(defview Menu
         [{[h-dir v-dir] :direction} & children]
         [:.mdl-menu__container.is-visible
          {:style (merge (case h-dir :right {:left 0}
                                     :left {:right 0}
                                     nil)
                         (case v-dir :up {:top 0}
                                     :bottom {:bottom 0}
                                     nil))}
          (into [:.mdl-menu.absolute.z-999.bg-white.shadow-2.flex.flex-column
                 {:style (merge (case h-dir :right {:left 0}

                                            :left {:right 0}
                                            nil)
                                (case v-dir :up {:bottom 0}
                                            :down {:top 0}
                                            nil))}
                 children])])

(defview MenuItem
         {:key (fn [{:keys [href primaryText]}] (or href primaryText))}
         [{:keys [primaryText rightIcon href disabled className view/props]}]
         [(if (and href (not disabled)) :a :div)
          (cond-> (merge (dissoc props :primaryText :rightIcon :disabled)
                         {:className (str "mdl-menu__item" (when disabled " gray") " " className)})
                  href (assoc :href href)
                  disabled (assoc :disabled true))
          primaryText])

(def MenuDivider
  [:.mdl-menu__item--full-bleed-divider])

(defview DropDown
         {:initial-state {:open? false}
          :did-mount     #(swap! (:view/state %) assoc :listeners [(events/listen js/window "click"
                                                                                  (fn [e]
                                                                                    (when-not (gdom/contains (v/dom-node %) (.-target e))
                                                                                      (swap! (:view/state %) assoc :open? false))))
                                                                   (routing/on-location-change
                                                                     (fn [] (swap! (:view/state %) assoc :open? false))
                                                                     false)])
          :will-unmount  #(doseq [k (:listeners @(:view/state %))]
                            (events/unlistenByKey k))}
         [{:keys [view/state]} target content]
         [:.dib.relative
          [:span {:onClick #(swap! state update :open? not)} target]
          (when (:open? @state) content)])


(defn Chip [{:keys [label
                    className
                    labelClassName
                    icon-className
                    onClick
                    onDelete] :as props}]
  [:div (merge {:className (str "br4 pv2 ph2 mt2 mr2 f65 dib inner-shadow bg-light-gray "
                                (when (or onClick onClick)
                                  " pointer")
                                " "
                                className)}
               (select-keys props [:style :onClick :onClick :key]))
   [:span {:className (str "ma1 " labelClassName)} label]
   (when onDelete
     (update icons/Cancel 1 merge
             {:className (str " o-60 hover-o-100 pointer mtn2 mbn2 mrn1 " icon-className)
              :onClick   onDelete}))])

(defview Avatar
         {:key :src}
         [{:keys [size style view/props] :as this
           :or   {size 40}}]
         [:img (-> props
                   (assoc :style (merge {:width        size
                                         :height       size
                                         :borderRadius "50%"}
                                        style)))])

(defn Divider [] [:hr.bt.mv2.b--light-gray {:style {:borderBottom "none"}}])

(def Subheader :.gray.f6.w-100.ph3.pt3.pb1)

(defview Paper
         [_ & children]
         (into [:.mdl-shadow--2dp.bg-white.pa3.br1] children))


(defn button-classes
  [{:keys [primary secondary className icon label href]} button-type]
  (str "mdl-button mdl-js-button "
       (when-not href " mdl-js-ripple-effect")
       (when primary " mdl-button--colored")
       (when (and icon (not label)) " ph0")
       (when secondary " mdl-button--accent")
       " mdl-button--" (some-> button-type (name))
       " " className))

(defn Header [& content]
  (into [:.flex.bg-blue.flex-none.white.items-center] content))

(defn HeaderButton [{:keys [href] :as props} icon]
  [(if href :a :div)
   (update props :className #(str " pointer pa3 hover-bg-darken-1 " %))
   icon])

(defview Button
         {:key       :label
          :did-mount upgrade-component}
         [{:keys [href onClick type label view/props icon disabled]}]
         (let [active? (and (not disabled)
                            (or href onClick))]
           [(if (and href active?) :a :button)
            (merge {:className (button-classes props type)}
                   (cond-> (dissoc props :className :type :primary :secondary :icon)
                           disabled (dissoc :href :onClick)))
            icon
            [:.dib.w0.o-0 (when (and icon (util/ensure-str label))
                            {:className "mr2"}) "&nbsp;"]
            label]))

(defview Input
         {:initial-state      #(get % :value "")

          :will-receive-props (fn [{{prev-value :value} :view/prev-props
                                    {value :value}      :view/props
                                    state               :view/state :as this}]
                                (when-not (= prev-value value)
                                  (reset! state value)))
          :did-mount          (fn [{:keys [autoFocus] :as this}]
                                (when (true? autoFocus)
                                  (v/focus this)))}
         [{:keys [view/props
                  view/state
                  element
                  onChange
                  mask
                  onKeyPress
                  autoFocus] :as this}]
         (when onKeyPress (println :input-has-keypress (:value props)))
         [element (cond-> (dissoc props :element)
                          (contains? props :value)
                          (merge
                            {:value      (or @state "")
                             :onKeyPress (fn [e]
                                           (when (and mask (= (mask (util/keypress-value e)) @state))
                                             (.preventDefault e))
                                           (when onKeyPress (onKeyPress e)))
                             :onChange   (fn [e]
                                           (binding [re-view.render-loop/*immediate-state-update* true]
                                             (let [target (.. e -target)
                                                   value (.. target -value)
                                                   new-value (cond-> value
                                                                     mask (mask))
                                                   cursor (.. target -selectionEnd)]
                                               (when mask (aset target "value" new-value))
                                               (when onChange (onChange e))
                                               (doto target
                                                 (aset "selectionStart" cursor)
                                                 (aset "selectionEnd" cursor)))))}))])
(defview ProgressIndeterminate
         {:did-mount upgrade-component}
         [{:keys [view/props]}]
         [:.mdl-progress.mdl-js-progress.mdl-progress__indeterminate (assoc props :dangerouslySetInnerHTML {:__html ""})])

(defview ProgressSpinner
         {:did-mount upgrade-component}
         [{:keys [view/props]}]
         [:.mdl-spinner.mdl-js-spinner.is-active.mdl-spinner--single-color (assoc props :dangerouslySetInnerHTML {:__html ""})])


(defview Text
         {:key           :name
          :reset         #(swap! (:view/state %) assoc :dirty? false)
          :initial-state {:dirty? false}}
         [{:keys [fullWidth
                  name
                  type
                  id
                  label
                  floatingLabel
                  expandable
                  multi-line
                  error
                  info
                  view/props
                  className
                  view/state
                  onKeyDown
                  value
                  onSave
                  input-style
                  inProgress
                  onFocus
                  onBlur] :as this}]
         (let [{:keys [focused?]} @state
               dirty? (or (:dirty? @state) (:dirty? props))
               floatingLabel  (or floatingLabel
                                  focused?
                                  (util/ensure-str value))]
           [:.mdl-textfield.mdl-js-textfield
            {:onBlur    #(do (swap! state assoc :dirty? true :focused? false)
                             (when onBlur (onBlur %)))
             :onFocus   #(do (swap! state assoc :focused? true)
                             (when onFocus (onFocus %)))
             :onKeyDown #(when (= 13 (.-which %))
                           (swap! state assoc :dirty? true))
             :className (str
                          (when floatingLabel " mdl-textfield--floating-label ")
                          (when multi-line " mdl-js-textfield")
                          (when fullWidth " w-100")
                          (when expandable " mdl-textfield--expandable")
                          (when focused? " is-focused")
                          (when (util/ensure-str value) " is-dirty")
                          (when (and (seq error) dirty?) " is-invalid")
                          " " className)}
            (Input
              (merge (dissoc props
                             :floatingLabel
                             :multi-line
                             :fullWidth
                             :expandable
                             :focused?
                             :dirty?
                             :text
                             :input-style
                             :info
                             :error
                             :onSave
                             :onKeyDown
                             :inProgress)
                     {:element   (if multi-line :textarea :input)
                      :className (str "mdl-textfield__input "
                                      (when fullWidth " w-100")
                                      )
                      :type      (or type "text")
                      :id        (or id name)
                      :style     input-style
                      :onKeyDown (fn [^js/React.SyntheticEvent e]
                                   (when (and onSave (#{"ctrl+S" "meta+S" "enter"} (util/keypress-action e)))
                                     (.preventDefault e)
                                     (onSave))
                                   (when onKeyDown (onKeyDown e)))}))
            [:label {:className "mdl-textfield__label"
                     :htmlFor   id}
             label]
            (when inProgress (ProgressIndeterminate {:className "absolute w-100"}))
            (or
              (when (seq error)
                (into [:div {:className "mdl-textfield__error"}] error))
              (when (seq info)
                (into [:.absolute.w-100.f6.mt1] info)))]))


(defn Submit [label]
  (Button
    {:type      :raised
     :primary   true
     :className "w-100 f4 mv3 pv2"
     :label     label
     :style     {:height "auto"}}))


(defview Checkbox
         {:key       :id
          :did-mount upgrade-component}
         [{:keys [id name checked disabled label
                  labelClassName
                  input-className
                  className view/props]}]
         [:label {:className (str "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect "
                                  className)
                  :htmlFor   id}
          [:input (cond-> (merge {:type      "checkbox"
                                  :id        id
                                  :name      name
                                  :className (str "mdl-checkbox__input mr2 " input-className)}
                                 (select-keys props [:value :defaultChecked]))
                          checked (assoc :checked true)
                          disabled (assoc :disabled true))]
          (when label [:span {:className (str "mdl-checkbox__label "
                                              labelClassName)} label])])

(defview RadioButton
         {:key        :id
          :did-mount  upgrade-component
          :did-update (fn [this]
                        (let [m (.. (v/dom-node this) -MaterialRadio)]
                          (if (:checked this) (.check m) (.uncheck m))))}
         [{:keys [label
                  className
                  labelClassName
                  input-className
                  checked
                  id
                  view/props]}]
         [:label {:className (str "mdl-radio mdl-js-radio mdl-js-ripple-effect pl4 "
                                  className)
                  :htmlFor   id}
          [:input (merge {:type      "radio"
                          :className (str "mdl-radio__button absolute left-0 "
                                          input-className)}
                         (cond-> (select-keys props [:name :id :value :defaultChecked])
                                 checked (assoc :defaultChecked "checked")))]
          [:span {:className (str "mdl-radio__label "
                                  labelClassName)} label]])

(defview List
         [{:keys [className view/props]} & items]
         (into [:div (merge props
                            {:className (str className " mdl-list")})]
               items))

(defview ListItem
         [{:keys [avatar title body href onClick className view/props]}]
         [(if href :a.db.no-underline.black :div)
          (merge {:className (str "pv2 ph3 f65 "
                                  (when (or href onClick)
                                    "pointer hover-bg-darken-1 ")
                                  className)}
                 (dissoc props :avatar :title :body :className))
          [:.flex
           avatar
           (when avatar [:.ml2])
           [:.flex-auto
            title
            [:.f65 body]]]])