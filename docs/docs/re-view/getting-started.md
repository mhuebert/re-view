## Re-View: Getting Started

## Your First View 

Add re-view to your `:dependencies` in `project.clj`: 

![badge](https://img.shields.io/clojars/v/re-view.svg)

Require `re-view.core` like so:

```clj
(ns my-app.core 
  (:require [re-view.core :as v :refer [defview]]))
```

**`defview`** is a macro that returns a view. Similar to `defn`, it expects a name, optional docstring, and arguments vector, followed by the body of the view, which should return valid link:Hiccup-Syntax[hiccup syntax] or a React element. 

Create a view that returns a `div` with a 'hello, world!' greeting.

```clj
(defview say-hello [this] 
  [:div "hello, world!"])
```

## Render to the page

**`re-view.core/render-to-element`** renders an React element to the page, given a DOM element or the ID of an element on the page.

Suppose we have the following `div` on our HTML page.

```html
<div id="my-app"></div>
```

How do we render our view to the page?
 
```clj
(v/render-to-element (say-hello) "my-app")
```

## Props

Render the component again, but this time pass it a map containing the `:name` (a string) of someone you know.

```clj
(v/render-to-element (say-hello {:name "fred"}) "my-app")
```

If the first argument to a view is a Clojure **map** (eg. `{:name "fred"}`), it is considered the component's https://facebook.github.io/react/docs/components-and-props.html[props]. Props can be looked up by keyword on the component itself (eg. `(:name this)`. The component is **always** passed as the first argument to a view. 

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

There is one more way to read prop keys from the component. That is, Clojure [destructuring](https://clojure.org/guides/destructuring):

```clj
(defview say-hello [{:keys [name] :as this}]
  ... name ...)
```

## Children

If the first argument passed to a view is a map, it is considered the component's `props`. All other arguments are considered `children`, and passed as additional arguments **after** the component.

Render the component again, but this time pass it a name directly, as a string, instead of inside a `props` map.

```clj
(v/render-to-element (say-hello "fred") "my-app")
```

Now modify the view to accept a second argument, which will contain the string you passed in. Update the greeting text to use this value. 

```clj
(defview say-hello [this name]
  [:div "hello, " name "!"])
```
Remember, the first argument to the view function always the component itself (`this`), and that's where we read `props` keys. All other arguments are passed in afterwards. `(say-hello "fred")` and `(say-hello {} "fred")` are equivalent, just as `[:div "fred"]` and `[:div {} "fred"]` are equivalent.

## Methods map

`defview` accepts a methods map, immediately before the arguments list. Methods are called with the component itself (by convention, `this`) as the first argument, and children as additional arguments (when present). Keys are converted to `camelCase` and should be accessed using dot syntax on the component (eg. `(.-someProperty this)` or `(.someFunction this)`.

```clj
(defview say-hello 
  "Prints a greeting when clicked"
  {:print-greeting (fn [this] (println (str "Hello, " (:name this) "!"))}
  [this] 
  [:div {:on-click (fn [e] (.printGreeting this))} "Print Greeting"])
```

## Lifecycle methods 

React https://facebook.github.io/react/docs/react-component.html#the-component-lifecycle[lifecycle methods] are supported via the following keys:


| Method key          | React equivalent          ||
|---|---|
| :initial-state      | getInitialState           | Initial value for the `:view/state` atom. Can be function (of `this`) or other value. |
| :will-mount         | componentWillMount        ||
| :did-mount          | componentDidMount         ||
| :will-receive-props | componentWillReceiveProps ||
| :should-update      | shouldComponentUpdate     ||
| :will-update        | componentWillUpdate       ||
| :did-update         | componentDidUpdate        ||
| :will-unmount       | componentWillUnmount      ||

**Example:**

```clj
(defview say-hello 
  "Prints a message when mounted"
  {:did-mount (fn [this] (println "Mounted!"))}
  [this]
  [:div "hello, world!"])
```

There are two other special keys:

| key | description
| --- | ---
| **:key**  | React [key](https://facebook.github.io/react/docs/lists-and-keys.html). A unique value for components which occur in lists. `:key` can be a keyword, which will be applied to the component's `props` map, a function, which will be passed the component and its children, a string, or number.
| **:display-name** | React _[displayName](https://facebook.github.io/react/docs/react-component.html#displayname)_. A friendly name for the component, which will show up in React Devtools. Re-View automatically supplies a display-name for all components, based on the name of the component and the immediate namespace it is defined in.

## State

Re-View supports two ways of managing state.

### Local state

The `:view/state` key on a component will return a Clojure link:Atoms[atom], unique to the component. When the value of this atom changes, the component will update (re-render). The initial value of the state atom is determined by the `:initial-state` key in the methods map, or `nil` if not present.

____
If you're not sure what a Clojure atom is, check out the link:Atoms[atom explainer].
____

### Global state

Re-View was written in tandem with [re-db](https://github.com/mhuebert/re-db), a tool for managing global state. When a view renders, we track which data is read from `re-db`, and update the view when that data changes. (Re-DB docs to follow)