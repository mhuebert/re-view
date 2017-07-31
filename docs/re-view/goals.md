# Goals

## Beginner-friendly

Docs should be friendly and not assume previous experience with Clojure(Script).

Advanced functionality is **opt-in**. Start simple, add power later.

Helpful error messages.

## Powerful

Complete access to React component lifecycle (for production apps, this is the common case).

Good performance even for large apps, no need to switch libraries when transitioning from 'beginner' to intermediate/advanced.

## Reactive dataflow without mystery

Updating components when referenced data has changed should be automatic (like Excel). **But** reactivity should not involve hidden state that is hard to debug.

## Support component re-use

Inline documentation: "What does this component do, and how can I use it?" should always be clear. 

Publishing component libraries should be encouraged. Encourage MINIMAL extra dependencies: rely on Google Closure Library where possible. 
