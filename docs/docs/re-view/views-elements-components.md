# Views, Elements, and Components

Composing and mounting a tree of React components is a multi-stage process.

First, we define **view functions** using `defview`. These are functions which accept props and children as arguments, and return React **elements**.

A React **element** is only a specification for what should go on the screen. You can't see it until it is rendered, at which point it becomes a React **component**. A React element isn't attached to the DOM yet and you can't do much with it.

A React **component** is an element that has been mounted to the DOM. It maintains state, and goes through 'lifecycles' of updates as it receives new props and state. You can call methods on a component, and look up the component's DOM node.

Learn more in the React doc, [React Components, Elements, and Instances](https://facebook.github.io/react/blog/2015/12/18/react-components-elements-and-instances.html).