(ns re-view.material.components.list
  (:refer-clojure :exclude [List])
  (:require [re-view.core :as v]
            [re-view.material.mdc :as mdc]
            [re-view.material.components.ripple :refer [Ripple]]))

(v/defview List
  "Presents multiple line items vertically as a single continuous element. [More](https://material.io/guidelines/components/lists.html)"
  {:spec/props    {:props/keys  [::mdc/dense]
                   :avatar-list {:spec :Boolean
                                 :doc  "Adds modifier class to style start-detail elements as large, circular 'avatars'"}}
   :spec/children [:& :Element]}
  [{:keys [dense avatar-list] :as this} & items]
  [:div (-> (v/pass-props this)
            (update :classes into ["mdc-list"
                                   (when dense "mdc-list--dense")
                                   (when avatar-list "mdc-list--avatar-list")]))
   items])

(v/defview ListItem
  "Collections of list items present related content in a consistent format. [More](https://material.io/guidelines/components/lists.html#lists-behavior)"
  {:key        (fn [{:keys [href text-primary]}] (or href text-primary))
   :spec/props {:props/keys     [::mdc/ripple]
                :text-primary   :Element
                :text-secondary :Element
                :detail-start   :Element
                :detail-end     :Element
                :dense          :Boolean}}
  [{:keys [text-primary
           text-secondary
           detail-start
           detail-end
           href
           ripple
           dense
           view/props] :as this}]
  (when-let [dep (some #{:title :body :avatar} (set (keys props)))]
    (throw (js/Error. "Deprecated " dep "... title, body - primary/text-secondary, avatar - detail-start or detail-end")))
  (let [el (if href :a :div)]
    (cond-> [el (-> (v/pass-props this)
                    (update :classes into ["mdc-list-item"
                                           (when ripple "mdc-ripple-target")])
                    (cond-> dense (update :style merge {:font-size   "1.1rem"
                                                        :height      40
                                                        :line-height "40px"
                                                        :padding     "0 8px"})))
             (some->> detail-start (conj [:.mdc-list-item__start-detail]))
             [:.mdc-list-item__text
              (when dense {:style {:font-size "80%"}})
              text-primary
              [:.mdc-list-item__text__secondary text-secondary]]
             (some->> detail-end (conj [:.mdc-list-item__end-detail]))]
            ripple (Ripple))))

(defn ListDivider []
  [:.mdc-list-divider {:role "presentation"}])

(defn ListGroup [& content]
  (into [:.mdc-list-group] content))

(defn ListGroupSubheader [content]
  [:.mdc-list-group__subheader content])