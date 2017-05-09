:toc:


## Advanced compilation

For trouble-free https://github.com/clojure/clojurescript/wiki/Advanced-Compilation[advanced compilation], items defined in the method map should be accessed in camelCase with dot syntax (eg. `(.-someProperty this)` or `(.someFunction this "my-arg")`, **not** using `aset` or `goog.object/get`. Keys are added via `set!` for Closure Compiler compatibility, so trying to read them by string will fail.

## Keys on the component

Re-View components implement https://cljs.github.io/api/cljs.core/ILookup[ILookup] for succinct access to items in the props map. Because a component is always passed as the first argument to all of its methods, we can read the props we need by destructuring on `this`, pass in child elements as additional arguments, and still have access to the component itself:

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

We can also `get` three additional keys on `this`:

**:view/props** returns the entire props map (eg. to pass down to a child component). +
**:view/children** returns the list of children passed to the component. +
**:view/state** returns the state atom for the component (it is created when looked up for the first time).

## Mixins

Mixins are https://facebook.github.io/react/blog/2016/07/13/mixins-considered-harmful.html[not supported] by `defview`. 

**`v/compseq`** composes functions to execute sequentially on the same arguments, useful for composing lifecycle methods:

```
(defview my-app 
  {:did-mount (v/compseq register-view focus-input)}
  [this]
  ...)
```

## Render Loop and forceUpdate

Views are updated in a render loop using `requestAnimationFrame` in browsers that support it. 

**`v/force-render`** updates a component on the next animation frame. +
**`v/force-render!`** updates a component immediately (https://facebook.github.io/react/docs/react-component.html#forceupdate[forceUpdate]). +
**`v/flush!`** executes pending updates immediately.


