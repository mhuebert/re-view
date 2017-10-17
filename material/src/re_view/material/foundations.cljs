(ns re-view.material.foundations
  (:require ["@material/base" :as base]
            ["@material/checkbox" :as checkbox]
            ["@material/dialog" :as dialog]
            ["@material/drawer" :as drawer]
            ["@material/form-field" :as form-field]
            ["@material/grid-list" :as grid-list]
            ["@material/icon-toggle" :as icon-toggle]
            ["@material/menu" :as menu]
            ["@material/radio" :as radio]
            ["@material/ripple" :as ripple]
            ["@material/select" :as select]
            ["@material/snackbar" :as snackbar]
            ["@material/textfield" :as textfield]
            ["@material/toolbar" :as toolbar]))

(def foundations {"MDC" base/MDCFoundation
                  "MDCCheckbox" checkbox/MDCCheckboxFoundation
                  "MDCDialog" dialog/MDCDialogFoundation
                  "MDCPersistentDrawer" drawer/MDCPersistentDrawerFoundation
                  "MDCTemporaryDrawer" drawer/MDCTemporaryDrawerFoundation
                  "MDCFormField" form-field/MDCFormFieldFoundation
                  "MDCGridList" grid-list/MDCGridListFoundation
                  "MDCIconToggle" icon-toggle/MDCIconToggleFoundation
                  "MDCSimpleMenu" menu/MDCSimpleMenuFoundation
                  "MDCRadio" radio/MDCRadioFoundation
                  "MDCRipple" ripple/MDCRippleFoundation
                  "MDCSelect" select/MDCSelectFoundation
                  "MDCSnackbar" snackbar/MDCSnackbarFoundation
                  "MDCTextfield" textfield/MDCTextfieldFoundation
                  "MDCToolbar" toolbar/MDCToolbarFoundation})