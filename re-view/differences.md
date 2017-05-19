# Differences

## Reagent

If you are coming from Reagent, two things to be aware of:

1. Components must be called as functions

    Reagent supports putting a component in the tag position of a Hiccup form, which ReView does not.
   
    ```clj
    ;; DO 
    (my-component "hello")
    
    ;; DON'T - will NOT work in Re-View
    [my-component "hello"]
   
    ```
2. No 'reactive atoms'

    There is one **[state atom](getting-started#state-atoms)** per component; when it changes, the component will update. There is no other automatic tracking of atoms.

    In place of reactive atoms, Re-View integrates with **[Re-DB](https://www.github.com/re-view/re-db)** for managing app-wide, global state. Just as with reactive atoms, reads are logged during render to determine data dependencies. Reads are logged as a list of re-db _patterns_ which are visible as plain data at runtime.


