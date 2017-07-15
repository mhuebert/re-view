# Differences

## Reagent

### Similarities

- **React** is used to render components
- **Hiccup**-like syntax is used for creating views
- **Reactivity**: components react automatically to changes in state
- Good **Performance** is usually easy to achieve

But there are also differences, explained below.

### Creating views

Reagent has [three ways](https://github.com/Day8/re-frame/wiki/Creating-Reagent-Components) to create a component. Re-View has one, `defview`, which is modeled after `defn`.

- There is only one form to learn
- Buying in to more advanced functionality requires only minor changes to a `defview` call.

```clj
;; simple component
(defview my-component []
  [:div "Hello, world"])

;; component with lifecycle method
(defview my-component
  {:view/did-mount #(println "mounted!")}
  []
  [:div "Hello, world"])

;; component with local state
(defview my-component
  [{state :view/state}]
  [:div {:on-click #(swap! state inc)} @state])
```

### Reactivity

Reagent uses 'reactive atoms' (`ratoms`). Each Reagent component keeps track of which ratoms are read during render, and subscribes itself to those ratoms. Re-View assigns one ordinary Clojure atom to each component, and re-renders the component when this atom changes.

- Using local state in Reagent requires mastering usage of closures and dynamic scope (which does not cross async boundaries). Using local state in Re-View requires only an understanding of atoms.
- In Re-View, it is always clear which atom will trigger re-render of which component.

### Consistency with React

Reagent does not call lifecycle methods when a component's state (ie. an input ratom) changes. Re-View always calls relevant lifecycle methods when a component updates.

Reagent uses [square brackets](https://github.com/Day8/re-frame/wiki/Using-%5Bsquare-brackets%5D-instead-of-%28parentheses%29) (`[my-component ...]`) for component re-use, whereas Re-View uses function calls (`(my-component ...)`).

- Re-View components can be seamlessly used inside ordinary javascript React apps, and vice versa.


### Global State

Reagent is most commonly used with [re-frame](https://github.com/Day8/re-frame) for handling global state.

Re-View integrates with **[Re-DB](https://www.github.com/re-view/re-db)** to automatically update views when relevant global state has changed.
