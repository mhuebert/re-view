# Re-View Material

![badge](https://img.shields.io/clojars/v/re-view.material.svg)

Components for [Re-View](https://www.github.com/braintripping/re-view) built on Google's [material-components-web](https://github.com/material-components/material-components-web) library.

ALPHA

Dev: `webpack -w -p`

### How to contribute
----

Google's instructions for creating components based on their MDC framework start here: https://material.io/components/web/docs/framework-integration/#the-advanced-approach-using-foundations-and-adapters

We use the 'Advanced' approach as described on that page. 

#### Current status

The first version of `re-view/material` was built before ClojureScript had support for `npm` dependencies, and was therefore monolithic, and JS deps were bundled with webpack. As of October/2017, that approach has been deprecated, and we have simultaneously migrated to the [shadow-cljs](https://github.com/thheller/shadow-cljs/) build tool for development. 

Some next steps:

1. Put each component in its own namespace. 

- Currently, each component has one part in `material.core` and another part (its foundation/adapter) in `material.mdc`. 
- All component-specific code should be in a namespace like `material.components.component-name`
- All reusable utility code should remain in `material.mdc`
    
The result of this step is that users can require a single component and it will pull in _only_ the required code.

2. Figure out a way for users to sanely include the CSS just for the components they use, in their app.

3. Add more components

4. Tests

5. We'll get there yet

