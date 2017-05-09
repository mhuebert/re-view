
### Changelog

0.3.14
- Sync with re-db listen! api change

0.3.13
- Fix bug in debug log for errors in component render


0.3.12
- Add `re-view.core/partial` to partially apply props to a view

0.3.11
- Custom keys in method maps are added using `set!` (& camelCase) to play well with advanced compilation, so
  the following should 'just work':
  
  ```clj
  (defview my-app
    {:my-greeting (fn [this] "hello, world!")}
    [this]
    [:div (.myGreeting this)])
  ```
- Remove `core.match` dependency
- Remove unused/deprecated namespaces, `re-view.subscriptions` and `re-view.shared`
- Remove `re-view.react` namespace, call React functions directly

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