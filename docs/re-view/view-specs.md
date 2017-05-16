# view/spec

ALPHA

**defview** accepts a `:view/spec` key for specifying the props accepted by a component. View specs are somewhat inspired by [Clojure Spec](https://clojure.org/about/spec), but do much less, and are used at runtime to support common development needs.

## Purpose

1. Clarity / documentation - quickly identify how to use a view by looking at its spec.
2. Cleaner code - support common development needs like supplying defaults for props, and passing unused props down to a child component.
3. Better debugging - get helpful error messages during development.

## Usage

Include ``:view/spec` in a component's method map.

```clj
(defview greeting
  {:view/spec {:props {:name {:spec :String
                              :default "random traveller"
                              :doc "Name of person to greet"}}}}
  [this]
  [:div (str "Hello, " (get this :name) ".")])
```
A prop's spec map may include the following keys:

| key | info |
|---|---|---|
| `:spec` | A predicate function, set, or keyword of an already-defined spec |
| `:doc` (string) | A **docstring** for each prop  |
| `:default` (any) | Supply defaults values for props |
| `:pass-through` (boolean) | Mark as `true` if the prop should be passed to a child view in the return value of `re-view.core/pass-props` |
| `:validate` (function) | Should return true if value is valid |

## Base specs

```clj

:Boolean
:String
:Number
:Enum
:Function
:StyleMap
:Vector
:Hiccup
:Element

```