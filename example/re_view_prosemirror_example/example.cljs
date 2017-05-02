(ns re-view-prosemirror-example.example
  (:require [re-view.core :as v :refer [defview view]]
            [re-view-example.helpers :as h]
            [re-view-prosemirror.markdown :as prose]
            [re-view-prosemirror.toolbar :as prose-toolbar]))

(set! *warn-on-infer* true)

(defview editor-with-toolbar
         "A [ProseMirror](http://prosemirror.net/) rich text editor which reads and writes Markdown."
         [{:keys [view/state view/props on-mount on-dispatch]}]
         (let [{:keys [prosemirror/view]} @state]
           [:.shadow-4.flex-auto.bg-white.black
            (when-not (nil? view)
              [:div.bb.bw2.b--light-gray.ph1
               (map #(% (.-state view) (.-dispatch view)) prose-toolbar/all-toolbar-items)])

            (prose/Editor (assoc props
                            :on-mount (cond-> #(swap! state assoc :prosemirror/view %2)
                                              on-mount (v/compseq on-mount))
                            :on-dispatch (cond-> (fn [_ ^js/pm.EditorView pm-view]
                                                  (swap! state assoc
                                                         :prosemirror/state (.-state pm-view)
                                                         :prosemirror/dispatch (.-dispatch pm-view)))
                                                 on-dispatch (v/compseq on-dispatch))))]))

(def examples-data
  (let [example-state (atom {:markdown nil
                             :view     nil})
        update-markdown (fn [this _]
                          (swap! example-state assoc :markdown (.serialize this)))]
    [{:kind      :component
      :label     "Rich Text"
      :tags      #{:forms}
      :component editor-with-toolbar
      :prop-atom (atom {:default-value "*Ingredients:*\n* **One cup** tomato juice"
                        :on-mount      #(js/setTimeout (partial update-markdown %) 0)
                        :on-dispatch   update-markdown})
      :wrap      #(h/with-prop-atom* nil (view [{:keys [markdown]}]
                                               [:.flex.items-stretch.pv3.flex-wrap.w-100
                                                [:.flex-auto.mw6 %]
                                                [:.pv4.ph3.code.f6
                                                 {:style {:white-space "pre-wrap"}} markdown]]) example-state)}]))