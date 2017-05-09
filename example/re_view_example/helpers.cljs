(ns re-view-example.helpers
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [re-view-material.util :as util]
            [clojure.string :as string])
  (:require-macros [re-view-example.helpers]))

(defn key-field
  "View key in editor"
  [k]
  (str (some-> (namespace k) (str "/")) (name k)))

(defn value-type
  "Name of type"
  [v]
  (cond (fn? v) "Function"
        (string? v) "String"
        (number? v) "Number"
        (boolean? v) "Boolean"
        (vector? v) (cond (= :svg (first v)) "SVG"
                          (keyword? (first v)) "Hiccup"
                          :else "Vector")
        (v/is-react-element? v) "Element"
        :else (str (type v))))

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
        id (-> (string/join "__" path)
               (string/replace ":" ""))
        kind (value-type v)]
    [:.dib {:key id}
     (case kind
       "String" (ui/Input {:value     v
                           :class     "bn shadow-5 focus-shadow-5-blue pa1 f7 bg-white"
                           :id        id
                           :on-change #(set-val! (.. % -target -value))})
       "Boolean" [:.pv2 (ui/Switch {:checked   v
                                    :id        id

                                    :on-change #(set-val! (boolean (.. % -target -checked)))})]
       "Vector" [:.ph2.relative
                 [:.absolute.top-0.left-0.di.b "["]
                 (interpose [:br] (map (fn [i] (value-field prop-atom (conj path i))) (range (count v))))
                 [:.absolute.bottom-0.right-0.di.b "]"]]
       "Element" (.. v -type -displayName)
       "Hiccup" (str "[" (first v) (when (map? (second v))
                                     (str " { " (string/join ", " (map str (keys (second v)))) " } ")) " ... ]")
       [:.i kind])]))

(defview props-editor
  "Editor for atom containing Clojure map"
  {:key          :label
   :did-mount    (fn [this a] (add-watch a :prop-editor #(v/force-update this)))
   :will-unmount (fn [_ a] (remove-watch a :prop-editor))}
  [{:keys [label]} prop-atom]

  (let [set-val! (fn [k v] (swap! prop-atom assoc-in [0 k] v))
        section #(do [:tr [:td.b.black.pv2.f6 {:col-span 2} %]])]
    [:div
     (when label [:.f6.b.ph2.pv1 label])
     (if prop-atom
       [:table.f7
        [:tbody
         (when-let [props (some-> prop-atom deref first seq)]
           (list (section "Props")
                 (for [[k v] props
                       :let [id (name k)]
                       :when (not= k :key)]
                   [:tr
                    [:td.b.o-60 (key-field k)]
                    [:td.pl3 (value-field prop-atom [0 k])]])))
         (when-let [children (some->> prop-atom deref (drop 1) (seq))]
             (list (section "Children")
                   (for [i (range (count children))]
                     [:tr
                      [:td.b.o-60 (value-type (nth children i))]
                      [:td.pl3 (value-field prop-atom [(inc i)])]])))]]
       [:.gray.i.mv2.tc.f7 "No Props"])]))

(defview with-prop-atom*
  "Calls component with value of atom & re-renders when atom changes."
  {:key          (fn [_ _ prop-atom]
                   (let [props (some-> prop-atom deref first)
                         {:keys [key id name]} (when (map? props) props)]
                     (or key id name)))
   :did-mount    (fn [this component atom]
                   (some-> atom
                           (add-watch this (fn [_ _ old new]
                                             (when (not= old new)
                                               (v/force-update this))))))
   :will-unmount (fn [this _ atom]
                   (some-> atom
                           (remove-watch this)))}
  [_ component prop-atom]
  (apply component (or (some-> prop-atom deref) [])))

(defview example-inspector
  {:key           :label
   :initial-state (fn [{:keys [prop-atom]}] prop-atom)}
  [{:keys [component orientation label view/state]
    :or   {orientation :horizontal}}]
  [:div
   (cond->> label
            (string? label) (heading 2))
   [:.mv3.flex.items-center
    {:class (case orientation :horizontal "flex-row justify-around"
                              :vertical "flex-column items-stretch")}
    [:div (with-prop-atom* nil component @state)]
    [:.order-1 (when (= orientation :vertical)
                 {:class "mt3"})
     (props-editor {:label label} @state)]]])

