## Changelog

### 0.1.9
- All DB listeners are now called with the tx-report map, instead of data related to the pattern(s) the listener was registered on.
- `listen!` takes a vector of patterns instead of multiple args.
- `entity-ids` and `entities` take a vector of patterns instead of multiple args.

### 0.1.8

- The `:eav` index is now a sorted-map (from `clojure.data.avl`) which supports `subrange`. This is primarily to support path-based id attributes for hierarchical schemas.
- Overall transaction time reduced by ~60%, w/ a further 30% reduction with option `{:notify false}`, which supercedes the :mute option.
- :cardinality/many attributes must be passed as sets, eg/
  [:db/add 1 :my-attr #{"new-val"}] or [:db/retract-attr 1 :my-attr #{"old-val"}].
- Passing a cardinality-many value in a map replaces the existing value for that attribute (retracts previous members that do not exist in new value)
- rename db/has? to db/contains?
- `:db.type/ref` has been added. `re-db/touch` performs a reverse ref lookup for an entity.

 - Indexes in the re-db data structure are now named: `:eav`
 for all attributes, a `:ave` index for indexed/unique entities, and a `:vae` index for `ref` attributes.
  A `:aev` index may be added in the future to support faster queries.

### 0.1.4

- `listen!` returns an unlisten! function for the supplied arguments.