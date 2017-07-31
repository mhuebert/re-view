# Getting Started

## Your First View 

Add re-view to your `:dependencies` in `project.clj`: 

![badge](https://img.shields.io/clojars/v/re-view.svg)

Require `re-view.core` like so:

```clj
(ns my-app.core 
  (:require [re-view.core :as v :refer [defview]]))
```

Create a view that returns a `div` with a 'hello, world!' greeting.

```clj
(defview say-hello [this] 
  [:div "hello, world!"])
```

### Render to the page

**`re-view.core/render-to-dom`** renders an React element to the page, given a DOM element or the ID of an element on the page.

Suppose we have the following `div` on our HTML page.

```html
<div id="my-app"></div>
```

How do we render our view to the page?
 
```clj
(v/render-to-dom (say-hello) "my-app")
```

### Props

Render the component again, but this time pass it a map containing the `:name` (a string) of someone you know.

```clj
(v/render-to-dom (say-hello {:name "fred"}) "my-app")
```

If the first argument to a view is a Clojure **map** (eg. `{:name "fred"}`), it is considered the component's [props](https://facebook.github.io/react/docs/components-and-props.html). Props can be looked up by keyword on the component itself (eg. `(:name this)`. The component is **always** passed as the first argument to a view. 

How do we read the `:name` prop from the component?

```clj
(:name this)
;; or 
(get this :name)
```

Change the view to include the `:name` prop in the greeting text.

```clj
(defview say-hello [this]
  [:div "hello, " (:name this) "!"])
```

We have created a view and passed it a `props` map, `{:name "fred"}`. We accessed the `:name` prop by reading it from the component, which is passed in as the first argument to the view. 

There is another way to read prop keys from the component. That is, Clojure [destructuring](https://clojure.org/guides/destructuring):

```clj
(defview say-hello [{:keys [name] :as this}]
  ... name ...)
```

## State

> What do programmers mean by the word 'state'? If you're not sure, read [this](../explainers/state).

### Local state with the 'state atom'

Every component is automatically assigned a Clojure [atom](../explainers/atoms), and will update whenever this atom changes. You can get the atom for a component via its `:view/state` key:

```
(defview Counter 
  [this]
  [:div (:view/state this)])
```

To set an initial value for a component's state atom, set the `:view/initial-state` key in its methods map:

```
(defview Counter 
  {:view/initial-state 0}
  [this]
  [:div (:view/state this)])
```

> If the value of `:view/initial-state` is a function, it will be called (with the component as its first argument) and initial-state is set to its return value.

During each component lifecycle, the previous state value is accessible via the `:view/prev-state` key.

Now let's add a click handler to make our Counter component complete:

```clj
(defview Counter
  {:view/initial-state 0}
  [this]
  [:div {:on-click #(swap! (:view/state this) inc)} "Current count: " @(:view/state this)])
```

### Global state

Re-View was written in tandem with [re-db](https://github.com/re-view/re-db), a tool for managing global state. When a view renders, we track which data is read from `re-db`, and update the view when that data changes. More information in the re-db [README](https://www.github.com/re-view/re-db).

## Methods map

`defview` accepts a map, immediately before the arguments list, where we can introduce additional, more advanced component features.

### Lifecycle methods 

React [lifecycle methods](https://facebook.github.io/react/docs/react-component.html#the-component-lifecycle) are supported via the following keys:

| key          | description          |
|---|---|
| **:view/initial-state**      | _getInitialState_ (Initial value for the `:view/state` atom. Can be function (of `this`) or other value.)           |
| **:view/will-mount**         | _componentWillMount_        |
| **:view/did-mount**          | _componentDidMount_         |
| **:view/will-receive-props** | _componentWillReceiveProps_ |
| **:view/should-update**      | _shouldComponentUpdate_     |
| **:view/will-update**        | _componentWillUpdate_       |
| **:view/did-update**         | _componentDidUpdate_        |
| **:view/will-unmount**       | _componentWillUnmount_      |

**Example:**

```clj
(defview say-hello 
  {:view/did-mount (fn [this] (println "Mounted!"))}
  [this]
  [:div "hello, world!"])
```

### Custom view methods

Arbitrary keys may be included in the methods map. Functions are **always** passed the component itself as the first argument. Keys are converted to `camelCase` and should be accessed using dot syntax on the component (eg. `(.-someProperty this)` or `(.someFunction this)`.

```clj
(defview say-hello 
  {:print-greeting (fn [this] (println (str "Hello, " (:name this) "!"))}
  [this] 
  [:div {:on-click #(.printGreeting this)} "Print Greeting"])
```
## Special keys

There are two additional keys which will be handled specially if included in the methods map.

| key | description
| --- | ---
| **:key**  | React [key](https://facebook.github.io/react/docs/lists-and-keys.html). A unique value for components which occur in lists. `:key` can be a keyword, which will be applied to the component's `props` map, a function, which will be passed the component and its children, a string, or number.
| **:display-name** | React _[displayName](https://facebook.github.io/react/docs/react-component.html#displayname)_. A friendly name for the component, which will show up in React Devtools. Re-View automatically supplies a display-name for all components, based on the name of the component and the immediate namespace it is defined in.

### Children

If the first argument passed to a view is a map, it is considered the component's `props`. All other arguments are considered `children`, and passed as additional arguments **after** the component.

Render the component again, but this time pass it a name directly, as a string, instead of inside a `props` map.

```clj
(v/render-to-dom (say-hello "fred") "my-app")
```

Now modify the view to accept a second argument, which will contain the string you passed in. Update the greeting text to use this value. 

```clj
(defview say-hello [this name]
  [:div "hello, " name "!"])
```
Remember, the first argument to the view function always the component itself (`this`), and that's where we read `props` keys. All other arguments are passed in afterwards. `(say-hello "fred")` and `(say-hello {} "fred")` are equivalent, just as `[:div "fred"]` and `[:div {} "fred"]` are equivalent.
