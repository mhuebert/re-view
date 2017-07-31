# Re-View Routing

![badge](https://img.shields.io/clojars/v/re-view-routing.svg)

----

Full details: https://re-view.io/docs/routing

----

## Usage

Include the dependency in `project.clj`:

```clj
[re-view-routing "0.1.1"]
```

Require **`re-view-routing.core`**:

```clj
(ns my-app.core
  (:require [re-view-routing.core :as routing])
```

Pass a callback to **`routing/listen`**. When the route changes, it will be called with a map containing a `:path` string, `:segments` vector and `:query` map.

```clj
(routing/listen update-route-state)
```

_...elsewhere..._

Pattern match on the `:segments` vector for common route handling. 

Use `case` for static routes: 

```clj
(defview static-route-handler 
  [location]
  (case (:segments location) 
    [] (app-views/home)
    ["about"] (app-views/about))
```

Use `core.match` for wildcard segments or other patterns.

```clj
(defview dynamic-route-handler 
  [location]
  (match (:segments location) 
    [] (app-views/home)
    ;; bind `id` to the second path segment, eg. '8' in the path `/posts/8`
    ["post" id] (app-views/posts {:id id}))
```


