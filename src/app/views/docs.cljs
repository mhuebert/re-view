(ns app.views.docs
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [app.views.markdown :refer [md]]
            [goog.dom :as gdom]
            [goog.dom.classes :as classes]
            [goog.string.path :as path]
            [clojure.string :as string]
            [goog.events :as events]
            [re-view-routing.core :as routing]
            [re-db.d :as d]
            [re-view-material.icons :as icons]
            [app.util :as util]
            [app.views :as views]))

(def download-root "https://re-view.github.io/docs")
(def doc-edit-root "https://github.com/re-view/docs/edit/master")

(defn hyphens->readable [s]
  (if (= s "re-view")
    "Re-View"
    (string/replace s #"(?:^|-)([a-z])" (fn [[_ s]] (str " " (string/upper-case s))))))



#_(defn doc-breadcrumb [url]
    (let [all-segments (routing/segments url)]
      (let [num-segments (count all-segments)]
        (->> all-segments
             (map-indexed
               (fn [i label]
                 [(hyphens->readable label)
                  (as-> (take (inc i) all-segments) segments
                        (string/join "/" segments)
                        (str "/docs/" segments)
                        (if-not (= i (dec num-segments)) (str segments "/") segments))]))
             (cons ["Docs" "/docs/"])
             (breadcrumb)))))

(def index nil)

(defn path->keys [url]
  (if (= url "/")
    (list "index")
    (-> (string/replace url #"\.md$" "")
        (string/split #"/"))))

(def get-index (fn [cb]
                 (util/GET :json "https://re-view.github.io/re-view/json/index.json"
                           (fn [{:keys [value]}]
                             (set! index
                                   (->> (js->clj value :keywordize-keys true)
                                        (reduce (fn [m {:keys [title name path] :as doc}]
                                                  (assoc-in m (concat (path->keys path) (list :*file)) doc)) {})))
                             (cb)))))

(defn index-page [children]

  [:div
   [:h3.mv3 "Index"]
   (->> (vals children)
        (keep :*file)
        (sort-by :title)
        (map (fn [{:keys [title name path]}]
               [:p [:a {:href (str "/docs/" (string/replace path #"\.md$" ""))} title]])))])



(defview doc-page
  {:life/did-mount   (fn [{:keys [view/state
                                  edit-url] :as this} url]
                       (when-not index (get-index #(v/force-update this))))}
  [{:keys [edit-url] :as this} url]
  (let [path-ks (path->keys url)
        {{file-path :path} :*file
         :as               current-index} (get-in index (path->keys url))]
    (views/page {:toolbar-items [[:.flex-auto] (when file-path (views/edit-button edit-url))]}
                (cond
                  (nil? index) [:div "Loading..."]
                  file-path (views/markdown-page (path/join download-root file-path))
                  current-index (index-page current-index)))))

(def sidebar
  [{:text-primary "Getting Started"
    :href         "/docs/re-view/getting-started"}])