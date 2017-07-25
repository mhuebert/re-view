# Re-View

![badge](https://img.shields.io/clojars/v/re-view.svg)

Re-View is a library for building React apps in ClojureScript. [https://re-view.io](https://re-view.io)

## Quickstart

Use the leiningen template to start a fresh project:

```bash
lein new re-view my-app
cd my-app
lein figwheel
;; when figwheel finishes building, visit http://localhost:5300 to see the example page
```

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
