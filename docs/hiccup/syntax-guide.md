## Hiccup Syntax Intro

Introduction for first-timers. Also: [full docs](overview)

----

## Get started

Create a div.

```clj
[:div]
```

Add the class 'red'.
```clj
[:div.red]
```

Add the ID 'my-app'.
```clj
[:div#my-app.red]
```

Add an inline style. Set the font size to be 14.
```clj
[:div#my-app.red {:style {:font-size 14}]}
```

Create an empty text input element.
```clj
[:input {:type "text}]
```

Make the input a [controlled component](https://facebook.github.io/react/docs/forms.html#controlled-components) by specifying a value.
```clj
[:input {:type "text" 
         :value "Hello"}]
```

You'll want to change the value, so specify an `onChange` callback. **hint:** in Hiccup we use dashes instead of camelCase.
```clj
[:input {:type "text" 
         :value "Hello"
         :on-change (fn [e] ...)}]
```

Put a label above the input. Connect the `:input` and `:label` by using `:id` and `:for` attributes.

```clj
[:label {:for "greeting"} "Please enter a greeting."]
[:input#greeting {:type "text" 
                  :value "Hello"
                  :on-change (fn [e] ...)}]
```

Create a paragraph with a sentence of text which includes a link to Apple's website. The link should have an `:href` attribute.

```clj
[:p 
  "Why are you advertising for " [:a {:href "https://www.apple.com"} "Apple?"] ]
```

### Adding classes

There are three ways to add classes, and each has a purpose. 

The keyword method is concise:

```clj
[:div.active]
```

The `:class` attribute is more flexible because we can choose the class at runtime, based on conditions:

```clj
[:div {:class (if active? "active" "inactive")}]
```

The `:classes` key is most flexible. We can add multiple classes at runtime, based on conditions:

```clj
[:div {:classes [(when active? "active")
                 (when admin? "edit-mode")]}]
```

All classes will be combined:

```clj
[:div.text-center {:class "text-shadow"
                   :classes ["red" "big-border"]}]
;; => <div class="text-center text-shadow red big-border"></div>
```

## Include React components

You can React elements in the body of a hiccup form:

```clj
[:div (my-react-view {:label "Hello, world"})]
```

You **can't** use react elements in the place of keywords.

```clj
;; DOES NOT WORK
[my-react-view {:label "Hello, world"}]
```

## Lists and sequences 

You can include lists and sequences in the body of an element. The following are valid and will render equivalently:

```clj

[:div (for [n [1 2 3]]
        [:span n]))]

[:div (map (fn [n] [:span n]) [1 2 3])]

[:div '([:span 1] [:span 2] [:span 3])]

[:div [:span 1] [:span 2] [:span 3]]

```

(Remember, this only works for Clojure seqs and lists, *not* vectors or sets.)
