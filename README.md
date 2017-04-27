# Re-View Routing

Our basic perspective on routing is that the current browser location should be treated as 'just another fact' that views can read and react to. The goal of this library is to provide a reliable API to listen for changes to a browser's location, and parse routes into ordinary Clojure data structures.

## API

`routing/on-location-change` calls a listener function when the browser's location changes. The listener is passed a location map consisting of a `:path` string, `:segments` vector, and `:query` parameter map. An options map may be passed as a second parameter:
   
- `:intercept-clicks?` (boolean, `true`): For _click_ events on local links, prevent page reload & use history navigation instead.
- `:fire-now?` (boolean, `true`): in addition to listening for changes, fire callback immediately with current parsed route. Useful for populating app state before the initial render.

`routing/parse-path` returns the location map for a path (useful for server environments).


### Example with Re-View

If you are using `re-view`, we recommend writing the current location to `re-db` so that views can read the location and automatically re-render when it changes.
    
```clj 
(ns my-app.core 
  (:require [re-view-routing :as routing]
            [re-view.core :as v :refer [defview]]
            [re-db.d :as d]))
            
(routing/on-location-change 
  (fn [location] 
    (d/transact! [(assoc location :db/id :route/location)])))
      
(defview root 
  "Displays the current browser location path"
  [] 
  [:div (str "The current path is:" (d/get :route/location :path))])
```    

### Dispatching views

We usually dispatch views based on the location's `:segments`, which is a vector of path segment strings (normalized to ignore trailing slashes, so the root path, "/", is an empty vector).

For static routes, `case` is sufficient:

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
