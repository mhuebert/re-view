# Re-View

A reactive view library for building user interfaces in ClojureScript. Depends on [re-db](https://www.github.com/mhuebert/re-db) for managing state.


### Hello, world

Include `re-view.core` like so:

```clj
(ns my-app.core 
  (:require [re-view.core :as v :refer [defview]]))
```

`defview` is a macro that returns a view which can be rendered to the page using `v/render-to-element`. Similar to `defn`, it expects a name, optional docstring, and arguments vector, followed by the body of the view, which should return valid [hiccup](https://github.com/mhuebert/re-view/wiki/Hiccup-Syntax) or a React element. 


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
