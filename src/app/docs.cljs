(ns app.docs
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [app.markdown :refer [md]]
            [goog.net.XhrIo :as xhr]
            [goog.dom :as gdom]
            [clojure.string :as string]
            [goog.events :as events]
            [re-view-routing.core :as routing]
            [re-db.d :as d]
            [re-view-material.icons :as icons]))

(def cache {})

(defview toolbar []
  (ui/ToolbarSection
    {:class "flex items-center"}
    (ui/ToolbarTitle {:class "no-underline"
                      :href  "/"} "Docs")
    [:.flex-auto]))

(defn md-path [path]
  (str
    (when-not (string/starts-with? path "/") "/")
    path
    (when-not (string/ends-with? path ".md") ".md")))

(defn download-url [path]
  (str "http://re-view.github.io/re-view" (md-path path)))

(defn edit-url [path]
  (str "http://prose.io/#re-view/re-view/edit/master/docs" (md-path path)))

(defn local-link [el]
  (when-not (= (.-href el) (and (.hasAttribute el "href") (.getAttribute el "href")))
    (->> (routing/segments (.getAttribute el "href"))
         (string/join "/")
         (str "/docs/"))))
#_(defn markdown-link [href]
    (when (string/ends-with? href ".md")
      href))

(defview markdown-page
  {:initial-state           (fn [_ url] {:value (get cache url)})
   :update                  (fn [{:keys [view/state]} url]
                              (swap! state assoc :loading true)
                              (xhr/send (download-url url) (fn [e]
                                                             (let [value (.getResponseText (.-target e))]
                                                               (set! cache (assoc cache url value))
                                                               (reset! state {:value value})))))
   :intercept               (fn [this e]
                              (when-let [href (some-> (routing/closest (.-target e) routing/link?)
                                                      (local-link))]
                                (.stopPropagation e)
                                (.preventDefault e)
                                (routing/nav! href)))
   :life/did-mount          (fn [this url]
                              (events/listen (v/dom-node this) "click" (.-intercept this))
                              (.update this url))
   :life/will-unmount       (fn [this] (prn :un) (events/unlisten (v/dom-node this) "click" (.-intercept this)))
   :life/will-receive-props (fn [{:keys [children prev-children] :as this}]
                              (when (not= children prev-children)
                                (.update this (first children))))}
  [{:keys [view/state]} url]
  (let [{:keys [value error]} @state]
    [:.ma4.ph4.pv2.lh-copy.markdown-copy.center.mw7.elevated-card.relative
     (ui/Button {:href    (edit-url url)
                 :target "_blank"
                 :icon    icons/ModeEdit
                 :label   "Edit on GitHub"
                 :dense   true
                 :compact true
                 :class   "absolute top-0 right-0 ma2"})
     (cond error [:div "Error..." error]
           value [:div
                  [:div (md value)]]
           :else [:div "Loading..."])]))

(defview home
  []
  [:div
   (markdown-page "/index.md")])

(defview page
  [this segments]
  (markdown-page (string/join "/" segments)))