(ns re-view-example.helpers
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [clojure.string :as string])
  (:require-macros [re-view-example.helpers]))

(defn key-field
  "View key in editor"
  [k]
  (str (some-> (namespace k) (str "/")) (name k)))

(defn heading [level label]
  [(case level 1 :h1.f2.normal.bw2.bt.b--light-gray.pt3
               2 :h2.f3.normal
               3 :h3.f5.normal)
   {:id (str "h" level "-" (string/replace label #"\W" "-"))} label])

(defn value-field
  "Recursively view and edit Clojure values"
  [prop-atom path]
  (let [v (get-in @prop-atom path)
        set-val! #(swap! prop-atom assoc-in path %)
        id (->> path
                (map name)
                (string/join "/"))]
    [:.dib {:key id}
     (cond (fn? v) [:.i.ph2 "fn"]
           (string? v) (ui/Input {:value     v
                                  :class     "ba bw1 f7 b--moon-gray ph1 lh-copy mh1"
                                  :id        id
                                  :on-change #(set-val! (.. % -target -value))})
           (boolean? v) (ui/Checkbox {:checked   v
                                      :id        id
                                      :dense     true
                                      :on-change #(set-val! (boolean (.. % -target -checked)))})
           (vector? v) (if (= :svg (first v))
                         [:.i.ph2 "svg"]
                         [:.ph2.relative
                          [:.absolute.top-0.left-0.di.b "["]
                          (interpose [:br] (map (fn [i] (value-field prop-atom (conj path i))) (range (count v))))
                          [:.absolute.bottom-0.right-0.di.b "]"]])
           :else [:.ph2 (str v)])]))

(defview state-editor
  "Editor for atom containing Clojure map"
  {:key          :label
   :did-mount    (fn [this a] (add-watch a :prop-editor #(v/force-update this)))
   :will-unmount (fn [_ a] (remove-watch a :prop-editor))}
  [{:keys [label]} prop-atom]
  (let [set-val! (fn [k v] (swap! prop-atom assoc k v))]
    [:div
     (when label [:.f6.b.ph2.pv1 label])
     (if-let [props (some-> prop-atom deref seq)]
       [:table.f7
        [:tbody
         (for [[k v] props
               :let [id (name k)]
               :when (not= k :key)]
           [:tr
            [:td.b.o-60 (key-field k)]
            [:td.pl3 (value-field prop-atom [k])]])]]
       [:.gray.i.mv2.tc.f7 "No Props"])]))

(defview with-prop-atom*
  "Calls component with value of atom & re-renders when atom changes."
  {:key          (fn [_ _ prop-atom]
                   (let [{:keys [key id name]} (some-> prop-atom deref)]
                     (or key id name)))
   :did-mount    (fn [this component atom]
                   (some-> atom
                           (add-watch this (fn [_ _ old new]
                                             (when (not= old new)
                                               (v/force-update this))))))
   :will-unmount (fn [this _ atom]
                   (some-> atom
                           (remove-watch this)))}
  [_ component atom & children]
  (apply component (some-> atom deref) children))

(defview example-inspector
  {:key           :label
   :initial-state (fn [{:keys [prop-atom]}] prop-atom)}
  [{:keys [component orientation label view/state children]
    :or   {orientation :horizontal}}]
  [:div
   (cond->> label
            (string? label) (heading 2))
   [:.mv3.flex.items-center
    {:class (case orientation :horizontal "flex-row justify-around"
                              :vertical "flex-column items-stretch")}
    [:div (apply with-prop-atom* nil component @state children)]
    [:.order-1 (when (= orientation :vertical)
                 {:class "mt3"}) (state-editor {:label label} @state)]]])

