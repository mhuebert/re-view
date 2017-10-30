(ns re-view.material.core
  (:refer-clojure :exclude [List])
  (:require
    [goog.object :as gobj]
    [re-view.core :as v :refer [defview]]
    [re-view.material.util :as util]
    [clojure.string :as string]
    [re-view.routing :as routing]
    [re-view.core :as v]
    [re-view.util :refer [update-attrs]]
    [re-view.material.mdc :as mdc]
    [re-view.material.ext :as ext]

    [re-view.material.components.button :as button]
    [re-view.material.components.checkbox :as checkbox]
    [re-view.material.components.dialog :as dialog]
    [re-view.material.components.select :as select]
    [re-view.material.components.text :as text]
    [re-view.material.components.list :as list-views]
    [re-view.material.components.menu :as menu]
    [re-view.material.components.switch :as switch]
    [re-view.material.components.drawer :as drawer]
    [re-view.material.components.toolbar :as toolbar]
    [re-view.material.components.ripple :as ripple])
  (:import [goog Promise]))




(def Button button/Button)
(def Submit button/Submit)

(def Checkbox checkbox/Checkbox)

(def Dialog dialog/Dialog)
(def DialogWithTrigger dialog/DialogWithTrigger)

(def List list-views/List)
(def ListItem list-views/ListItem)
(def ListDivider list-views/ListDivider)
(def ListGroup list-views/ListGroup)
(def ListGroupSubheader list-views/ListGroupSubheader)

(def Text text/Text)
(def Input text/Input)

(def Ripple ripple/Ripple)

(def Select select/Select)

(def SimpleMenu menu/SimpleMenu)
(def SimpleMenuWithTrigger menu/SimpleMenuWithTrigger)
(def SimpleMenuItem menu/SimpleMenuItem)

(def Switch switch/Switch)
(def SwitchField switch/SwitchField)


(def PermanentDrawerToolbarSpacer drawer/PermanentDrawerToolbarSpacer)
(def PermanentDrawer drawer/PermanentDrawer)
(def TemporaryDrawerHeader drawer/TemporaryDrawerHeader)
(def TemporaryDrawer drawer/TemporaryDrawer)
(def TemporaryDrawerWithTrigger drawer/TemporaryDrawerWithTrigger)


(def Toolbar toolbar/Toolbar)
(def ToolbarWithContent toolbar/ToolbarWithContent)
(def ToolbarSection toolbar/ToolbarSection)
(def ToolbarTitle toolbar/ToolbarTitle)
(def ToolbarRow toolbar/ToolbarRow)
