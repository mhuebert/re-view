## re-view website

This is what you see at https://re-view.io.

----

### Development

First, install deps:

```
npm install
```


Build and watch ClojureScript:

```
npm run watch;
```

If successful, you can view the result at: http://localhost:8706

[shadow-cljs](https://github.com/thheller/shadow-cljs/) is our build tool, config is in `shadow-cljs.edn`.

Build and watch CSS:

```
webpack -p -w
```
