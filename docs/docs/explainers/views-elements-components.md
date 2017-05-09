# Views, Elements, and Components

How does a React component get rendered to the screen?

First, we define **view functions** using `defview`. These are functions which accept props and children as arguments, and return React **elements**.

A React **element** is only a specification for what should go on the screen. It consists of a component class (or _type_), and the props/children it was called with. An element isn't attached to the DOM yet, and you can't do much with it. It is immutable.

A React **component** is an element that has been mounted to the DOM. It maintains state, and goes through 'lifecycles' of updates as it receives new props and state. You can call methods on a component, and look up the component's DOM node.

Learn more in the React docs for [Components, Elements, and Instances](https://facebook.github.io/react/blog/2015/12/18/react-components-elements-and-instances.html).