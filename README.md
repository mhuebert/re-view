# re-view

A thin [React](https://facebook.github.io/react/) ClojureScript wrapper designed to play well with [re-db](https://github.com/mhuebert/re-db) and closely follow/expose ordinary React behaviour.

Example:

```clj

(ns app.core
  (:require [re-view.core :as v :refer [defview]]))

(defview my-first-component
  {:initial-state {:height 0 }
   :did-mount (fn [{:keys [view/state] :as this}] 
                (swap! state assoc :height (.height (v/dom-node this))))}
   [{:keys [view/state]}] 
   [:div "Hello, world. I am " (:height @state) " pixels tall."])

```

Props can be looked up (& destructured) directly on a component:

```
(defview greeting
 [{:keys [user-name]}]
  [:div "Hello, " user-name])
  
(greeting {:user-name "Fred"})  
```

Views which include :initial-state will have an additional `:view/state` atom on the component:

```
(defview counter 
  {:initial-state 0}
  [{:keys [view/state]}] 
  [:div {:onClick #(swap! state inc)} @state])
```

Child elements are passed as additional arguments to the view function. 
If the first argument to a view is not a map, it is passed as a child (only a map is passed via props).

```
(defview show-the-number [_ n] 
  [:div.number-view n])
  
(map display-number [1 2 3])
```

### Changelog

0.3.1
- A `:ref` must be a function, instead of a string (see: https://facebook.github.io/react/docs/refs-and-the-dom.html)
- State is provided by an atom under the key `:view/state`, and only when `:initial-state` is present
- DOM element properties must be camelCase, eg `:onClick`. (We now use `re-view.hiccup/element` instead of `sablono` for creating React elements.)

0.3.0
- Props are looked up (& destructured) directly on components (`this`). 
- children are passed as additional args to the render function. 
- State is now an ordinary Clojure atom.

0.2.9
- Deprecated `render-to-dom` in favour of `render-to-node` and `render-to-id`

0.2.3
- Use render loop by default

0.2.2

- Use global re-db.d database (simplifies db subscriptions)
- Use namespace kw for indexing views by id (`:re-view/id`)
- `subs/db` subscription macro takes an extra `should-update?` parameter, no attempt to infer this from props
- More efficient subscription initialization

0.2.1

- Removed pattern-based db subscriber. Use re-view.subscriptions/db (a macro) to create a reactive database subscription. When body of subs/db is executed, listeners are created on accessed db-patterns, and body is re-run when these entities/attributes change. If the body reads from :props, the subscription will be updated when props change.

0.2
- `props` and `state` are keyword properties on `this`. Also available are `:prev-props, :prev-state, :children`. Destructuring on function arglist is encouraged:      

```

- Swap state via `(view/swap-state! this ...)`. Or, if you are feeling evil, plain `swap!` is equivalent to `view/swap-state!`.
- Shorten keyword method names (`:will-mount`, `:should-update`, etc.)
- Depracate `expand-props`
- Upgrade to Clojure(Script) 1.9.*
