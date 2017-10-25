# Re-View Material

![badge](https://img.shields.io/clojars/v/re-view.material.svg)

Components for [Re-View](https://www.github.com/braintripping/re-view) built on Google's [material-components-web](https://github.com/material-components/material-components-web) library.

ALPHA

Dev: `webpack -w -p`

## How to contribute

Google's instructions for creating components based on their MDC framework start here: https://material.io/components/web/docs/framework-integration/#the-advanced-approach-using-foundations-and-adapters

We use the 'Advanced' approach as described on that page. 

### Current status

The first version of `re-view/material` was built before ClojureScript had support for `npm` dependencies, and was therefore monolithic, and JS deps were bundled with webpack. As of October/2017, that approach has been deprecated, and we have simultaneously migrated to the [shadow-cljs](https://github.com/thheller/shadow-cljs/) build tool for development. 

Some next steps:

1. Put each component in its own namespace. This will satisfy Google's requirement for libraries that they promote to `Serve components in an à-la-carte delivery model`.

- Currently, each component has one part in `material.core` and another part (its foundation/adapter) in `material.mdc`. 
- All component-specific code should be in a namespace like `material.components.component-name`
- All reusable utility code should remain in `material.mdc`
    
The result of this step is that users can require a single component and it will pull in _only_ the required code.

2. Figure out a way for users to sanely include the CSS just for the components they use, in their app.

3. Add more components

### Our approach

**Docstrings and View Specs**

The preview panels you see when you expand a component on the [component library](https://re-view.io/components) page are fully auto-generated, based on re-view's support for docstrings and [view specs](https://re-view.io/docs/re-view/view-specs):

![Example](https://i.imgur.com/BLd9RdP.png)

> Note: [view specs](https://re-view.io/docs/re-view/view-specs) are not implemented with Clojure Spec, although they share similarities. They are a light-weight construct designed to provide view-specific functionality at runtime with little overhead.

Components should include docstrings using the following format:

```
(defview Text
  "Allow users to input, edit, and select text. [More](https://material.io/guidelines/components/text-fields.html)"
  ...)
```

Wherever reasonable, use the same description for the text as appears on the official site, and always end the docstring with a link to its official guideline page.

**`ext` components**

There may be occasions where we wish to provide a component that is not part of the Material Design system. This should be rare and is not the focus of the library. Current exceptions include `re-view.material.persisted.core`, which wraps the Text widget with help for making a component manage local/persisted state, and `re-view.material.ext`, which has a helper for use with components that need to be 'opened'. These should not be relied on and will likely be moved elsewhere (if they remain at all).
