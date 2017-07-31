# Re-View

![badge](https://img.shields.io/clojars/v/re-view.svg)
![badge](https://img.shields.io/clojars/v/re-view-hiccup.svg)
![badge](https://img.shields.io/clojars/v/re-view-routing.svg)
![badge](https://img.shields.io/clojars/v/re-view-material.svg)
![badge](https://img.shields.io/clojars/v/re-view-prosemirror.svg)

Re-View is a library for building React apps in ClojureScript. It's a beginner-friendly tool that is also suitable for demanding, production-grade apps.

Website: [https://re-view.io](https://re-view.io)

## Objectives

- Readable code
- Precise and transparent reactivity/dataflow
- Convenient access to React lifecycle methods - do not try to hide the React component model
- A smooth 'upgrade continuum': simple components are extremely simple to create, while 'advanced' components are created by progressively adding information to simple components (no need to switch paradigms along the way)

## Motivation

Existing tools in the ClojureScript ecosystem, although excellent for their respective use cases, were found to be either too magical or too verbose for my particular needs. Re-View was originally "programmed in anger" (but with lotsa love) during the development of a [reactive-dataflow coding environment](http://px16.matt.is/).

## Getting started

Start by reading the [website](https://re-view.io) or the [quickstart](https://github.com/braintripping/re-view/tree/master/re_view#quickstart).

For the impatient, build an example app using our lein template:

```clj
lein new re-view my-example-app;
cd my-example-app;
lein figwheel;
;; open browser window to http://localhost:5300
```

## Navigating this repo

core + utilities

- [/re_view](/re-view/tree/master/re_view), the main thing you will use -- a ClojureScript view library built on top of React
- [/re_view_routing](/re-view/tree/master/re_view_routing), a small routing library
- [/re_view_hiccup](/re-view/tree/master/re_view_hiccup), a small hiccup parser

reusable components

- [/re_view_material](/re-view/tree/master/re_view_material), implementations of Google's Material Design components
- [/re_view_prosemirror](/re-view/tree/master/re_view_prosemirror), wrapper around [ProseMirror](http://prosemirror.net/)


docs & examples

- [/website](/re-view/tree/master/website), the source code for https://re-view.io
- [/docs](/re-view/tree/master/docs), the docs that re-view.io reads from
- Also check out the [source code for Maria](https://www.github.com/mhuebert/maria), a browser-based ClojureScript editor that uses re-view

