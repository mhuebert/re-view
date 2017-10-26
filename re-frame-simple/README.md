# Re-Frame Simple

[alpha]

A light, beginner-friendly syntax built for `re-frame`.

#### TL;DR

To read data:

```
(db/get :a)
(db/get-in [:a :b])
```

To write data:

```
(db/assoc! :a 1)
(db/assoc-in! [:b :c] 1)

(db/update! :counter inc)
(db/update-in! [:counters :a] inc)
```

These functions 

1. map to a coherent set of re-frame operations based on Clojure core functions, 
2. provide reactivity that 'just works' (no need for manual subscriptions), and
3. don't get in your way if you want to go into more advanced stuff.

Using a tool like re-frame-trace, we can still see a meaningful log of operations performed:

![](https://i.imgur.com/vAuRHwo.png)

(screenshot taken from the [example project](mhuebert.github.io/shadow-re-frame/))

----

### Introduction

Learning `re-frame` involves wrapping one's head around many new words and concepts. However, the basic thing it does is quite simple and shouldn't be hard to get started with. `re-frame-simple` is a light syntax on top of `re-frame` which feels more like ordinary Clojure. It makes getting started and prototyping easier, without preventing you from using lower-level constructs where desired. 

Our approach:

1. read and write using ordinary Clojure operations
2. reactivity should 'just work'
3. more advanced state management is **opt-in** (eg. named queries and updates, coeffects)

(Syntax is roughly derived from [re-view](https://www.re-view.io) and its associated re-db library.)

### Example project

I have set up an example project which includes **re-frame-trace** so that you can see what's going on behind the scenes when you use `re-frame-simple`.

- Check out the [live demo](https://mhuebert.github.io/shadow-re-frame/)
- Read the [source code](https://github.com/mhuebert/shadow-re-frame)

### Get Started

Add the dependency (`boot` or `project.clj`):

`[re-view/re-frame-simple "0.1.1"]`

Require the namespace:

```clj
(ns my-app.core
  (:require [re-view.re-frame-simple :as db]))
```

**Note** we've aliased `re-view.re-frame-simple` as `db`.

Read from the db using `get`, `get-in` and `identity` functions:

```clj
;; value of the entire db
(db/get :a)        => (get @app-db :a)

;; swap! the entire db
(db/get-in [:a :b]) => (get-in @app-db [:a :b])
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

These functions map to re-frame **event handlers** which mutate the current state of the world (the db). They end in `!` as a reminder that you're mutating the world.

Also available are some operations for the whole db (rather than at a particular
key or path). We'll use these less often, as it's easier to understand, inspect, and debug your app when operations are scoped to specific paths.

```clj

(db/identity)     => @app-db

(db/swap! merge {:a 1})
```


### Example

Here is a counter widget which uses `get-in` and `update-in` to read and write a counter, given an id.

```clj
(defn counter
  "Render an interactive counter for `id`"
  [id]
                   ;; NOTICE: `db/update-in!` to write
  [:div {:on-click #(db/update-in! [::counters id] inc)
         :style    {:padding    20
                    :margin     "10px 0"
                    :background "rgba(0,0,0,0.05)"
                    :cursor     "pointer"}}
   (str "Counter " (name id) ": ")

   ;; NOTICE: `db/get-in` to read
   (db/get-in [::counters id])])
```

1. We put counters in a namespaced path in the db (`::counters`). This means I can search across my app for the namespaced `::counters` keyword, and find every instance where a counter is read or mutated. This makes up for some of the explicitness that is lost by moving away from typical, named re-frame events.
2. We didn't have to "register" anything to get a simple example like this to work. There is no inventing of names for transactions as simple as incrementing an integer at a path.

Using these tools, legibility of the reactivity system is built in to the design of your data structure, instead of added via explicitly named events/actions. (but we can still add those, when desired.)


### Named updates

`defupdate` associates a keyword with an update function. This can be dispatched like any other re-frame handler.

```clj
(db/defupdate :initialize [db]
              {::counters {"A" 0
                           "B" 1
                           "C" 2}})
```

Use with `re-frame.core/dispatch` (it's copied into the db namespace):

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

Usage:

```clj
(defn root-view
  "Render the page"
  []
  [:div
   "Click to count!"
                   ;; NOTICE: using our query
   (doall (for [id (counter-list)]
            ^{:key id} [counter id]))])
```


----

### Wrapping up

I hope you've found this helpful or interesting. Feedback is welcome!
