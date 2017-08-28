(ns re-view-prosemirror.example
  (:require [re-view.core :as v :refer [defview view]]
            [re-view.example.helpers :as h]
            [re-view.hoc :as hoc]
            react
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.defaults :as defaults]
            [re-view-prosemirror.example-toolbar :as prose-toolbar]))

(set! *warn-on-infer* true)

(defn toolbar [^js/pm.EditorView pm-view]
  (->> prose-toolbar/all-toolbar-items
       (map (fn [toolbar-item]
              (toolbar-item (.-state pm-view) (.-dispatch pm-view))))))

(defview RichTextMarkdown
         "A [ProseMirror](http://prosemirror.net/) rich text editor which reads and writes Markdown."
         [{:keys [view/state view/props on-dispatch] :as this}]
         [:.shadow-4.flex-auto.bg-white.black.pa3
          (some->> (:prosemirror/view @state)
                   (toolbar)
                   (conj [:div.bb.bw2.b--light-gray.ph1]))
          (prose/Editor (merge props
                               {:input-rules defaults/input-rules
                                :keymap      defaults/keymap
                                :ref         #(when % (swap! state assoc :prosemirror/view (.pmView %)))
                                :on-dispatch (fn [& args]
                                               (apply on-dispatch args)
                                               (v/force-update this))}))])

(def sample-markdown-text "*Ingredients:*\n* **One cup** tomato juice

| x | y | z |
|---|---|---|
| a | b | c |
| d | e | f |

[a link](https://maria.cloud)

    Code

Paragraph  ")

(def examples-data
  (let [example-output (atom [{:markdown sample-markdown-text}])
        update-markdown (fn [^react/Component this _]
                          (swap! example-output assoc-in [0 :markdown] (.serialize this)))]
    [{:kind      :component
      :component RichTextMarkdown
      :prop-atom (atom [{:default-value sample-markdown-text
                         :on-dispatch   update-markdown}])
      :wrap      #(hoc/bind-atom (view [{:keys [markdown]}]
                                       [:.w-100
                                        [:div %]
                                        [:.code.f6.ma3
                                         {:style {:white-space "pre-wrap"}} markdown]])
                                 example-output)}]))