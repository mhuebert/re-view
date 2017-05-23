(ns re-view.example.helpers
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [re-view-material.util :as util]
            [re-view-material.icons :as icons]
            [re-view.view-spec :as s]
            [re-view.hoc :as hoc]
            [goog.dom :as gdom]
            [clojure.string :as string]
            [re-view-hiccup.core :as hiccup])
  (:require-macros [re-view.example.helpers]))

(defn key-field
  "View key in editor"
  [k]
  (str (some-> (namespace k) (str "/")) (name k)))

(defn value-kind
  "Name of type"
  [v]
  (cond (string? v) :String
        (number? v) :Number
        (boolean? v) :Boolean
        (nil? v) :Nil
        (fn? v) :Function
        (set? v) :Set
        (vector? v) (if
                      (keyword? (first v))
                      (if (string/starts-with? (name (first v)) "svg")
                        :SVG :Hiccup)
                      :Vector)
        (v/is-react-element? v) :Element
        (object? v) :Object
        :else (str (type v))))

(defn parse-dom-string
  [v]
  (if-not (string? v)
    v
    (if (string/starts-with? v \:)
      (keyword (subs v 1))
      (case v
        "true" true
        "false" false
        "" nil
        v))))

(defn heading [level label]
  [(case level 1 :h1.f2.normal.bw2.bt.b--light-gray.pt3
               2 :h2.f3.normal
               3 :h3.f5.normal)
   {:id (str "h" level "-" (string/replace label #"\W" "-"))} label])

(defn string-editor [props cb]
  (ui/Text (merge {:dense       true
                   :compact     true
                   :field-props {:style {:height     32
                                         :margin-top 0}}
                   :on-change   #(cb (.. % -target -value))}
                  props)))

(defn value-display-string
  "Returns a string representation for value"
  [v]
  (case (value-kind v)
    :Element (some-> v .-type .-displayName)
    :Hiccup (str "[" (first v) (when (map? (second v))
                                 (str " { " (string/join ", " (map str (keys (second v)))) " } ")) " ... ]")
    (str v)))

(defn value-edit
  "Recursively view and edit Clojure values"
  [{:keys [doc spec default spec-name] :as spec-map} prop-atom path]
  (let [v (get-in @prop-atom path default)
        {:keys [example/select]} (when (satisfies? IMeta v)
                                   (meta v))
        set-val! #(swap! prop-atom assoc-in path (parse-dom-string %))
        id (-> (string/join "__" path)
               (string/replace ":" ""))
        kind-name (or (s/spec-kind spec-map) (value-kind v))]
    [:.dib {:key id}
     (case kind-name
       :Set (ui/Select {:value     v
                        :on-change #(set-val! (.. % -target -value))}
                       (for [value spec]
                         [:option {:value value
                                   :label (value-display-string value)}]))
       :String (string-editor {:id id :value v} set-val!)
       :Boolean [:.pv2 (ui/Switch {:checked   v
                                   :id        id

                                   :on-change #(set-val! (boolean (.. % -target -checked)))})]
       :Vector [:.ph2.relative
                [:.absolute.top-0.left-0.di.b "["]
                (interpose [:br] (map (fn [i] (value-edit nil prop-atom (conj path i))) (range (count v))))
                [:.absolute.bottom-0.right-0.di.b "]"]]
       :Element (if (or (nil? v) (string? v))
                  (string-editor {:id id :value v} set-val!)
                  (some-> v .-type .-displayName))
       :Hiccup (str "[" (first v) (when (map? (second v))
                                    (str " { " (string/join ", " (map str (keys (second v)))) " } ")) " ... ]")
       [:.i (str kind-name)])]))

(defview props-editor
  "Editor for atom containing Clojure map"
  {:key               :label
   :life/did-mount    (fn [this a] (add-watch a :prop-editor #(v/force-update this)))
   :life/will-unmount (fn [_ a] (remove-watch a :prop-editor))}
  [{:keys [component view/state container-props]} prop-atom]
  (let [{prop-specs  :props
         child-specs :children
         defaults    :props/defaults} (v/element-get (component) :view/spec)
        {:keys [editing?]} @state
        section :.b.pa2.pt3.f6
        children (some->> prop-atom deref (drop 1) (seq))]
    (when (or (seq prop-specs) children)
      [:div
       container-props
       (when-not (empty? prop-specs)
         (let [values (some->> prop-atom deref first)]
           (list [section [:.flex.items-center "Props"
                           [:.pointer.pa2.o-60.hover-o-100 {:on-click #(swap! state update :editing? not)} icons/ModeEdit]]]
                 [:table.f7.w-100
                  [:tbody
                   (for [[k v] (->> prop-specs
                                    (seq)
                                    (sort-by first))
                         :let [{:keys [doc] :as prop-spec} (s/resolve-spec v)
                               v (get values k)]
                         :when (not= k :key)]
                     [:tr
                      (when editing?
                        [:td (value-edit prop-spec prop-atom [0 k])])
                      [:td
                       [:.b.pre-wrap.mb1 (key-field k)]
                       [:.o-60 doc]]])]])))

       (when children
         (list [section "Children"]
               [:table.f7.w-100
                [:tbody
                 (for [i (range (count children))]
                   [:tr

                    [:td
                     {:col-span (if editing? 2 1)}
                     [:.b.pre-wrap.mb1 (key-field (value-kind (nth children i)))]
                     [:.pl3
                      (value-edit nil prop-atom [(inc i)])]]])]]))])))

(defview example-inspector
  {:key                :label
   :life/initial-state (fn [{:keys [prop-atom]}] prop-atom)}
  [{:keys [component orientation label view/state]
    :or   {orientation :horizontal}}]
  [:div
   (cond->> label
            (string? label) (heading 2))
   [:.mv3.flex.items-center
    {:class (case orientation :horizontal "flex-row justify-around"
                              :vertical "flex-column items-stretch")}
    [:div (hoc/bind-atom component @state)]
    [:.order-1 (when (= orientation :vertical)
                 {:class "mt3"})
     (props-editor {:label     label
                    :component component} @state)]]])

