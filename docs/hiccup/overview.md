# Hiccup

If you're new to hiccup, check out the **[syntax guide](syntax-guide)**.

----

# API

**`re-view.hiccup.core/element`** accepts a `hiccup` vector and returns a React element. If a non-vector form is supplied, it is returned untouched. You may pass an options map with a `:wrap-props` function to be applied to all props maps during parsing.

**Example:**

```clj
(ns my-app.core 
  (:require [re-view.hiccup.core :as hiccup]))

(hiccup/element [:div {:style {:color "red"}} "Hello, world!"])
```


# Syntax Notes

### Attributes

Unlike React, we support `:for` and `:class` attributes, instead of `:html-for`, and `:className`. This is to be more consistent with HTML syntax (vs. the javascript DOM api).

The `:classes` attribute accepts a collection (eg. a vector or set) of classes, which are joined and concatenated to other supplied classes.

Use dashed prop and style keys, eg. `:font-size`; keys are converted to `camelCase` as necessary (`data-` and `aria-` attributes remain hyphenated as required by React).

### Element names

Element names must be keywords, and support CSS selector syntax for adding IDs and classes. If no element name is provided, a `div` is returned. For example:

`[:span#hello.red]` and `[:span {:id "hello", :class "red"}]` are equivalent

`[:#hello]` and `[:div {:id "hello"}]` are equivalent

Anything in the second position of a hiccup vector that is not a Clojure `map` is passed as a child element.   

# React dependency

`cljsjs.react` and `cljsjs.react.dom` namespaces are required, but not included. They must be provided separately. You can use any version of React you like. We only expect `React.createElement` to be in the global environment.
  
It is not necessary to use the official `cljsjs` package. You can create your own bundle (eg. with webpack or rollup) and include it in `:foreign-libs`, as long as you specify that it provides `"cljsjs.react"` and `"cljsjs.react.dom"`. See [Packaging Foreign Dependencies](https://clojurescript.org/reference/packaging-foreign-deps).
