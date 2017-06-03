_Re-View,_ exposes all of the power of [React](https://facebook.github.io/react/) in a clear and straightforward way in ClojureScript. 

## The Basic Approach

Components are created using `defview`, which is similar to Clojure's `defn` but always returns a React element (using [hiccup syntax](/docs/hiccup/syntax-guide)).

```clj
(defview greeting [this]
  [:div "Hello, world!"])
```

Views return React elements that can be rendered to the page.

```clj
(view/render-to-dom (greeting) "some-element-id")
```

Every component is assigned an atom for local state, returned via the `:view/state` key on the component. 

```clj
(defview counter [this]
  (let [state-atom (:view/state this)]
    [:div ...]))
```

When a component's state atom changes, the component is re-rendered -- exactly like `setState` in React.

React Lifecycle methods can be included in a map, before the argument list:

```clj
(defview greeting
  {:life/did-mount #(println "Mounted!")}
  [this]
  [:div ...])
```

## How do I use it?

Add the following dependencies to your project.clj or boot file:

```clj
[re-view "0.3.16"]
[cljsjs/react "15.5.0-0"]
[cljsjs/react-dom "15.5.0-0"]
```

Require the core namespace like so:

```clj
(ns app.core
  (:require [re-view.core :as view :refer [defview]]))
```
See the [Getting Started](/docs/re-view/getting-started) guide for more.


