(ns re-view.example.helpers
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [re-view-material.util :as util]
            [re-view.view-spec :as s]
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
  (ui/Input (merge {:class     "bn shadow-5 focus-shadow-5-blue pa1 f7 bg-white"
                    :on-change #(cb (.. % -target -value))}
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
  [{:keys [doc spec default name]} prop-atom path]
  (let [v (get-in @prop-atom path default)
        {:keys [example/select]} (when (satisfies? IMeta v)
                                   (meta v))
        set-val! #(swap! prop-atom assoc-in path (parse-dom-string %))
        id (-> (string/join "__" path)
               (string/replace ":" ""))
        kind-name (or name (value-kind v))]
    [:.dib {:react/key id}
     (case kind-name
       :Set (ui/Select {:value     v
                        :on-change #(set-val! (.. % -target -value))}
                       (for [value spec]
                         [:option {:value value
                                   :label (value-display-string value)}]))
       :String (string-editor {:id id :value v}  set-val!)
       :Boolean [:.pv2 (ui/Switch {:checked   v
                                   :id        id

                                   :on-change #(set-val! (boolean (.. % -target -checked)))})]
       :Vector [:.ph2.relative
                [:.absolute.top-0.left-0.di.b "["]
                (interpose [:br] (map (fn [i] (value-edit nil prop-atom (conj path i))) (range (count v))))
                [:.absolute.bottom-0.right-0.di.b "]"]]
       :Element (if (or (nil? v) (string? v))
                  (string-editor {:id id :value v}  set-val!)
                  (some-> v .-type .-displayName))
       :Hiccup (str "[" (first v) (when (map? (second v))
                                    (str " { " (string/join ", " (map str (keys (second v)))) " } ")) " ... ]")
       [:.i (str kind-name)])]))

(defview props-editor
  "Editor for atom containing Clojure map"
  {:react/key         :label
   :life/did-mount    (fn [this a] (add-watch a :prop-editor #(v/force-update this)))
   :life/will-unmount (fn [_ a] (remove-watch a :prop-editor))}
  [{:keys [label component]} prop-atom]
  (let [section #(do [:tr [:td.b.black.pv2.f6 {:col-span 3} %]])
        {prop-specs  :props
         child-specs :children
         defaults    :props/defaults} (v/element-get (component) :view/spec)]
    [:div
     (when label [:.f6.b.ph2.pv1 label])
     (if prop-atom
       [:table.f7.w-100
        [:tbody
         (when-let [props (some->> prop-atom deref first)]
           (list (section "Props")
                 (for [[k v] prop-specs
                       :let [{:keys [doc] :as prop-spec} (s/resolve-spec v)
                             v (get props k)]
                       :when (not= k :react/key)]
                   [:tr
                    [:td.b.o-60 (key-field k)]
                    [:td.pl3 (value-edit prop-spec prop-atom [0 k])]
                    [:td doc]])))
         (when-let [children (some->> prop-atom deref (drop 1) (seq))]
           (list (section "Children")
                 (for [i (range (count children))]
                   [:tr
                    [:td.b.o-60 (key-field (value-kind (nth children i)))]
                    [:td.pl3 (value-edit nil prop-atom [(inc i)])]
                    [:td #_doc]])))]]
       [:.gray.i.mv2.tc.f7 "No Props"])]))

(defview with-prop-atom*
  "Calls component with value of atom & re-renders when atom changes."
  {:react/key         (fn [_ _ prop-atom]
                        (let [props (some-> prop-atom deref first)
                              {:keys [react/key id name]} (when (map? props) props)]
                          (or key id name)))
   :life/did-mount    (fn [this component atom]
                        (some-> atom
                                (add-watch this (fn [_ _ old new]
                                                  (when (not= old new)
                                                    (v/force-update this))))))
   :life/will-unmount (fn [this _ atom]
                        (some-> atom
                                (remove-watch this)))}
  [_ component prop-atom]
  (apply component (or (some-> prop-atom deref) [])))

(defview example-inspector
  {:react/key          :label
   :life/initial-state (fn [{:keys [prop-atom]}] prop-atom)}
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
     (props-editor {:label     label
                    :component component} @state)]]])

(defview Frame
  "Renders component (passed in as child) to an iFrame."
  {:view/spec         {:children [:Element]}
   :life/did-mount    (fn [this content]
                        (-> (v/dom-node this)
                            (aget "contentDocument" "body")
                            (gdom/appendChild (gdom/createDom "div")))
                        (.renderFrame this content))
   :life/will-unmount (fn [this]
                        (.unmountComponentAtNode js/ReactDOM (.getElement this)))
   :get-element       (fn [this]
                        (-> (v/dom-node this)
                            (aget "contentDocument" "body")
                            (gdom/getFirstElementChild)))
   :render-frame      (fn [this content]
                        (v/render-to-dom (hiccup/element
                                           [:div
                                            [:link {:type "text/css"
                                                    :rel  "stylesheet"
                                                    :href "/app.css"}]
                                            content]) (.getElement this)))}
  [{:keys [view/props]} component]
  [:iframe.bn.shadow-2 props])