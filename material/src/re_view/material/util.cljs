(ns re-view.material.util
  (:require [re-view.core :refer [defview]]
            [clojure.string :as string]
            [goog.object :as gobj]
            [goog.dom.classes :as classes]
            [goog.dom :as gdom]
            [clojure.set :as set]
            [react]))

(defn ensure-str [s]
  (when-not (contains? #{nil ""} s)
    s))

(defn add-styles
  ([target styles]
   (add-styles target styles {}))
  ([target styles prev-styles]
   (when (and target (or styles prev-styles))
     (let [^js style (gobj/get target "style")]
       (doseq [attr (-> #{}
                        (into (keys styles))
                        (into (keys prev-styles)))]
         (.setProperty style attr (get styles attr)))))))

(defn concat-handlers [handlers]
  (when-let [handlers (seq (keep identity handlers))]
    (fn [^js e]
      (reduce (fn [res f] (f e)) nil handlers))))

(defn collect-handlers
  "Combines specified handlers from props"
  [props handlers]
  (reduce-kv (fn [m key handler]
               (assoc m key (concat-handlers [handler
                                              (get props key)]))) {} handlers))

(defn handle-on-save [handler]
  (when handler
    (fn [^js e]
      (when (or (and (= "s" (.toLowerCase (.-key e)))
                     (or (.-ctrlKey e) (.-metaKey e)))
                (= 13 (.-which e)))
        (.preventDefault e)
        (handler)))))

(defn collect-text [text]
  (if (string? text)
    (some-> (ensure-str text) (list))
    (seq (for [msg text
               :when (ensure-str msg)]
           msg))))

(defn find-node [^js root p]
  (if (p root) root (gdom/findNode root p)))

(defn find-tag [^js root re]
  (gdom/findNode root (fn [^js el]
                        (some->> (.-tagName el) (re-find re)))))

(defn closest [^js root p]
  (if (p root) root (gdom/getAncestor root p)))

(defn force-layout [^js el]
  (.-offsetWidth el))

(defview sync-element-css!
  "Applies & syncs :style map and :classes vector to the element returned from :get-element fn."
  {:spec/props      {:style       :Map
                     :classes     :Vector
                     :get-element :Function}
   :view/did-mount  (fn [{:keys [view/state get-element] :as ^js this}]
                      (let [^js element (get-element this)]
                        (swap! state assoc
                               :element element
                               :style-obj (.-style element)))
                      (.componentDidUpdate this))
   :view/did-update (fn [{{style   :style
                           classes :classes}      :view/props
                          {prev-style   :style
                           prev-classes :classes} :view/prev-props
                          :keys                   [view/state]}]
                      (let [{:keys [element style-obj]} @state
                            classes (set classes)
                            prev-classes (set prev-classes)
                            styles-removed (set/difference (set (keys prev-style)) (set (keys style)))
                            classes-removed (set/difference prev-classes classes)]
                        (doseq [attr styles-removed]
                          (.setProperty style-obj attr nil))
                        (doseq [[attr val] style]
                          (.setProperty style-obj attr val))
                        (doseq [class classes-removed]
                          (classes/remove element class))
                        ;; always add classes, in case some were removed?
                        (doseq [class classes]
                          (classes/add element class))))}
  [_]

  [:span.dn])