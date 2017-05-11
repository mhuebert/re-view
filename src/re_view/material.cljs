(ns re-view.material
  (:refer-clojure :exclude [List])
  (:require
    [goog.net.XhrIo :as xhr]
    [goog.events :as events]
    [goog.dom :as gdom]
    [goog.style :as gstyle]
    [goog.object :as gobj]
    [re-view.core :as v :refer [defview]]
    [re-view.material.icons :as icons]
    [re-view.material.util :as util]
    [clojure.string :as string]
    [re-view.routing :as routing]
    [re-view.core :as v]
    [re-view.material.mdc :as mdc]
    [re-view.material.ext :as ext])
  (:import [goog Promise]))

;; TODO
;; MDC Dialog
;; MDC Snackbar



;(defn Chip [{:keys [label
;                    class
;                    label-class
;                    icon-class
;                    on-click
;                    onDelete] :as props}]
;  [:div (merge {:class (str "br4 pv2 ph2 mt2 mr2 f65 dib inner-shadow bg-light-gray "
;                                 (when (or on-click on-click)
;                                  " pointer")
;                                 " "
;                                 class)}
;               (select-keys props [:style :on-click :on-click :key]))
;   [:span {:class (str "ma1 " label-class)} label]
;   (when onDelete
;     (update icons/Cancel 1 merge
;             {:class (str " o-60 hover-o-100 pointer mtn2 mbn2 mrn1 " icon-class)
;              :on-click   onDelete}))])
;
;(defview Avatar
;         {:key :src}
;         [{:keys [size style view/props] :as this
;           :or   {size 40}}]
;         [:img (-> props
;                   (assoc :style (merge {:width         size
;                                         :height        size
;                                         :border-radius "50%"}
;                                        style)))])
;
;(defn Divider [] [:hr.bt.mv2.b--light-gray {:style {:border-bottom "none"}}])
;
;(def Subheader :.gray.f6.w-100.ph3.pt3.pb1)
;
;(defview Paper
;         [_ & children]
;         (into [:.mdl-shadow--2dp.bg-white.pa3.br1] children))
;
;(defn Header [& content]
;  (into [:.flex.bg-blue.flex-none.white.items-center] content))
;
;(defn HeaderButton [{:keys [href] :as props} icon]
;  [(if href :a :div)
;   (update props :class #(str " pointer pa3 hover-bg-darken-1 " %))
;   icon])
;
;(defview ProgressIndeterminate
;         "ProgressIndeterminate..."
;         {:did-mount upgrade-component}
;         [{:keys [view/props]}]
;         [:.mdl-progress.mdl-js-progress.mdl-progress__indeterminate (assoc props :dangerouslySetInnerHTML {:__html ""})])
;
;(defview ProgressSpinner
;         "ProgressSpinner..."
;         {:did-mount upgrade-component}
;         [{:keys [view/props]}]
;         [:.mdl-spinner.mdl-js-spinner.is-active.mdl-spinner--single-color (assoc props :dangerouslySetInnerHTML {:__html ""})])
;
;(defview RadioButton
;         "RadioButton..."
;         {:key        :id
;          :did-mount  upgrade-component
;          :did-update (fn [this]
;                        (let [m (.. (v/dom-node this) -MaterialRadio)]
;                          (if (:checked this) (.check m) (.uncheck m))))}
;         [{:keys [label
;                  class
;                  label-class
;                  input-class
;                  checked
;                  id
;                  view/props]}]
;         [:label {:class (str "mdl-radio mdl-js-radio mdl-js-ripple-effect pl4 "
;                              class)
;                  :html-for   id}
;          [:input (merge {:type       "radio"
;                          :class (str "mdl-radio__button absolute left-0 "
;                                      input-class)}
;                         (cond-> (select-keys props [:name :id :value :default-checked])
;                                 checked (assoc :default-checked "checked")))]
;          [:span {:class (str "mdl-radio__label "
;                              label-class)} label]])
;
;


(defview Select
  "Select..."
  {:key          (fn [{:keys [id name]}] (or id name))
   :did-mount    (fn [this] (mdc/init this mdc/Select))
   :will-unmount (fn [this] (mdc/destroy this mdc/Select))}
  [{:keys [label]}]
  [:.mdc-select
   {:role      "listbox"
    :tab-index 0}
   [:span.mdc-select__selected-text label]
   [:.mdc-simple-menu.mdc-select__menu
    ]])



(def ^:private ButtonProps [:type :primary :icon-end :accent :icon :raised :compact :dense])
(defview Button
  "Communicates the action that will occur when the user touches it. [More](https://material.io/guidelines/components/buttons.html)"
  {:key          :label
   :did-mount    (fn [{:keys [ripple]
                       :or   {ripple true} :as this}]
                   (when ripple (mdc/init this mdc/Ripple)))
   :will-unmount (fn [{:keys [ripple]
                       :or   {ripple true} :as this}]
                   (when ripple (mdc/destroy this mdc/Ripple)))
   :did-update   (util/mdc-style-update :Ripple)}
  [{:keys [view/props
           view/state
           href
           on-click
           label
           icon
           disabled
           dense
           raised
           compact
           accent
           primary
           icon-end
           id
           ripple]
    :or   {ripple true}}]
  (when (some #{:secondary :type} (set (keys props)))
    (throw "Depracated :secondary, :type"))
  (let [{:keys [mdc/Ripple-classes]} @state]
    [(if (and (not disabled)
              (or href on-click)) :a :button)
     (-> (apply dissoc props ButtonProps)
         (cond-> disabled (dissoc :href :on-click))
         (update :style merge (when (and icon (not label))
                                {:padding-left  0
                                 :padding-right 0}))
         (update :classes into (into Ripple-classes
                                     ["mdc-button flex items-center"
                                      (when ripple "mdc-ripple-target")
                                      (when dense "mdc-button--dense")
                                      (when raised "mdc-button--raised")
                                      (when compact "mdc-button--compact")
                                      (when accent "mdc-button--accent")
                                      (when primary "mdc-button--primary")])))
     (when icon
       (cond-> (icons/styles icon :margin-right "0.5rem")
               dense (icons/size 20)))
     (when-let [label (util/ensure-str label)]
       label)
     (when icon-end
       (cond-> (icons/styles icon-end :margin-left "0.5rem")
               dense (icons/size 20)))]))

(defview Dialog
  {:initial-state {:mdc/styles {"visibility" "hidden"}}
   :did-mount     (v/compseq #(mdc/init % mdc/Dialog)
                             ;; update Dialog styles with initial-state.
                             (util/mdc-style-update :Dialog))
   :will-unmount  #(mdc/destroy % mdc/Dialog)
   :open          (fn [this]
                    (.open (gobj/get this "mdcDialog")))
   :close         (fn [this]
                    (.close (gobj/get this "mdcDialog")))
   :did-update    (util/mdc-style-update :Dialog)}
  [{:keys [label/accept
           label/cancel
           view/state
           scrollable?
           content/header]
    :or   {accept "OK"
           cancel "Cancel"}} & body]
  [:aside#mdc-dialog.mdc-dialog
   {:classes          (:mdc/Dialog-classes @state)
    :role             "alertdialog"
    :aria-hidden      "true"
    :aria-labelledby  "mdc-dialog-label"
    :aria-describedby "mdc-dialog-body"}
   [:.mdc-dialog__surface
    (some->> header (conj [:header.mdc-dialog__header]))
    (into [:section#mdc-dialog-body.mdc-dialog__body
           {:class (when scrollable? "mdc-dialog__body--scrollable")}]
          body)
    [:footer.mdc-dialog__footer
     (Button {:classes ["mdc-dialog__footer__button"
                        "mdc-dialog__footer__button--cancel"]
              :label   cancel})
     (Button {:classes ["mdc-dialog__footer__button"
                        "mdc-dialog__footer__button--accept"]
              :primary true
              :label   accept})]]
   [:.mdc-dialog__backdrop]])

(def DialogWithTrigger (ext/with-trigger Dialog))

(defview Input
  {:initial-state      #(get % :value)

   :will-receive-props (fn [{{prev-value :value} :view/prev-props
                             {value :value}      :view/props
                             state               :view/state :as this}]
                         (when-not (= prev-value value)
                           (reset! state value)))
   :did-mount          (fn [{:keys [auto-focus] :as this}]
                         (when (true? auto-focus) (v/focus this)))}
  [{:keys [view/props
           view/state
           element
           on-change
           mask
           on-key-press
           auto-focus
           class] :as this
    :or {element :input}}]
  [element (cond-> (-> (dissoc props :element)
                       (update :classes conj "outline-0"))
                   (contains? props :value)
                   (merge
                     {:value        (or @state "")
                      :on-key-press (fn [e]
                                      (when (and mask (= (mask (util/keypress-value e)) @state))
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

(defview NativeSelect
  "Native select element"
  [{:keys [view/props]} & items]
  (into [:select.mdc-select props] items))

(defview Ripple
  "Applies ripple effect to a single child view."
  {:key           (fn [_ element]
                    (or (get-in element [1 :key])
                        (get-in element [1 :id])))
   :did-mount     #(mdc/init % mdc/Ripple)
   :will-unmount  #(mdc/destroy % mdc/Ripple)
   :did-update    (v/compseq (util/mdc-style-update :Ripple)
                             (util/mdc-classes-update :Ripple))
   :should-update #(do true)}
  [{:keys       [view/state]
    [component] :view/children}]
  (v/update-attrs component update :classes into (:mdc/Ripple-classes @state)))


(def ^:private TextProps [:floating-label
                          :dense
                          :multi-line
                          :full-width
                          :expandable
                          :focused?
                          :dirty?
                          :text
                          :input-style
                          :hint
                          :error
                          :info
                          :on-save
                          :in-progress
                          :help-text-persistent
                          :container-class])

(defview Text
  "Allow users to input, edit, and select text. [More](https://material.io/guidelines/components/text-fields.html)"
  {:key           :name
   :reset         #(swap! (:view/state %) assoc :dirty? false)
   :initial-state {:dirty?                false
                   :mdc/Textfield-classes #{"mdc-textfield--upgraded"}
                   :mdc/label-classes     #{"mdc-textfield__label"}
                   :mdc/helpText-classes  #{"mdc-textfield-helptext"}}
   :did-mount     (fn [this]
                    (mdc/init this mdc/Textfield))
   :will-unmount  (fn [this]
                    (mdc/destroy this mdc/Textfield))}
  [{:keys [full-width
           name
           type
           id
           label
           expandable
           multi-line
           error
           info
           view/props
           class
           view/state
           value
           on-save
           input-style
           in-progress
           default-value
           on-focus
           on-blur
           on-change
           help-text-persistent
           container-class] :as this
    :or {type "text"}}]
  (let [{:keys [focused?
                mdc/Textfield-classes
                mdc/label-classes
                mdc/helpText-classes
                mdc/helpText-attrs]} @state
        dirty? (or (:dirty? @state) (:dirty? props))

        field-id (or id name)]
    [:div
     {:class container-class}
     [:.mdc-textfield
      {:classes (into Textfield-classes
                      [(when multi-line "mdc-textfield--multiline")
                       (when (:disabled props) "mdc-textfield--disabled")
                       (when full-width "mdc-textfield--fullwidth")
                       (when (:dense props) "mdc-textfield--dense")])}
      (Input
        (-> (apply dissoc props TextProps)
            (cond-> full-width (assoc :aria-label label))
            (merge {:element       (if multi-line :textarea :input)
                    :class         "w-100 mdc-textfield__input"
                    :aria-controls (str field-id "-helpText")
                    :id            field-id
                    :style         input-style}
                   (util/collect-handlers props {:on-key-down (util/handle-on-save on-save)}))))
      (when (and (not full-width) (util/ensure-str label))
        (let [floatingLabel (boolean (or (:floating-label props)
                                         (util/ensure-str value)
                                         (util/ensure-str default-value)
                                         (util/ensure-str (:placeholder props))))]
          [:label {:class    (cond-> (string/join " " label-classes)
                                     floatingLabel (str " mdc-textfield__label--float-above"))
                   :html-for field-id}
           label]))
      #_(when in-progress (ProgressIndeterminate {:class "absolute w-100"}))]

     (let [error (seq (util/collect-text (:error props)))
           info (seq (util/collect-text (:info props)))
           hint (seq (util/collect-text (:hint props)))]
       [:div (merge {:class (cond-> (string/join " " helpText-classes)
                                    (or help-text-persistent
                                        error
                                        info) (str " mdc-textfield-helptext--persistent"))
                     :id    (str field-id "-helpText")}
                    helpText-attrs)
        (some->> error (conj [:.dark-red.pb1]))
        (some->> info (conj [:.black.pb1]))
        hint])]))


(defn Submit [label]
  (Button
    {:type    :raised
     :primary true
     :class   "w-100 f4 mv3 pv2"
     :label   label
     :style   {:height "auto"}}))


(def ^:private ListProps [:dense :avatars :ripples])
(defview List
  "Presents multiple line items vertically as a single continuous element. [More](https://material.io/guidelines/components/lists.html)"
  [{:keys [class dense avatars view/props]} & items]
  (into [:div (-> (apply dissoc props ListProps)
                  (assoc :class (cond-> (str class " mdc-list")
                                        dense (str " mdc-list--dense")
                                        avatars (str " mdc-list--avatar-list"))))] items))

(def ^:private ListItemProps [:text/primary :text/secondary :detail/start :detail/end :ripple])
(defview ListItem
  "Collections of list items present related content in a consistent format. [More](https://material.io/guidelines/components/lists.html#lists-behavior)"
  {:key (fn [{:keys [href text/primary]}] (or href primary))}
  [{:keys [text/primary
           text/secondary
           detail/start
           detail/end
           href
           on-click
           class
           view/props
           style
           ripple]}]
  (when-let [dep (some #{:title :body :avatar} (set (keys props)))]
    (throw (js/Error. "Deprecated " dep "... title, body - primary/text-secondary, avatar - detail-start or detail-end")))
  (cond-> [(if href :a.no-underline.black :div)
           (-> (apply dissoc props ListItemProps)

               ;; tachyons fix (box-sizing)
               (update :style assoc :box-sizing "content-box")

               (assoc :class (cond-> (str "mdc-list-item " class)
                                     ripple (str " mdc-ripple-target")
                                     (or href on-click) (str " pointer "))))
           (some->> start (conj [:.mdc-list-item__start-detail]))
           [:.mdc-list-item__text
            primary
            [:.mdc-list-item__text__secondary secondary]]
           (some->> end (conj [:.mdc-list-item__end-detail]))]
          ripple (Ripple)))

(defn ListDivider []
  [:.mdc-list-divider {:role "presentation"}])

(defn ListGroup [& content]
  (into [:.mdc-list-group] content))

(defn ListGroupSubheader [content]
  [:.mdc-list-group__subheader content])


(def ^:private SimpleMenuProps [:on-cancel :on-selected])

(defview SimpleMenu
  "Menus appear above all other in-app UI elements, and appear on top of the triggering element. [More](https://material.io/guidelines/components/menus.html#menus-behavior)"
  {:key          :id
   :did-mount    (fn [this] (mdc/init this mdc/SimpleMenu))
   :will-unmount (fn [this] (mdc/destroy this mdc/SimpleMenu))
   :open         (fn [this] (.open (gobj/get this "mdcSimpleMenu")))
   :did-update   (v/compseq (util/mdc-style-update :SimpleMenu :mdc/styles-inner "menuItemContainer")
                            (util/mdc-style-update :SimpleMenu))}
  [{:keys [view/state view/props classes]} & items]
  [:.mdc-simple-menu (merge (apply dissoc props SimpleMenuProps)
                            {:tab-index -1
                             :classes   (into classes (:mdc/SimpleMenu-classes @state))})
   (into [:.mdc-simple-menu__items.mdc-list
          {:role        "menu"
           :aria-hidden true}]
         items)])

(def SimpleMenuWithTrigger (ext/with-trigger SimpleMenu {:container-classes ["mdc-menu-anchor"]}))

(v/defpartial SimpleMenuItem
              ListItem
              {:role      "menuitem"
               :tab-index 0})

(def SimpleMenuItem (v/partial ListItem
                               {:role      "menuitem"
                                :tab-index 0}))


(defn- formField-attrs [{:keys [mdc/FormField-classes align-end rtl classes class]}]
  {:classes (cond-> (into classes FormField-classes)
                    align-end (conj "mdc-form-field--align-end"))
   :class   class
   :dir     (when rtl "rtl")})


(def ^:private SwitchProps [:label :rtl :align-end :label-class :input-class :compact :dense])
(defview Switch
  "Allow a selection to be turned on or off. [More](https://material.io/guidelines/components/selection-controls.html#selection-controls-radio-button)"
  {:key :id}
  [{:keys [disabled id rtl label view/props]}]
  [:.mdc-switch
   {:class (when disabled " mdc-switch--disabled")
    :dir   (when rtl "rtl")}
   [:input.mdc-switch__native-control (merge {:type "checkbox"}
                                             (apply dissoc props SwitchProps))]
   [:.mdc-switch__background [:.mdc-switch__knob]]])

(defview SwitchField
  "Allow a selection to be turned on or off. [More](https://material.io/guidelines/components/selection-controls.html#selection-controls-radio-button)"
  {:key :id}
  [{:keys [disabled id label view/props]}]
  [:.mdc-form-field
   (formField-attrs props)
   [:.mdc-switch
    ;; TODO remove tachyons
    {:class (str "mh2" (when disabled " mdc-switch--disabled"))}
    [:input.mdc-switch__native-control (merge {:type "checkbox"}
                                              (apply dissoc props SwitchProps))]
    [:.mdc-switch__background [:.mdc-switch__knob]]]
   (when label [:label.pl2 {:html-for id} label])])



(defview Checkbox
  "Allow the selection of multiple options from a set. [More](https://material.io/guidelines/components/selection-controls.html#)"
  {:key          :id
   :did-mount    #(mdc/init % mdc/Ripple mdc/Checkbox mdc/FormField)
   :will-unmount #(mdc/destroy % mdc/Ripple mdc/Checkbox mdc/FormField)
   :did-update   (util/mdc-style-update :Ripple)}
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
      [:input.mdc-checkbox__native-control (-> (apply dissoc props SwitchProps)
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
           (v/update-attrs #(-> %
                                (assoc :html-for (or id name))
                                (update :classes conj label-class)))))]))

#_(defview FormField
    {:key          (fn [_ _ {:keys [id]} _] id)
     :did-mount    #(mdc/init % mdc/FormField)
     :will-unmount #(mdc/destroy % mdc/FormField)}
    [{:keys [rtl align-end]} field label]
    [:.mdc-form-field
     {:class (when align-end "mdc-form-field--align-end")
      :dir   (when rtl "rtl")}
     field
     (v/update-attrs label assoc :html-for (v/element-get field :id))])

#_(defn CheckboxField [{:keys [id rtl label align-end] :as props}]
    (FormField (select-keys props [:rtl :align-end])
               (Checkbox (dissoc props :rtl :align-end :label))
               [:label label])
    #_[:.mdc-form-field
       {:class (when align-end "mdc-form-field--align-end")
        :dir   (when rtl "rtl")}
       (Checkbox (dissoc props :label :rtl :align-end))
       (when label [:label {:html-for id} label])])
(def PermanentDrawerToolbarSpacer [:.mdc-permanent-drawer__toolbar-spacer])
(defview PermanentDrawer
  "Permanent navigation drawers are always visible and pinned to the left edge, at the same elevation as the content or background. They cannot be closed. The recommended default for desktop. [More](https://material.io/guidelines/patterns/navigation-drawer.html#navigation-drawer-behavior)"
  [{:keys [view/props]} & content]
  [:.mdc-permanent-drawer
   props
   (into [:.mdc-permanent-drawer__content] content)])

(defn TemporaryDrawerHeader [& content]
  [:.mdc-temporary-drawer__header
   (into [:.mdc-temporary-drawer__header-content] content)])

;; must implement: https://github.com/material-components/material-components-web/tree/046e3373098a57315a6e7327cd72865c6817d013/packages/mdc-drawer
(defview TemporaryDrawer
  "Slides in from the left and contains the navigation destinations for your app. [More](https://material.io/guidelines/patterns/navigation-drawer.html)"
  {:did-mount          (fn [{:keys [open? view/state] :as this}]
                         (mdc/init this mdc/TemporaryDrawer)
                         (swap! state assoc :route-listener (routing/listen (aget this "close") {:fire-now? false}))
                         (when open? (.open this)))
   :foundation         (fn [this]
                         (let [^js/mdc.Foundation foundation (gobj/get this "mdcTemporaryDrawer")]
                           foundation))
   :will-receive-props (fn [{open? :open? {prev-open? :open?} :view/prev-props :as this}]
                         (cond (and open? (not prev-open?)) (.open this)
                               (and prev-open? (not open?)) (.close this)))
   :will-unmount       #(do (mdc/destroy % mdc/TemporaryDrawer)
                            (routing/unlisten (:route-listener @(:view/state %))))
   :open               (fn [this] (.open (.foundation this)))
   :close              (fn [this] (.close (.foundation this)))
   :notifyOpen         (fn [{:keys [onOpen]}] (when onOpen (onOpen)))
   :notifyClose        (fn [{:keys [onClose]}] (when onClose (onClose)))}
  [{:keys      [toolbar-spacer? header-content view/state]
    list-props :view/props
    :as        this} & list-items]
  [:.mdc-temporary-drawer
   {:class (string/join " " (:mdc/TemporaryDrawer-classes @state))}
   [:.mdc-temporary-drawer__drawer
    (when header-content
      [:.mdc-temporary-drawer__header
       [:.mdc-temporary-drawer__header-content header-content]])
    (apply List (-> (dissoc list-props :open? :onOpen :onClose)
                    (update :class str " mdc-temporary-drawer__content")
                    (dissoc :toolbar-spacer? :header-content)) list-items)]])

(def TemporaryDrawerWithTrigger (ext/with-trigger TemporaryDrawer))


;; MDC select element needs work --  arrow does not line up.
;; wait until it has improved to implement.


;(def SelectItem (v/partial ListItem {:role      "option"
;                                     :tab-index 0}))
;(def ^:private SelectProps [:on-change])
;(defview Select
;  {:did-mount        #(mdc/init % mdc/Select)
;   :will-unmount     #(mdc/destroy % mdc/Select)
;   :getValue         (fn [this] (.getValue (gobj/get this "mdcSelect")))
;   :getSelectedIndex (fn [this] (.getSelectedIndex (gobj/get this "mdcSelect")))
;   :setSelectedIndex (fn [this] (.setSelectedIndex (gobj/get this "mdcSelect")))
;   :isDisabled       (fn [this] (.isDisabled (gobj/get this "mdcSelect")))
;   :setDisabled      (fn [this] (.setDisabled (gobj/get this "mdcSelect")))
;
;   :did-update       (fn [this]
;                       (.resize (gobj/get this "mdcSelect")))}
;  [{:keys [mdc/Select-classes view/props]}]
;  [:.mdc-select
;   (-> {:role      "listbox"
;        :tab-index 0}
;       (merge (apply dissoc props SelectProps))
;       (update :classes into Select-classes))
;   [:span.mdc-select__selected-text "Pick..."]
;   (SimpleMenu {:class "mdc-select__menu"}
;               )])

