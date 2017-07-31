# Re-View

![badge](https://img.shields.io/clojars/v/re-view.svg)
![badge](https://img.shields.io/clojars/v/re-view-hiccup.svg)
![badge](https://img.shields.io/clojars/v/re-view-routing.svg)
![badge](https://img.shields.io/clojars/v/re-view-material.svg)
![badge](https://img.shields.io/clojars/v/re-view-prosemirror.svg)

Re-View is a library for building React apps in ClojureScript. [https://re-view.io](https://re-view.io)

## Getting started

If you're new here, you probably want to check out the [website](https://re-view.io) or the [quickstart](https://github.com/braintripping/re-view/tree/master/re_view#quickstart)

Build an example app using our lein template:

```clj
lein new re-view my-example-app;
cd my-example-app;
lein figwheel;
;; open browser window to http://localhost:5300
```

## In this repo

The core:

- [re-view](/re-view/tree/master/re_view), the main thing you will use -- a ClojureScript view library built on top of React

Reusable components:

- [re-view-material](/re-view/tree/master/re_view_material), implementations of Google's Material Design components
- [re-view-prosemirror](/re-view/tree/master/re_view_prosemirror), wrapper around [ProseMirror](http://prosemirror.net/)

Utilities:

- [re-view-routing](/re-view/tree/master/re_view_routing), a small routing library
- [re-view-hiccup](/re-view/tree/master/re_view_hiccup), a small hiccup parser

Docs & examples:

- [website](/re-view/tree/master/website), the source code for https://re-view.io
- [docs](/re-view/tree/master/docs), the docs that re-view.io reads from

