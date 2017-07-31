# Advanced Topics

## Advanced compilation

For trouble-free [advanced compilation](https://github.com/clojure/clojurescript/wiki/Advanced-Compilation), items defined in the method map should be accessed in camelCase with dot syntax (eg. `(.-someProperty this)` or `(.someFunction this "my-arg")`, **not** using `aset` or `goog.object/get`. Keys are added via `set!` for Closure Compiler compatibility, so trying to read them by string will fail.

## Keys on the component

Re-View components implement [ILookup](https://cljs.github.io/api/cljs.core/ILookup) for succinct access to items in the props map. Because a component is always passed as the first argument to all of its methods, we can read the props we need by destructuring on `this`, pass in child elements as additional arguments, and still have access to the component itself:

```clj
(defview Post
  {:custom-method (fn [] ...)}
  [{:keys [title] :as this} & body]
  [:div {:on-click (.customMethod this)} 
    [:.bold title] 
    body])
...
(Post {:title "First post!"} 
   [:p "Welcome to ..."]
   [:p "We have..."])
```

Additional keys are defined under the `view` namespace:

```clj
:view/props     ;; the props map
:view/children  ;; list of children passed to the component.
:view/state     ;; state atom for the component (created when looked up for the first time).
```

Previous versions of these values are also available during each lifecycle:

```clj
:view/prev-props
:view/prev-children
:view/prev-state
```

## Mixins

Mixins are not directly supported by `defview` -- you must pass a literal map (there is some [discussion](https://facebook.github.io/react/blog/2016/07/13/mixins-considered-harmful.html) as to why).

However, you can specify a vector of functions for any lifecycle key and they will be evaluated sequentially at the appropriate time:

```clj
(defview my-app 
  {:view/did-mount [register-view focus-input]}
  [this]
  ...)
```

## Render Loop and forceUpdate

Views are updated in a render loop using `requestAnimationFrame` in browsers that support it. We use (https://facebook.github.io/react/docs/react-component.html#forceupdate[forceUpdate]) instead of rendering from the top level, because we know exactly which components should update for a given change in state.

```clj
v/force-render  ;; update component on next animation frame.
v/force-render! ;; update component immediately.
v/flush!        ;; execute pending updates immediately.
```

