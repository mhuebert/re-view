(ns re-view.react
  (:require [cljsjs.react]
            [cljsjs.react.dom]
            [goog.object :as gobj]))

(def Component js/React.Component)
(def findDOMNode js/ReactDOM.findDOMNode)
(def isValidElement js/React.isValidElement)
(def createElement js/React.createElement)
(def render js/ReactDOM.render)