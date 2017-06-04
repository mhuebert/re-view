_Re-View_ is a tool for building [React](https://facebook.github.io/react/) apps in ClojureScript. 

## Benefits

- Precise and transparent mechanisms for reactivity
- Convenient access to React lifecycle methods
- A smooth 'upgrade continuum': simple components are extremely simple to create, while 'advanced' components are created by progressively adding more information to a simple component (no need to switch paradigms along the way)

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

```clj
(defview counter [this]
  (let [state-atom (:view/state this)]
    [:div ...]))
```

When a component's state atom changes, the component is re-rendered -- exactly like `setState` in React.

React components are upgraded to behave kind of like Clojure maps: we can  `get` internal data by using keywords on the component itself, eg. `(:view/state this)`. **In addition,** if you pass a Clojure map as 'props' to a view, such as `(greeting {:name "Fred"})`, you can `get` props by key directly on the component, eg. `(:name this)`.

```clj
(defview greeting [this]
  [:div "Hello, " (:name this)])
  
(greeting {:name "Herbert"})
;; => <div>Hello, Herbert</div>
```

React lifecycle methods can be included in a map before the argument list.

```clj
(defview greeting
  {:life/did-mount #(println "Mounted!")}
  [this]
  [:div ...])
```

See the [Getting Started](/docs/re-view/getting-started) guide for more.


