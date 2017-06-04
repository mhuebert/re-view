(ns re-view-prosemirror.example
  (:require [re-view.core :as v :refer [defview view]]
            [re-view.example.helpers :as h]
            [re-view.hoc :as hoc]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.toolbar :as prose-toolbar]))

(set! *warn-on-infer* true)

(defn toolbar [^js/pm.EditorView pm-view]
  (->> prose-toolbar/all-toolbar-items
       (map (fn [toolbar-item]
              (toolbar-item (.-state pm-view) (.-dispatch pm-view))))))

(defview RichTextMarkdown
         "A [ProseMirror](http://prosemirror.net/) rich text editor which reads and writes Markdown."
         [{:keys [view/state view/props on-mount on-dispatch]}]
         [:.shadow-4.flex-auto.bg-white.black
          (some->> (:prosemirror/view @state)
                   (toolbar)
                   (conj [:div.bb.bw2.b--light-gray.ph1]))

          (prose/Editor (assoc props
                          :on-mount (cond-> #(swap! state assoc :prosemirror/view %2)
                                            on-mount (v/compseq on-mount))
                          :on-dispatch (cond-> (fn [_ ^js/pm.EditorView pm-view]
                                                 (swap! state assoc
                                                        :prosemirror/state (.-state pm-view)
                                                        :prosemirror/dispatch (.-dispatch pm-view)))
                                               on-dispatch (v/compseq on-dispatch))))])

(def examples-data
  (let [example-output (atom [{:markdown nil}])
        update-markdown (fn [^js/React.Component this _]
                          (swap! example-output assoc-in [0 :markdown] (.serialize this)))]
    [{:kind      :component
      :component RichTextMarkdown
      :prop-atom (atom [{:default-value "*Ingredients:*\n* **One cup** tomato juice

| x | y | z |
|---|---|---|
| a | b | c |
| d | e | f |

    Code

Paragraph  "
                         :on-mount      #(js/setTimeout (partial update-markdown %) 0)
                         :on-dispatch   update-markdown}])
      :wrap      #(hoc/bind-atom (view [{:keys [markdown]}]
                                     [:.flex.items-stretch.pv3.flex-wrap.w-100
                                      [:.flex-auto.mw6 %]
                                      [:.pv4.ph3.code.f6
                                       {:style {:white-space "pre-wrap"}} markdown]]) example-output)}]))