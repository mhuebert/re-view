## Changelog

### 0.1.8

- ~40% speed improvement
- :cardinality/many attributes must be passed as sets, eg/
  [:db/add 1 :my-attr #{"new-val"}] or [:db/retract-attr 1 :my-attr #{"old-val"}]. 
  Note that passing cardinality-many values in a map does not retract existing values for that attribute.
- rename db/has? to db/contains?
- `:db.type/ref` has been added. `re-db/touch` performs a reverse ref lookup for an entity.

 - Indexes in the re-db data structure are now named: `:eav`
 for all attributes, a `:ave` index for indexed/unique entities, and a `:vae` index for `ref` attributes.
  A `:aev` index may be added in the future to support faster queries.

### 0.1.4

- `listen!` returns an unlisten! function for the supplied arguments.