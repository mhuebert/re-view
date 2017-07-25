# Changelog

## 0.1.8
- Fixed behaviour of `wrap-props` option to `element`. Also, pass `tag` to `wrap-props`.

## 0.1.4
- Added `:create-element` option to override `React.createElement`
- Moved to re-view.hiccup namespace

## 0.1.3
- Added HTML string output in the `react/html` (client-side) and `react/server` (server-side) namespaces
- Added tests

## 0.1.2

- Support namespaced custom elements, such that `:amazon/effect` becomes `<amazon:effect />`
- Only `camelCase` attributes that React handles specifically -- custom attributes should be left alone