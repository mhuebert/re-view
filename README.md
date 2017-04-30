# Re-View Hiccup

![badge](https://img.shields.io/clojars/v/re-view-hiccup.svg)

[Hiccup](https://github.com/weavejester/hiccup) is a representation of HTML in Clojure. `re-view hiccup` parses hiccup and returns React elements. Its goal is to be relatively small (~100 lines) and fast, and offer some additional flexibility (eg. the `:wrap-props` option) over alternatives. It should work fine in self-hosted ClojureScript.

---- 

New to Hiccup? Read the **[syntax guide](https://github.com/mhuebert/re-view/wiki/Hiccup-Syntax)**.

---

### Usage

`re-view-hiccup.core/element` accepts a `hiccup` vector and returns a React element. If a non-vector form is supplied, it is returned untouched. You may pass an options map with a `:wrap-props` function to be applied to all props maps during parsing. 

**Differences from React**

- You can use `:for` instead of `:html-for`, and `:class` instead of `:class-name`. You may also include a `:classes` key with a collection of classes, to be joined and concatenated. It is often convenient to manipulate classes as a set or vector.

**Things to know**

- Use dashed prop and style keys, eg. `:font-size`; keys are converted to `camelCase` as necessary (`data-` and `aria-` attributes remain hyphenated as required by React).
- Element names must be keywords, and support CSS selector syntax for adding IDs and classes. If no element name is provided, a `div` is returned. For example:
    `[:span#hello.red]` is equivalent to `[:span {:id "hello", :class "red"}]`
    `[:#hello]` is equivalent to `[:div {:id "hello"}]` 
- Anything in the second position of a hiccup vector that is not a Clojure `map` is passed as a child element.   
- `cljsjs.react` and `cljsjs.react.dom` namespaces are required, but not included. They must be provided separately. You can use any version of React you like. We only expect `React.createElement` to be in the global environment.
  
  It is not necessary to use the official `cljsjs` package. You can create your own bundle (eg. with webpack or rollup) and include it in `:foreign-libs`, as long as you specify that it provides `"cljsjs.react"` and `"cljsjs.react.dom"`. See [Packaging Foreign Dependencies](https://clojurescript.org/reference/packaging-foreign-deps).
