Welcome! **Re-View** is a library for building React apps in ClojureScript. It has one external dependency, [React](https://facebook.github.io/react/).

## What are the advantages?

Re-View exposes all the power of React in a clear and straightforward way:

There is _one_ way to create components: the `defview` macro.

Views created with `defview` return plain React elements.

Every component is assigned an atom for local state (it is only created if used). When the value of this atom changes, the component is re-rendered -- exactly like `setState` in React. Lifecycle methods are called as expected.

Simple components are extremely simple to create. 'Advanced' components are created by progressively adding information to a simple component, and do not require entering a radically different headspace. Lifecycle methods are fully supported.

Components are self-documenting with an opt-in 'view spec' system, which can also validate props and assist in the creation of higher-order components.

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

**`defview`** is a macro similar to `defn`, which defines a view function using [hiccup syntax](/docs/hiccup/syntax-guide).

```clj
(defview greeting [this]
  [:div "Hello, world"])
```

When `greeting` is called, it will return a [React](https://facebook.github.io/react/) element, which you can render to the page:

```clj
(view/render-to-dom (greeting) "some-element-id")
```

Now let's define a view which expects a `:name` prop.

```clj
(defview greeting [this]
  [:div "Hello, " (:name this)])
```

Our `greeting` view will always be passed its React component as the first argument, which we've called `this`. We can read props from `this` just by looking up a key, eg. `(get this :name)`, or destructuring, `(let [{:keys [name]} this] ...)`.

Let's render our component to the page, passing in a name:

```clj
(view/render-to-dom (greeting {:name "Herbert"}) "some-element-id")
```



