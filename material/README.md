# Re-View Material

![badge](https://img.shields.io/clojars/v/re-view.material.svg)

Components for [Re-View](https://www.github.com/braintripping/re-view) built on Google's [material-components-web](https://github.com/material-components/material-components-web) library.

ALPHA

Dev: `webpack -w -p`

## How to contribute

Google's instructions for creating components based on their MDC framework start here: https://material.io/components/web/docs/framework-integration/#the-advanced-approach-using-foundations-and-adapters

We use the 'Advanced' approach as described on that page. 

### Current status

The first version of `re-view/material` was built before ClojureScript had support for `npm` dependencies, so I packaged all the JS deps into a single file using webpack. That led to a monolithic design where I put all of the component's framework/adapter code into `re-view.material.mdc`, and all of the components into `re-view.material.components`. 

Thankfully, ClojureScript added support for requiring deps directly from npm, so in October/2017 I updated this library to use the new style of js deps (at the same time, I switched to using the [shadow-cljs](https://github.com/thheller/shadow-cljs/) build tool). But I haven't had time to refactor the library into the modular style that is now possible.

So, next steps:

1. Put each component in its own namespace. This will satisfy Google's [requirement](https://material.io/components/web/docs/framework-integration/#examples) for libraries that they promote to `"Serve components in an à-la-carte delivery model"`. (Currently, each component has one part in `material.core` and another part (its foundation/adapter) in `material.mdc`.)

    - [ ] Put component-specific code in a namespace like `material.components.component-name`
    - [ ] Keep only reusable utility code in `material.mdc`


2. Figure out a way for users to sanely include the CSS just for the components they use, in their app.

### Our approach

**Docstrings and View Specs**

By using re-view's support for docstrings and [view specs](https://re-view.io/docs/re-view/view-specs), we are able to _autogenerate_ the [component library](https://re-view.io/components):

![Example](https://i.imgur.com/BLd9RdP.png)

_Note:_ [view specs](https://re-view.io/docs/re-view/view-specs) are not implemented with Clojure Spec, although they share similarities. They are a light-weight construct designed to provide view-specific functionality at runtime with little overhead.

View specs have the additional benefit of allowing users of this library to have their usage of components (props, children) validated during development.

Docstrings should use the following format:

```
(defview Text
  "Allow users to input, edit, and select text. [More](https://material.io/guidelines/components/text-fields.html)"
  ...)
```

Wherever reasonable, use the same description for the text as appears on the official site, and always end the docstring with a link to its official guideline page.

**additional components**

The goal of this library is _only_ to faithfully implement Google's official web components. We are not trying to make original design decisions here, only good _implementation_ decisions. 

Any _additional_ components which are desired but are not part of the mdc system should be published into a different library/namespace.

(Current exceptions to this include `re-view.material.persisted.core`, which wraps the Text widget with help for making a component manage local/persisted state, and `re-view.material.ext`, which has a helper for use with components that need to be 'opened'. These should be considered temporary.)

### Development environment

The recommended way to work on this library is via the `website` project, which includes usages of all the components. Instructions for getting that set up are in the website [readme](https://github.com/braintripping/re-view/blob/master/website/README.md).
