(ns app.docs
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [app.markdown :refer [md]]
            [goog.net.XhrIo :as xhr]
            [goog.dom :as gdom]
            [clojure.string :as string]
            [goog.events :as events]
            [re-view-routing.core :as routing]
            [re-db.d :as d]))

(def cache {})

(defview toolbar []
  (ui/ToolbarSection
    {:class "flex items-center"}
    (ui/ToolbarTitle {:class "no-underline"
                      :href "/"} "Docs")
    [:.flex-auto]))

(defn prefix-url [url]
  (str "http://re-view.github.io/re-view"
       (str
         (when-not (string/starts-with? url "/") "/")
         url
         (when-not (string/ends-with? url ".md") ".md"))))

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
                              (xhr/send (prefix-url url) (fn [e]
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
    [:.ma4.ph4.pv2.lh-copy.markdown-copy.center.mw7.elevated-card
     (cond error [:div "Error..." error]
           value [:div
                  [:div (md value)]]
           :else [:div "Loading..."])]))

(defview home
  []
  [:div (markdown-page "/index.md")])

(defview page
  [this segments]
  (markdown-page (string/join "/" segments)))