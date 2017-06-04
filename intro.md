_Re-View_ is a tool for building [React](https://facebook.github.io/react/) apps in ClojureScript.

## Basic Usage

Components are created using `defview`, which is similar to Clojure's `defn`.

```clj
(defview greeting [this]
  [:div "Hello, world!"])
```

(Note the [hiccup syntax](/docs/hiccup/syntax-guide).)

When called, views return React elements that can be rendered to the page using the `render-to-dom` function.

```clj
(view/render-to-dom (greeting) "some-element-id")
```

Every component is assigned an atom, under the key `:view/state`.

```clj
(defview counter [this]
  (let [state-atom (:view/state this)]
    [:div ...]))
```

When a component's state atom changes, the component is re-rendered -- exactly like `setState` in React.

React lifecycle methods can be included in a map before the argument list.

```clj
(defview greeting
  {:life/did-mount #(println "Mounted!")}
  [this]
  [:div ...])
```

## How to start

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


