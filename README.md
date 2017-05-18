# Re-View

![badge](https://img.shields.io/clojars/v/re-view.svg)

Re-View is a library for building reactive user interfaces in ClojureScript.

----

For full details, visit the **[website](https://re-view.matt.is)**.

----

## Usage

Include the dependency in `project.clj`:

```clj
[re-view "xxx"]
```

Require `re-view.core` (by convention, alias as `v` and refer `defview`)

```clj
(ns my-app.core 
  (:require [re-view.core :as v :refer [defview]]))
```

Define your first view:

```clj
(defview say-hello [this] 
  [:div "hello, world!"])
```

Render to the DOM:

```clj
(v/render-to-dom (say-hello) "id-of-element")
```

Pass in a props map:

```clj
(v/render-to-dom (say-hello {:name "Maria"}) "id-of-element")
```

Read the `:name` prop:

```clj
(defview say-hello [this] 
  [:div "hello, " (:name this) "!"])
```
