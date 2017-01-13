## Changelog

### 0.1.8

- `:db.cardinality/many` attributes should be passed one at a time in a tx: `[:db/add 1 :children val]`,
 or as a set if transacting a map form: `{:db/id 1 :children #{"child1", "child2"}}`.
 Note that passing cardinality-many values in a map does not retract existing values for that attribute.

 - `:db.type/ref` has been added. `re-db/touch` performs a reverse ref lookup for an entity.

 - Internal names in the re-db data structure have been changed to clarify that we keep a `:eav` index
 for all attributes, a `:ave` index for indexed/unique entities, and a `:vae` index for `ref` attributes.
  A `:aev` index may be added in the future to support more flexible queries.

### 0.1.4

- `listen!` returns an unlisten! function for the supplied arguments.