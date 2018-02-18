# Re-DB

![badge](https://img.shields.io/clojars/v/re-db.svg)

Re-DB is a client-side data store for handling global state in ClojureScript apps. It was built in tandem with [Re-View](https://www.github.com/braintripping/re-view) to support views which automatically update when underlying data changes. It is inspired by Datomic and DataScript.

## Installation

In `project.clj`, include the dependency `[re-db "xx"]`.

## Background

ClojureScript apps usually store state in [atoms](https://www.re-view.io/docs/explainers/atoms) which are looked up via namespace or symbol references. With `re-db`, global state is stored in a single location, as a collection of maps (`entities`), each of which has a unique ID (under the `:db/id` key). Data is read via unique ID, or by indexed attributes. We spend less time thinking about 'locations' in namespaces (or bindings in closures) and more time thinking about data itself.

## Usage

It is normal to use just one re-db namespace, `re-db.d`, for reads and writes throughout an app.

```clj
(ns my-app.core
  (:require [re-db.d :as d]))
```

### Writing data

To write data, pass a collection of transactions to `d/transact!`. There are two kinds of transactions.

1. Map transactions are a succinct way to transact entire entities. A map __must__ have a `:db/id` attribute.

    ```clj
    {:db/id 1 :name "Matt"}

    ;; usage

    (d/transact! [{:db/id 1 :name "Matt"}])
    ```

2. Vector transactions allow more fine-grained control.

    ```clj
    ;; add an attribute
    [:db/add <id> <attribute> <value>]

    ;; retract an attribute
    [:db/retract-attr <id> <attribute> <value (optional)>]

    ;; retract an entity
    [:db/retract-entity <id>]

    ;; update an attribute
    [:db/update-attr <id> <attr> <f> <& args>]

    ;; usage

    (d/transact! [[:db/add 1 :name "Matt"]])

    ```

### Reading data

Read a single entity by passing its ID to `d/entity`.

```clj
(d/entity 1)
;; => {:db/id 1 :name "Matt"}
```

An entity pattern read (:e__) is logged.

Read an attribute by passing an ID and attribute to `d/get`.

```clj
(d/get 1 :name)
;; => "Matt"
```

An entity-attribute pattern read (:ea\_) is logged.

Read nested attributes via `d/get-in`.

```clj
(d/get-in 1 [:address :zip])
```

An entity-attribute pattern read (:ea_) is logged.

### Listening for changes

Use `d/listen` to be notified of changes to specific entities or patterns in the db. Five patterns are supported:

    Value Format         Pattern              Description
    id                   :e__                 entity pattern
    [id attr]            :ea_                 entity-attribute pattern
    [attr val]           :_av                 attribute-value pattern
    attr                 :_a_                 attribute pattern
   



Pass `d/listen` a map of the form `{<pattern> [<...values...>]}`, and a function that should be called when data that matches one of the patterns has changed. A listener will be called at most once per transaction.

Examples:
TODO: update examples to new syntax

```clj
;; entity
(d/listen! [[1 nil nil]] #(println "The entity with id 1 was changed"))

;; entity-attribute
(d/listen! [[1 :name nil]] #(println "The :name attribute of entity 1 was changed"))

;; attribute-value
(d/listen! [[nil :name "Matt"]] #(println "The value 'Matt' has been removed or added to the :name attribute of an entity"))

;; attribute
(d/listen! [[nil :name nil]] #(println "A :name attribute has been changed"))

;; tx-log
(d/listen! [:tx-log] #(println "The database has been changed"))
```

### Indexes

Use `d/merge-schema!` to update indexes.

```clj
(d/merge-schema! {:children {:db/index true, :db/cardinality :db.cardinality/many}})
```

### Finding entities

Use `d/entity-ids` and `d/entities` to find entities which match a collection of predicates, each of which should be:

1. An attribute-value **vector**, to match entities which contain the attribute-value pair. If the attribute is indexed, this will be very fast. Logs an attribute-value pattern read (:_av).
2. A **keyword**, to match entities that contain the keyword. Logged as an attribute pattern read (:_a_).
3. A **predicate function**, to match entities for which the predicate returns true.

`d/entities` logs an entity pattern read (:e__) for every entity returned.

### Pattern read logging

The `re-db.d/capture-patterns` macro logs read patterns which occur during execution. This is to support reactive views which update when underlying data changes.
