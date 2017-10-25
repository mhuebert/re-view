## re-view website

This is what you see at https://re-view.io.

----

### Development


First, clone locally and install deps:

```
git clone https://github.com/braintripping/re-view.git;
cd re-view/website;
npm install;
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
