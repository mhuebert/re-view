# Re-View

![badge](https://img.shields.io/clojars/v/re-view.svg)
![badge](https://img.shields.io/clojars/v/re-view-hiccup.svg)
![badge](https://img.shields.io/clojars/v/re-view-routing.svg)
![badge](https://img.shields.io/clojars/v/re-view-material.svg)
![badge](https://img.shields.io/clojars/v/re-view-prosemirror.svg)

Re-View is a library for building React apps in ClojureScript. [https://re-view.io](https://re-view.io)

## Getting started

Start by reading the [website](https://re-view.io) or the [quickstart](https://github.com/braintripping/re-view/tree/master/re_view#quickstart)

For the impatient, build an example app using our lein template:

```clj
lein new re-view my-example-app;
cd my-example-app;
lein figwheel;
;; open browser window to http://localhost:5300
```

## Navigating this repo

### core

- [/re_view](/re-view/tree/master/re_view), the main thing you will use -- a ClojureScript view library built on top of React

### reusable components

- [/re_view_material](/re-view/tree/master/re_view_material), implementations of Google's Material Design components
- [/re_view_prosemirror](/re-view/tree/master/re_view_prosemirror), wrapper around [ProseMirror](http://prosemirror.net/)

### utilities

- [/re_view_routing](/re-view/tree/master/re_view_routing), a small routing library
- [/re_view_hiccup](/re-view/tree/master/re_view_hiccup), a small hiccup parser

### docs & examples

- [/website](/re-view/tree/master/website), the source code for https://re-view.io
- [/docs](/re-view/tree/master/docs), the docs that re-view.io reads from

