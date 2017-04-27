# Re-View

A reactive view library for building user interfaces in ClojureScript. Depends on [re-db](https://www.github.com/mhuebert/re-db) for managing state.


### Hello, world

Include `re-view.core` like so:

```clj
(ns my-app.core 
  (:require [re-view.core :as v :refer [defview]]))
```

`defview` is a macro that returns a view which can be rendered to the page using `v/render-to-element`. Similar to `defn`, it expects a name, optional docstring, and arguments vector, followed by the body of the view, which should return valid [hiccup](https://github.com/mhuebert/re-view-hiccup/wiki/Hiccup-Syntax) or a React element. 


Create a view that returns a `div` with a 'hello, world!' greeting.

```clj
(defview say-hello [this] 
  [:div "hello, world!"])
```

Notice that we already have one parameter, `this`. The component itself is always passed as the first argument to the view.

Add another parameter to the view, so that you can pass the view the name of a person to greet.

```clj
(defview say-hello [this name] 
  [:div "hello, " name "!"])
```

Render the view to the element on the page with the id "my-app". Greet someone you know.
 
```clj
(v/render-to-element (say-hello "fred") "my-app")
```

We created a view and passed it a string, "fred". This string was treated as a 'child' of the view, and passed as the second argument to the view.

### Props

If you pass a Clojure map as the first argument to a view, it will be treated as the component's [props](https://facebook.github.io/react/docs/components-and-props.html). Props are how we pass parameters to views. We read keys from the prop map by using `get` on the component itself.
  
Modify the component to expect a `:color` key to be passed as a prop. Use a `:style` map on the `div` to color the greeting.

```clj
(defview say-hello [this name] 
  [:div {:style {:color (get this :color)}} "hello, " name "!"])
```

See how we used `(get this :color)` to read a prop key from the component?

You can also use [destructuring](https://clojure.org/guides/destructuring) to get prop keys from the component. Here's what that looks like:

```clj
(defview say-hello [{:keys [color]} name] 
  [:div {:style {:color color}} "hello, " name "!"])
```

  
Render the component again, passing the color 'red' as a prop.
```clj 
(say-hello {:color "red"} "fred")
```  
  
We read individual prop keys from the component. The `:view/props` key returns the props map itself. This is useful when you want to create a higher-order component, and pass its props down to a child view.

### State

To manage local state, we can read the `:view/state` key on a component. It will return a Clojure [atom](https://github.com/mhuebert/re-view/wiki/Atoms), unique to the component, that we can `swap!` and `reset!`. Whenever the value of this atom changes, the component will update (re-render).

### Lifecycle methods 

_Oops,_ we withheld information. There is another difference between `defview` and `defn`. You can pass a Clojure map to `defview`, immediately before the arguments list, and include React [lifecycle methods](https://facebook.github.io/react/docs/react-component.html#the-component-lifecycle). These methods will be called at the appropriate time during the component's life. Methods are always called with the component itself (by convention, `this`) as the first argument. Children passed to the view are included as additional arguments.

To save typing, we use shortcuts for lifecycle method names. Here they are:

| Re-View name        | React Equivalent          |
| ------------------- | ------------------------- |
| :initial-state      | getInitialState           |
| :will-mount         | componentWillMount        |
| :did-mount          | componentDidMount         |
| :will-receive-props | componentWillReceiveProps |
| :should-update      | shouldComponentUpdate     |
| :will-update        | componentWillUpdate       |
| :did-update         | componentDidUpdate        |
| :will-unmount       | componentWillUnmount      |

 
### Additional component methods
 
A component's methods map may contain any functions, not just lifecycle methods. These methods will always be called with the component as the first argument, and then additional argument(s). Method names will be converted to `camelCase`, as they are defined on the component itself in javascript-land. Be careful though, ClojureScript advanced compilation has a hard time figuring out these names. You need to use the super-alpha [Externs Inference](https://clojurescript.org/guides/externs#externs-inference) to give a hint to the compiler that your component (eg. `this`) is a `React.Component`. Here's what that looks like:
 
```clj
(defview super-component 
  {:random-color (fn [this]
                   ;; returns color of your face if you screw up externs inference
                   (rand-nth ["blue" "black" "green"]))}
  [^js/React.Component this]
  [:div {:style {:color (.randomColor this)}}])
  
``` 
 
See how we added a hint in the arguments list for `this`, in the function where we call the method. To use externs inference you also need to use the latest ClojureScript alpha and follow the [guide](https://clojurescript.org/guides/externs#externs-inference). 

### Children

In addition to the `props` map, we can pass additional arguments to a view. In React parlance, we call these the component's 'children'. They are passed as additional arguments to the render function, and all the other methods on the component. The list of children is also returned via the `:view/children` key on the component itself.

### Reactivity and Re-DB

Re-View was written in tandem with [re-db](https://github.com/mhuebert/re-db). When a view renders, we track which data is read from `re-db.d`. After every re-db transaction, we update only views that match the new data.

### Force updates

`v/force-render` will force a component to re-render on the next animation frame. `v/force-render!` will force a component to render immediately.

### Component display names

Effort has been made to play well with the React Devtools browser add-on. Component names are composed of their immediate namespace, and name. So `my-app.views.layout/root` will show up as `layout/root`.

### Changelog

0.3.9
- Support docstrings in `defview`
- Pass children as add'l args to :initial-state
- Better handling of links in router to allow scrolling on anchor/hash links
- Do not include `ref` attribute in `props`
- `re-view-example` namespace with helpers for preparing component examples
- Move `re-view.routing` to a separate project (`re-view-routing.core`)
- Move `re-view.hiccup` to a separate project (`re-view-hiccup.core`). Hiccup conversion now supports dashes-to-camelCase conversion, `:class` instead of className, and :for instead of htmlFor. You may also pass classes as a collection (vector, set, etc.) under the `:classes` key.
- `core/render-to-element` accepts an ID string or HTML element, replacing `core/render-to-id` and the mistakenly named `core/render-to-node`.
- New readme

0.3.8
- Bugfix on-location-change 

0.3.7
- Rename routing/on-route-change -> routing/on-location-change, pass *parsed* route to callback
- Fixed tests

0.3.4
- Add 'swap-silently!' method to swap state without triggering re-render

0.3.3
- Classes provided in props, eg. {:className "bg-black"}, are listed *after* classes provided in element keywords, eg. :div.bg-black, so that dynamically-provided classes take precedence over statically written classes.

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