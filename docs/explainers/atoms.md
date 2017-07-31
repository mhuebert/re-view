# Clojure Atoms

A Clojure [atom](https://clojure.org/reference/atoms) is a thing that can change. It is a container, which can hold any kind of Clojure value (eg. a string, a vector, a map). Change an atom by calling `swap!` or `reset!` on it.

Create an atom by passing `atom` an initial value (or nothing, for an empty atom).

```clj
(def favorite-number (atom 5))
```

Read the value of the atom by using `@`.

```clj
@favorite-number
;; => 5
```

`@` is just a shortcut for `deref` (_dereference_).
```clj
(= @favorite-number (deref favorite-number))
;; => true
```

Call `swap!` with an atom and a function. The atom's value changes to whatever the function returns.

```clj
(swap! favorite-number inc)
@favorite-number
;; => 6
```

You can pass `swap!` additional arguments after the function. The function will be called with the atom's value as the first argument, and then the extra argument(s).

```clj
(swap! favorite-number + 10)
@favorite-number
;; => 16
```

Call `reset!` to change the value of the atom to anything at all.

```clj
(reset! favorite-number 99)
@favorite-number
;; => 99
```

**Watching an atom**

Atoms are cool because you can watch them for changes. We do this using the `add-watch` function. 

Pass `add-watch` an atom, and a function (the 'watcher') that should be called whenever `swap!` or `reset!` is called on the atom. _Oops!_ There is one more thing. We also need to pass a 'key' for the watcher. We can use this 'key' later if we ever want to remove the watcher.

```clj
(add-watch favorite-number :print-it (fn [key ref old-state new-state]
                                        (println "The new value of the atom is: " new-state)))
```

We chose `:print-it` to be the key for the watcher. This means we can stop watching this atom by calling `(remove-watch favorite-number :print-it)`. But we don't want to do that yet.

Notice that the function we passed has four arguments.

1. **key:** the key we passed (`:print-it`)
2. **ref:** a reference to the atom itself
3. **old-state:** the value of the atom before the change
4. **new-state:** the value of the atom after the change

Also, be warned: our function is called every time `swap!` or `reset!` is called on the atom, _even if the atom's value did not change_. But this is not a problem, because the function is passed the old state as well as the new state, so we can always compare them.

```clj
(fn [key ref old-state new-state]
  (when (not= old-state new-state)
    (println "The new value of the atom is: " new-state)))
```
 
Now you know what atoms are, and how to use them!