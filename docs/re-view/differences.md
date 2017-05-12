# Re-View: Differences

## Compared to Reagent

It should be said that Reagent is beautiful and simple for many use cases.

**Reactive updates**

Reagent uses 'reactive atoms' to handle state.

- Components track which atoms are read (dereferenced) during render, and when those atoms change, the view is re-rendered. There are no other rules about which atoms will trigger which components to render. Without reading a lot of code, it's not obvious which views will update when atoms change. Atoms and cursors can be `def`'d in namespaces and bound in closures all across your app.
- A common pattern is to put all of an app's state in a single atom, and then use [cursors](https://github.com/reagent-project/reagent-cookbook/tree/master/basics/cursors) to 'break up' the atom into smaller pieces to pass to components.

    A cursor behaves exactly like an atom itself, but it is derived from a parent atom, and can only read & write a particular path into its parent. Every time an atom changes, it must perform an equality check for each of its child cursors to determine which have changed. This is usually no problem, but in some cases can cause performance issues (eg. if you have hundreds or more cursors into the same atom).

Re-View has two ways of managing state.

- First, there is one **[state atom](getting-started#state-atoms)** per component. When it changes, the component is re-rendered. There is never any doubt about which component is bound to which atom. These are ordinary Clojure atoms, which do not support cursors: they are only for managing internal state, one component each. It is easy to inspect the state atom of a component at runtime.

- Second, Re-View integrates with **[Re-DB](https://www.github.com/re-view/re-db)** for managing app-wide, global state. Similar to Reagent, reads are logged during render to determine data dependencies. But we track reads based on re-db _patterns_, so a list of entities and attributes a component depends on is visible as plain data at runtime, and we can efficiently re-render based on changes in re-db even when the number of active components is large.

**Lifecycle Methods**

Reagent encourages the use of closures for setting up initial state of components. Re-View encourages direct use of React lifecycle methods.

**Hiccup syntax**

Reagent supports functions in the first position of hiccup forms:

```clj
[:div
 [my-greeting "Hello, world"]]
```

Re-View only accepts keywords in the `tag` position; components must be returned via ordinary function calls:

```clj
[:div
 (my-greeting "Hello, world")]
```


