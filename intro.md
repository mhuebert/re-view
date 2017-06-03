**Re-View** is a library for building React apps in ClojureScript. It has one external dependency, [React](https://facebook.github.io/react/).

## Usage

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

**`defview`** is a macro similar to `defn`, which defines a view function.

```clj
(defview greeting [this]
  [:div "Hello, world"])
```

When `greeting` is called, it will return a [React](https://facebook.github.io/react/) element, which you can render to the page:

```clj
(view/render-to-dom (greeting) "some-element-id")
```

**What you've seen so far**

- Creating a view using `defview`
- Using [hiccup syntax](/docs/hiccup/syntax-guide) to specify HTML elements
- Rendering a component to the page using `re-view.core/render-to-dom`

Let's keep going.

## Props

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



