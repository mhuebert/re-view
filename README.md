# Re-View

A reactive view library for building user interfaces in ClojureScript. Depends on [re-db](https://www.github.com/mhuebert/re-db) for managing state.


### Hello, world

Include `re-view.core` like so:

```clj
(ns my-app.core 
  (:require [re-view.core :as v :refer [defview]]))
```

`defview` is a macro that returns a view which can be rendered to the page using `v/render-to-element`. Similar to `defn`, it expects a name, optional docstring, and arguments vector, followed by the body of the view, which should return valid [hiccup syntax](https://github.com/mhuebert/re-view/wiki/Hiccup-Syntax) or a React element. 

If you don't know what `hiccup syntax` is, that's totally fine -- but you should [read the guide](https://github.com/mhuebert/re-view/wiki/Hiccup-Syntax) before continuing.

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

Recall that in [hiccup syntax](https://github.com/mhuebert/re-view/wiki/Hiccup-Syntax), we can _optionally_ pass a map of attributes to an element (in React, we call these attributes `props`):

```clj
;; element without attributes:
[:label "First Name"]

;; element with attributes:
[:label {:for "first-name"} "Name"]
```

Similarly, if you pass a Clojure map as the first argument to a view, it will be treated as the component's [props](https://facebook.github.io/react/docs/components-and-props.html) map. We read keys from the prop map by using `get` on the component itself.
  
Modify the component to expect a `:color` key in the props map. Add a `:style` map on the `div`'s props to color the greeting text.

```clj
(defview say-hello [this name] 
  [:div {:style {:color (get this :color)}} "hello, " name "!"])
```

See how we used `(get this :color)` to read a prop key from the component?

You can also use [destructuring](https://clojure.org/guides/destructuring) to get prop keys from the component. Here's what that looks like:

```clj
(defview say-hello [{:keys [color]}] 
  [:div {:style {:color color}} "hello, " name "!"])
```
  
Render the component again, passing "red" as the `:color` prop.

```clj 
(v/render-to-element (say-hello {:color "red"}))
```  
  
We've seen how to read individual prop keys from the component. The `:view/props` key returns the props map itself. The `:view/children` key returns a list of the component's children. This is useful when you want to create a higher-order component, and pass props or children down to a child view. 

### Lifecycle methods 

_Oops,_ we withheld information. There is another difference between `defview` and `defn`. You can pass a Clojure map to `defview`, immediately before the arguments list, and include React [lifecycle methods](https://facebook.github.io/react/docs/react-component.html#the-component-lifecycle). These methods will be called at the appropriate time during the component's life. Methods are always called with the component itself (by convention, `this`) as the first argument. Children passed to the view are included as additional arguments.

To save typing, we use shortcuts for lifecycle method names. Here they are:

| Re-View name        | React Equivalent          |                                                                                                                                                                                               |
| ------------------- | ------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| :initial-state      | getInitialState           | Returns an initial value for the `:view/state` atom of the component. If `:initial-state` is a function, it is called with the component and its children. Other values are left untouched.   |
| :will-mount         | componentWillMount        |                                                                                                                                                                                               |
| :did-mount          | componentDidMount         |                                                                                                                                                                                               |
| :will-receive-props | componentWillReceiveProps |                                                                                                                                                                                               |
| :should-update      | shouldComponentUpdate     |                                                                                                                                                                                               |
| :will-update        | componentWillUpdate       |                                                                                                                                                                                               |
| :did-update         | componentDidUpdate        |                                                                                                                                                                                               |
| :will-unmount       | componentWillUnmount      |                                                                                                                                                                                               |                                                                             

There are also two special keys we can add to the method map:

| key           | React Equivalent |                                                                                                                                                                                                                              |
| ------------- | ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| :key          | key              | A unique value for components which occur in lists. 

`:key` can be a keyword, which will be applied to the component's `props` map, a function, which will be passed the component and its children, a string, or number.   |
| :display-name | displayName      | A friendly name for the component, which will show up in React Devtools.
Re-View automatically supplies a display-name for all components, based on the name of the component and the immediate namespace it is defined in.  |
 
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

### Local state

To manage local state, the `:view/state` key on a component will return a Clojure [atom](https://github.com/mhuebert/re-view/wiki/Atoms), unique to the component. When the value of this atom changes, the component will update (re-render). If you're not sure what a Clojure atom is or how it works, please [read the guide](https://github.com/mhuebert/re-view/wiki/Atoms).

The initial value of the state atom will be whatever is defined by the `:initial-state` key in the methods map, or `nil`.

### Global state

Re-View was written in tandem with [re-db](https://github.com/mhuebert/re-db), a tool for managing global state. When a view renders, we track which data is read from `re-db`, and update the view when that data changes.

### Force updates

Views are rendered in a 'render loop' using `requestAnimationFrame`. `v/force-render` will force a component to re-render on the next animation frame, while `v/force-render!` will force an update immediately.

### Component display names

To help with debugging, a [display name](https://facebook.github.io/react/docs/react-component.html#displayname) of a component is set by `defview`. The display name is composed of the last segment of the component's namespace, and its name, eg. `my-app.views.layout/root` will show up as `layout/root`. You can override this by including `:display-name` in the methods map.
