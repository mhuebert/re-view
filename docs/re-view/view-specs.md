# view/spec

ALPHA

**defview** accepts a `:view/spec` key for specifying the props and children expected by a component. View specs are inspired by [Clojure Spec](https://clojure.org/about/spec), but do much less, and are used at runtime as well as during development.

## Purpose

1. Documentation - quickly learnr how to use a view by looking at its spec.
2. Debugging - get helpful error messages during development.
3. Runtime functionality - supports common patterns including prop defaults, and passing unhandled props to a child component.

## Usage

Include a `:view/spec` key in a component's method map, containing view specs for `:props` and `:children`.

```clj
(defview greeting
  {:view/spec {:props {:name :String}}}
  [this]
  [:div (str "Hello, " (get this :name) ".")])
```

A spec may be one of the following:

1. The keyword of a registered spec. The built-in specs are:
   ```clj
   :Boolean :String :Number :Function :Map :Vector :Element :Hiccup :SVG :Object :Keyword
   ```
2. A predicate function or set
3. A map containing a `:spec` key, which itself is a spec

Specs are registered using `re-view.view-spec/defspec`, which accepts a map of the form `{<keyword> <spec>}`.

```clj
(defspec ::label {:spec :String
                  :doc "Label for a form input"})
```


## The :view/spec map

The :view/spec map accepts the following keys:

| key | info |
| :props | A map of the form `{<prop-key> <spec>}` |
| :props/keys | A vector of registered spec keys |
| :children | A vector of specs, aligned to the child args accepted by the component |

## Props

```clj
;; spec can be a map with a :spec key and other options.
{:view/spec {:props {:name {:spec :String
                            :default "random traveller"
                            :doc "Name of person to greet"}}}}

;; spec can be the keyword of a registered spec
{:view/spec {:props {:name :String}}}

;; use the custom ::label spec by including it in the :prop/keys vector.
;; qualified keywords specified here will match  the _unqualified_ key in the props map, eg. `:label`.
{:view/spec {:props/keys [::label]}}

;; use the custom ::label in a :props map, and assign it to a different prop key (other than :label)
{:view/spec {:props {:input-label ::label}}}
```

## Children

Children are spec'd by supplying a vector of specs, aligned with the argslist of the component. The `:&` key may be used like `&` in an argslist to match any number of trailing children.


```clj
;; spec for a single :Element child
{:view/spec {:children [:Element]}}

;; spec for two children, a :Number and :Element
{:view/spec {:children [:Number :Element]}}

;; spec for any number of :Element children
{:view/spec {:children [:& :Element]}}

;; spec for a :Number followed by any number of :Element children
{:view/spec {:children [:Number :& :Element]}}
```

## A spec map

Specs may be supplied as maps, with any of the following keys (only :spec is required):

| key | info |
|---|---|---|
| `:spec` | A predicate function, set, or keyword of an already-defined spec |
| `:doc` (string) | A **docstring** for each prop  |
| `:default` (any) | Supply defaults values for props |
| `:required` (boolean) | Props are considered optional unless the :required key is true. |
| `:pass-through` (boolean) | Mark as `true` if the prop should be passed to a child view in the return value of `re-view.core/pass-props` |
| `:validate` (function) | Should return true if value is valid |

**Why not just use Clojure Spec?**

1. Clojure Spec is a powerful _general-purpose_ library which would add a lot to ClojureScript bundle sizes if it was included (`re-view.view-spec` is only ~100 LOC).
2. It has no support for docstrings, which are a key benefit of view specs.
3. It does not support inline spec'ing of individual one-off map keys, which are common in UI views.
4. It handles unqualified keywords as the exceptional case, but when working with Hiccup / React views most keys are unqualified.

Generally, a custom implementation means we get exactly the functionality we want with a small amount of code.

That said, you could use Clojure Spec _with_ re-view without problems. `defview` returns a function which could be instrumented, for example.