# re-view

A thin [React](https://facebook.github.io/react/) ClojureScript wrapper designed to play well with [re-db](https://github.com/mhuebert/re-db) or [datascript](https://github.com/tonsky/datascript) and closely follow/expose ordinary React behaviour.

Example:

```clj

(ns app.core
  (:require [re-view.core :as view]))

(def my-first-component
  (component
   :get-initial-state
   (fn [this props] {:color "purple"})

   ;; put rendered height into state
   :component-did-mount
   (fn [this] (view/update-state this assoc :height
                (.height (js/ReactDOM.findDOMNode this))))

   :render
   (fn [this] [:div "Hello, world"])))

```


Pass `component` React lifecycle methods (names are auto-converted from :hyphenated-keywords to camelCase) and get back a factory.

Important to know:

* use `view/state` to read state and `view/props` to read props

```
...
  :render
  (fn [this]
    [:div "Hello, " (:name (view/state this))])
...
```

* define :react-key to assign unique keys to components based on props (required for React)

```
...
  :react-key
  (fn [this props] (:id props))
...
```

* pass sparse props to components, and define `expand-props` to fetch/get the rest. For example, you might pass a component only `{:id <some-id>}`, and leave responsibility with the component to fetch the rest of the entity.

 ```
 ...
   :expand-props
   (fn [this props]
     (sync-entity! (:id props))
     (merge props (d/entity @db [:id props)])))
 ...
 ```

* call `render-component` to re-render a component, optionally with new props. this can be any component, not only root components.

```
(view/render-component my-component {:id <some-id>})
```

