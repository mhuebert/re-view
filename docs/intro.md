_Re-View_ is a beginner-friendly, production-strength library for building [React](https://facebook.github.io/react/) apps in ClojureScript. (See [demo](/components).)

## Objectives

- Readable code
- Precise and transparent reactivity/dataflow
- Convenient access to React lifecycle methods - do not try to hide the React component model
- A smooth upgrade curve: simple components are extremely easy to create, and become 'advanced' by progressively adding information along the way (no need to switch between different 'modes' or 'forms').

## Motivation

Existing tools in the ClojureScript ecosystem, although excellent for their respective use cases, were found to be either too magical or too verbose for my particular needs. Re-View was originally "programmed in anger" (but with lotsa love) during the development of a [reactive-dataflow coding environment](http://px16.matt.is/), and powers the online ClojureScript playground [Maria](https://www.maria.cloud).

## Quickstart

Quickly create a new project from the command line using the lein template:

```bash
lein new re-view my-great-app;
cd my-great-app;
npm install;
npm run watch;
```

This will install [shadow-cljs](https://github.com/thheller/shadow-cljs/) (the recommended tool for compiling ClojureScript projects), start a web server running at http://localhost:8700, and automatically update the page when you make edits to the source files.

## Basic usage

Require the core namespace like so:

```clj
(ns app.core
  (:require [re-view.core :as v]))
```

`defview`, similar to Clojure's `defn`, is how we create views. The first argument to a view is always its React component.

```clj
(v/defview greeting [this]
  [:div "Hello, world!"])
```

(Note the [hiccup syntax](/docs/hiccup/syntax-guide).)

When called, views return React elements that can be rendered to the page using the `render-to-dom` function.

```clj
(v/render-to-dom (greeting) "some-element-id")
```

### State

Every component is assigned an atom, under the key `:view/state` on the component. This is for local state.

> React components are upgraded to behave kind of like Clojure maps: we can  `get` internal data by using keywords on the component itself, eg. `(:view/state this)`. 

```clj
(v/defview counter [this]
  [:div 
    {:on-click #(swap! (:view/state this) inc)}
    "Count: " @(:view/state this)])
```

When a component's state atom changes, the component is re-rendered -- exactly like `setState` in React.

### Props

If you pass a Clojure map as the first argument to a view, it is considered the component's 'props'.

```clj
;; pass a props map to a view
(greeting {:name "Herbert"})
```

You can `get` props by key directly on the component, eg. `(:name this)`.

```clj
(v/defview greeting [this]
  [:div "Hello, " (:name this)])
  
(greeting {:name "Friend"})
;; => <div>Hello, Friend</div>
```

> **Tip:** this is handy for destructuring directly in the view function's argument list, eg:

> ```clj
> (v/defview greeting [{:keys [name]}]
>   [:div "Hello, " name ])
> ```

### Component keys

You can get the props map of a component via the `:view/props` key, eg. `(:view/props this)`. Other keys are also available:

| Key                 | Description                                              |
|---------------------|----------------------------------------------------------|
| :view/props         | the props map passed to the component                    |
| :view/state         | the component's state atom (created on-demand)           |
| :view/children      | list of children passed to the component                 |
| :view/prev-props    | value of props map during previous lifecycle             |
| :view/prev-state    | value of state atom during previous lifecycle            |
| :view/prev-children | value of `children` list during previous lifecycle       |
| other keys          | looked up directly in the component's `:view/props` map. |

React [lifecycle methods](/docs/re-view/getting-started#__lifecycle-methods) can be included in a map before the argument list.

```clj
(defview focused-input
  {:view/did-mount (fn [this] (.focus (v/dom-node this)))}
  [this]
  [:input (:view/props this)])
                 
(focused-input {:placeholder "Email"})
```

See the [Getting Started](/docs/re-view/getting-started) guide for more.


