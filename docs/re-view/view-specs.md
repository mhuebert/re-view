# View Specs

View specs are a way to specify exactly what props and children each component expects, inspired by Clojure's [Spec](https://clojure.org/about/spec).

### Why is this important?

Clarity / documentation -- quickly identify how to use a view by looking at its spec.

Debugging -- During develoment, we __validate__ the arguments passed to each component to verify correct usage and provide **helpful error messages**

Productivity -- Automate the pruning of props that are passed down to child components.

### Usage

You may recall that **defview** accepts a methods map, for lifecycle methods and so on. We can include a `:view/spec` key here, containing a spec map describing the `:props` and `:children` expected by a component.

Inside the spec map, we can put a `:props` map, which pairs prop keys with specs (more on what a spec is later). Here's a quick example which states that the `greeting` view expects a `:name` prop, which is a string:

```clj
(defview greeting
  {:view/spec {:props {:name :String
                       :doc "The name of a person to be greeted by this component."
                       :required true}}}
  [this]
  [:div (str "Hello, " (get this :name) ".")])
```

A spec may be one of the following:

1. The keyword of a registered spec. The built-in specs are:
   ```clj
   :Boolean :String :Number :Function :Map :Vector :Element :Hiccup :SVG :Object :Keyword
   ```
2. A predicate function or set.
3. A map containing a `:spec` key, which itself is a spec.

Specs are registered using `re-view.view-spec/defspecs`, which accepts a map of the form `{<keyword> <spec>}`. Let's define a `::label` spec, which should be a string along with a docstring.

```clj
(defspecs {::label {:spec :String
                    :doc "Label for a form input"}})
```

Now, how do we use `::label`?

First, we can include it in our view spec `:props` map:

```clj
{:view/spec {:props {:label ::label}}}
```

But this is verbose. We can also include `:props/keys`, a vector of namespaced keywords that have been registered as specs:

```clj
{:view/spec {:props/keys [::label]}}
;; means the component expects a `:label` prop, which conforms to the `::label` spec
```

This is a more concise way to declare props, and means we can reuse keys among many components.

Specs in `:props/keys` are matched to _unqualified_ keywords in props. `::label` matches `:label`, and so on.

Lastly, we can spec `:children`, the additional arguments passed to views. The `:children` key is a vector of specs, plus the special `:&` keyword, which behaves just like `&` in ordinary Clojure argument lists.

```clj
;; spec for a single :Element child
{:view/spec {:children [:Element]}}

;; spec for two children, a :Number and :Element
{:view/spec {:children [:Number :Element]}}

;; spec for any number of :Element children
{:view/spec {:children [:& :Element]}}

;; spec for a :Number followed by any number of :Element children
{:view/spec {:children [:Number :& :Element]}}
```

To recap, the :view/spec map accepts the following keys:

| key | info |
| --- | --- |
| **:props** | A map of the form `{<prop-key> <spec>}` |
| **:props/keys** | A vector of registered spec keys |
| **:children** | A vector of specs, aligned to the child args accepted by the component |

### Spec Options

A spec must contain a `:spec` key, but it may also include:

| key | info |
| --- | --- |
| **:doc** (string) | docstring that describes what the prop is for, usage instructions, etc. |
| **:required** (boolean) | key *must* be present |
| **:pass-through** (boolean) | key will be passed on to child component (via `re-view.core/pass-props`)|



**Why not just use Clojure Spec?**

You can absolutely use Clojure Spec _in addition_ to view specs for additional inspection and testing, but we don't think it's a good fit for being a hard dependency of Re-View.

1. View specs are designed to support runtime behaviour (eg. managing props flow, specifying prop defaults), and relying on Spec at runtime would add to compiled bundle sizes (`re-view.view-spec` is only ~100 LOC).
2. We care a lot about docstrings, which Spec doesn't support (yet).
3. We want to support patterns that Spec does not... eg. inline spec'ing of individual one-off map keys, which are common in UI views, and treating unqualified keys as the default case, which is the norm for Hiccup and all other similar tools.
