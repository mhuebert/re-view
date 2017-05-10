# State

In programming, when we talk about _state_, we mean information that can change over time, and is stored somewhere in memory. A program is 'stateful' if it _remembers_ information over time, and 'stateless' if it has no memory of the past.

## In React

A React component for a dropdown menu may keep track of whether it is 'open' or 'closed'. This would be the component's state. The component will render differently depending on its current state, and allow users to change its state by clicking or tapping.

A React component that always displays the same thing (eg. a paragraph element which always displays the same text) has no state.

In Re-View, each component has a [state atom](../re-view/getting-started#state-atom) for this purpose.

## In ClojureScript

In ClojureScript we use [atoms](atoms) to keep track of state.