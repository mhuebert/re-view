# Re-DB

![badge](https://img.shields.io/clojars/v/re-db.svg)

Re-DB is a reactive client-side data store for handling global state in ClojureScript apps. It was built in tandem with [re-view](/mhuebert/re-view) to support views which automatically update when underlying data changes. It is inspired by Datomic and DataScript.

# Installation

In `project.clj`, include the dependency `[re-db "xx"]`.

# Background

ClojureScript apps usually store state in atoms (because state can change) which are looked up via namespace or symbol references. `re-db` offers an alternative perspective: all of an app's global state is stored in a single location, as a collection of maps (`entities`), each of which has a unique ID (under the `:db/id` key). Data is read via unique ID, or by indexed attributes. We spend less time thinking about 'locations' in the namespaces of our app, and more time thinking about data itself.

# Usage

It is normal to use just one re-db namespace, `re-db.d`, for reads and writes throughout an app.

```clj
(ns my-app.core
  (:require [re-db.d :as d))
```

## Writing data

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

## Reading data

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

An entity-attribute pattern read (:ea_) is logged.

Read nested attributes via `d/get-in`.

```clj
(d/get-in 1 [:address :zip])
```

An entity-attribute pattern read (:ea_) is logged.

## Listening for changes

Use `d/listen!` to be notified of changes to specific entities or patterns in the db. Five patterns are supported:

    Value                Pattern name         Description
    [id _ _]             :e__                 entity pattern
    [id attr _]          :ea_                 entity-attribute pattern
    [_ attr val]         :_av                 attribute-value pattern
    [_ attr _]           :_a_                 attribute pattern
    [_ _ _] or :tx-log   :___                 Matches all transactions

_\__ may be a quoted '\_ or `nil`.

Pass `d/listen!` a collection of patterns, and the function that should be called when data that matches one of the patterns has changed. A listener will be called at most once per transaction.

Examples:

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

## Indexes

Use `d/merge-schema!` to update indexes.

```clj
(d/merge-schema! {:children {:db/index true, :db/cardinality :db.cardinality/many}})
```

## Finding entities

Use `d/entity-ids` and `d/entities` to find entities which match a collection of predicates, each of which should be:

1. An attribute-value **vector**, to match entities which contain the attribute-value pair. If the attribute is indexed, this will be very fast. Logs an attribute-value pattern read (:_av).
2. A **keyword**, to match entities that contain the keyword. Logged as an attribute pattern read (:_a_).
3. A **predicate function**, to match entities for which the predicate returns true.

`d/entities` logs an entity pattern read (:e__) for every entity returned.

# Pattern read logging

The `re-db.d/capture-patterns` macro logs read patterns which occur during execution. This is to support reactive views which update when underlying data changes.