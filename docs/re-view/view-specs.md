# View Specs

View specs are a way to add documentation and validation to components, inspired by Clojure's [Spec](https://clojure.org/about/spec).

## Benefits

1. Documentation - components "explain how to use themselves" when props are declared, with optional per-prop docstrings.

2. Debugging - Runtime validation of props and children to ensure correct usage and provide helpful error messages

3. Productivity - Efficient pruning of props that are passed down to child components.

## Usage

**defview** accepts a methods map, where we can include `:spec/props` and `:spec/children` keys. `:spec/props` should be a map which pairs keys with 'specs' that describe the prop:

```clj
(defview greeting
  {:spec/props {:name :String}}
  [this]
  [:div (str "Hello, " (get this :name) ".")])
```

Above, we used the built-in `:String` spec. A spec may be any of the following:

1. The keyword of a registered spec. The built-in specs are:
   ```clj
   :Boolean :String :Number :Function :Map :Vector :Element :Hiccup :SVG :Object :Keyword
   ```
2. A set of permissible values:
    ```clj 
    {:spec/props {:flavour #{:vanilla :chocolate}}}
    ```
3. A predicate function:
    ```clj 
    {:spec/props {:age #(> % 13)}}
    ```
3. A map containing a `:spec` key, which itself is a spec, alongside additional options.
    ```clj 
    {:spec/props {:age {:spec #(> % 13)
                        :required true}}}
    ```

### Spec options

With the map form, we can add metadata like `:doc` and `:required`.

```clj
(defview greeting
  {:spec/props {:name {:spec     :String
                       :doc      "The name of a person to be greeted by this component."
                       :required true}}}
  [this]
  [:div (str "Hello, " (get this :name) ".")])
```

| key | info |
| --- | --- |
| **:doc** (string) | docstring that describes what the prop is for, usage instructions, etc. |
| **:required** (boolean) | key *must* be present |
| **:pass-through** (boolean) | key will be passed on to child component (via `re-view.core/pass-props`)|

### Defining specs

For specs that we wish to re-use in multiple places, we call `defspecs` with a map of spec bindings:

```clj
(defspecs {::label {:spec :String
                    :doc "Label for a form input"}})
```

Now we can use the `::label` spec:

```clj
{:spec/props {:label ::label}}
```

### Specifying keys

Above, we've repeated ourselves, because the `::label` spec refers to the `:label` key in a props map. Under `:props/keys`, we can specify a set of registered specs, which will be paired with their equivalent non-namespaced prop keys:

```clj
{:spec/props {:props/keys #{::label}}
;; means the component expects a `:label` prop, which conforms to the `::label` spec
```

This is more concise.

Specs in `:props/keys` are matched to _simple_ (non-namespaced) keywords in props. `::label` matches `:label`, and so on.

Lastly, we can add `:spec/children` to spec the additional arguments passed to views. This should be a vector of specs, plus the special `:&` keyword, which behaves like the `&` in ordinary Clojure argument lists.

```clj
;; spec for a single :Element child
{:spec/children [:Element]}

;; spec for two children, a :Number and :Element
{:spec/children [:Number :Element]}

;; spec for any number of :Element children
{:spec/children [:& :Element]}

;; spec for a :Number followed by any number of :Element children
{:spec/children [:Number :& :Element]}
```

To recap, we can include the following keys in a component's method map:

| key | info |
| --- | --- |
| **:spec/props** | A map of the form `{<prop-key> <spec>}` |
| **:spec/children** | A vector of specs, aligned to the child args accepted by the component |

In the `:spec/props` map, we may include the special keys:

| key | info |
| --- | --- |
| **:props/keys** | A set of registered specs (keywords) of permissible props for the component |
| **:props/keys-req** | A set of registered specs representing _required_ props for the component |

**Why not just use Clojure Spec?**

While there are similarities between view specs and Clojure Spec, they do not solve precisely the same problems, and view specs were designed with particular requirements in mind:

1. Compiled bundle size: View specs support _runtime_ behaviour (eg. managing props flow, specifying prop defaults), so whatever 'spec' code we choose will be included in app bundles. Clojure Spec has many dependencies and greatly increases bundle sizes; this problem is even more extreme with self-hosted ClojureScript, which Re-View is designed to support. `re-view.view-spec` is ~130 LOC.
2. Documentation: Clojure Spec does not yet support docstrings.
3. Ease of learning: We'd like view specs to be as simple to learn and use as React prop-types. Clojure Spec's great power comes at the cost of a steeper learning curve. 

One can certainly use Clojure Spec _in addition_ to view specs, for more inspection and testing during development.
