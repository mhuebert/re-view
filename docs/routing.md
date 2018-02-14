# Routing

**[re-view-routing](https://github.com/braintripping/re-view/blob/master/re_view/src/re_view/routing.cljs)** provides an API to listen for changes to a browser's location, and parses routes into simple Clojure data structures. The current route is 'just another fact' that views can depend on.


## API

**`re-view.routing/listen`** calls a listener function when the browser's location changes. The listener is passed a location map consisting of the route's `:path` (string), `:segments` (vector), and `:query` (map). Options may be passed in the second parameter:

- `:intercept-clicks?` (boolean, `true`): For _click_ events on local links, prevent page reload & use history navigation instead.
- `:fire-now?` (boolean, `true`): in addition to listening for changes, fire callback immediately with current parsed route. Useful for populating app state before the initial render.

`routing/parse-path` returns the location map for a path (useful for server environments).


## Example

Create a listener that writes the current location to `re-db`. Views that read the current route will automatically re-render when it changes.
    
```clj 
(ns my-app.core 
  (:require [re-view.routing :as routing]
            [re-view.core :as v :refer [defview]]
            [re-db.d :as d]))

(routing/listen
  (fn [location] 
    (d/transact! [(assoc location :db/id :route/location)])))


(defview root 
  "Displays the current browser location path"
  [] 
  [:div (str "The current path is:" (d/get :route/location :path))])


```    

### Pattern match the current location

Dispatch the appropriate view using `case` or `core.match` on the location's `:segments`, which is a vector of strings (normalized to ignore trailing slashes, so the root path, "/", is an empty vector).

For static routes, use `case`:

```clj
;; where `views` is a namespace of React components
(defview root []
  (case (d/get :route/location :segments)
    [] (views/home)
    ["about"] (views/about)
    (views/not-found)))
```

For wildcard segments, use `core.match`. Here, we bind `page-id` to a path segment, and then pass it as a prop to a view:

```clj
(defview root []
  (match (d/get :route/location :segments)
    [] (views/home)
    ["pages" page-id] (views/page {:id page-id})
    :else (views/not-found)))

;; given path "/pages/x",
;; :segments would be ["pages" "x"],
;; `page-id` would bind to "x",
;; `match` would expand to (views/page {:id "x"})
```
`core.match` also supports [guards](https://github.com/clojure/core.match/wiki/Basic-usage#guards) and [maps](https://github.com/clojure/core.match/wiki/Basic-usage#map-patterns), so you could also pattern-match on the `:path` string and `:query` map. (See the [wiki](https://github.com/clojure/core.match/wiki) for full details.)
