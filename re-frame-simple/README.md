# Re-Frame Simple

[alpha]

Graduated-complexity syntax for re-frame. Simple should be easy, complex should be possible.

----

### What is this? Why does it exist?

Here are three main ideas behind `re-frame-simple`:

1. treat the `db` as a thing we perform ordinary Clojure operations on
2. reactivity should 'just work'
3. opt-in to more advanced state management (eg. named queries and updates, coeffects) where necessary, ad-hoc.

Syntax is based on [re-view](https://www.re-view.io) and its associated re-db library.

### The Basics

Add the dependency (`boot` or `project.clj`):

`[re-view/re-frame-simple "0.1.0]`

Require the namespace:

```clj
(ns my-app.core
  (:require [re-view.re-frame-simple :as db]))
```

Note: we've aliased the namespace as `db`.

Read from the db using `get`, `get-in` and `identity` functions:

```clj
(db/get :a)        => (get @app-db :a)
(db/get-in [:a :b) => (get-in @app-db [:a :b])
```

Well, that was simple. What's so special?

Behind the scenes, `get` and `get-in` map to re-frame **subscriptions**, so when you use these functions to read data, the component you're in will **automatically update** when that data changes.


Write using `assoc!`, `update!`, `assoc-in!`, `update-in!`:


```clj
(db/assoc! :a 1)
(db/assoc-in! [:a :b] 1)

(db/update! :a inc)
(db/update-in! [:a :b] inc)
```

These functions map to re-frame **event handlers** which mutate the current state of the world (the db).

Also available are some operations for the whole db (rather than at a particular
key or path). We'll use these less often, as it's easier to understand, inspect, and debug your app when operations are scoped to specific paths.

```clj

(db/identity)     => @app-db

(db/swap! merge {:a 1})
```


### Example

Here is a counter widget which uses `get-in` and `update-in` to read and write a counter, given an id.

```clj
(defn count-button
  "Given a counter id and its current value, render it as an interactive widget."
  [id]
  NOTICE: `db/update-in!`
  [:div {:on-click #(db/update-in! [::counters id] inc)
         :style    {:padding    20
                    :margin     "10px 0"
                    :background "rgba(0,0,0,0.05)"
                    :cursor     "pointer"}}
   (str "Counter " (name id) ": ")

   NOTICE: `db/get-in`
   (db/get-in [::counters id])])
```

1. We put counters in a namespaced path in the db (`::counters`). Using a namespaced keyword as a path segment in the `db` means I can search my app for the namespaced `::counters` keyword, and find every instance where a counter is read or mutated. This makes up for some of the explicitness that is lost by moving away from typical, named re-frame events.
2. We didn't have to "register" anything to get a simple example like this to work. There is no inventing of names for transactions as simple as incrementing an integer at a path.

So, legibility of the reactivity system is built in to the design of your data structure, instead of added via explicitly named events/actions. (but we can still add those, when desired.)


### Named updates

`defupdate` associates a keyword with an update function. This can be dispatched like any other re-frame handler.

```clj
(db/defupdate :initialize [db]
              {::counters {"A" 0
                           "B" 1
                           "C" 2}})
```

Use with ordinary `re-frame.core/dispatch` (it's copied into the db namespace):

```clj
(db/dispatch [:initialize])
```

### Named queries

Use `defquery` to create named queries that read data using `db/get` and `db/get-in`:

```clj
(db/defquery counter-list
  "Return the list of counters in the db, by id."
  []
  (-> (db/get ::counters)
      (keys)))
```

The function returns a plain value, but uses a reactive subscription behind the scenes
to trigger reactivity, so a component that uses the query will update when its data changes.


### Finish the example

Here's the rest of the code we use to render our example to the page:

```clj
(defn root-view
  "Render the page"
  []
  [:div
   "Click to count!"
   (doall (for [id (counter-list)]
            ^{:key id} [count-button id]))])

(defn ^:export render []
  (reagent/render [root-view]
                  (js/document.getElementById "shadow-re-frame")))

(defn ^:export init []
  (db/dispatch-sync [:initialize])
  (render))
```



