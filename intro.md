_Re-View_ is a library for building [React](https://facebook.github.io/react/) apps in ClojureScript. It is a beginner-friendly tool that is also suitable for demanding, production-grade apps.

## Benefits

- Precise and transparent mechanisms for reactivity
- Convenient access to React lifecycle methods
- A smooth 'upgrade continuum': simple components are extremely simple to create, while 'advanced' components are created by progressively adding information to simple components (no need to switch paradigms along the way)

## Motivation

Existing tools in the ClojureScript ecosystem, although excellent for their respective use cases, were found to be either too magical or too verbose for my particular needs. Re-View was originally "programmed in anger" (but with lotsa love) during the development of a [reactive-dataflow coding environment](http://px16.matt.is/). 

## How to start

Add the following dependencies to your project.clj or boot file:

```clj
[re-view "0.3.16"]
[cljsjs/react "15.5.0-0"]
[cljsjs/react-dom "15.5.0-0"]
```

> Re-View requires, but does not include, `cljsjs/react` and `cljsjs/react-dom`. This is so you can use whatever version of React you want.

Require the core namespace like so:

```clj
(ns app.core
  (:require [re-view.core :as v :refer [defview]]))
```

`defview`, similar to Clojure's `defn`, is how we create views. The first argument to a view is always its React component.

```clj
(defview greeting [this]
  [:div "Hello, world!"])
```

(Note the [hiccup syntax](/docs/hiccup/syntax-guide).)

When called, views return React elements that can be rendered to the page using the `render-to-dom` function.

```clj
(v/render-to-dom (greeting) "some-element-id")
```

Every component is assigned an atom, under the key `:view/state` on the component. This is for local state.

> React components are upgraded to behave kind of like Clojure maps: we can  `get` internal data by using keywords on the component itself, eg. `(:view/state this)`. 

```clj
(defview counter [this]
  [:div 
    {:on-click #(swap! (:view/state this) inc)}
    "Count: " @(:view/state this)])
```

When a component's state atom changes, the component is re-rendered -- exactly like `setState` in React.

If you pass a Clojure map as the first argument to a view, it is considered the component's 'props'.

```clj
;; pass a props map to a view
(greeting {:name "Herbert"})
```

You can `get` props by key directly on the component, eg. `(:name this)`.

```clj
(defview greeting [this]
  [:div "Hello, " (:name this)])
  
(greeting {:name "Herbert"})
;; => <div>Hello, Herbert</div>
```

(You can get the props map itself via the `:view/props` key, eg. `(:view/props this)`)

React [lifecycle methods](/docs/re-view/getting-started#__lifecycle-methods) can be included in a map before the argument list.

```clj
(defview focused-input
  {:life/did-mount (fn [this] (.focus (v/dom-node this)))}
  [this]
  [:input (:view/props this)])
                 
(focused-input {:placeholder "Email"})
```

See the [Getting Started](/docs/re-view/getting-started) guide for more.


