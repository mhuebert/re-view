(ns re-view-material.persisted
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [re-view-material.icons :as icons]))

(def FireMixin
  {:initial-state  (fn [_]
                     {:write-in-progress false})
   :inputChange    (fn [{:keys [view/state]} ^js/React.SyntheticEvent e]
                     (swap! state assoc :localValue (.. e -target -value)))
   :getInputValue  (fn [{:keys [readValue defaultValue view/state]}]
                     (or (:localValue @state)
                         (readValue)
                         defaultValue))
   :saveLocalState (fn [^js/React.Component {:keys [view/state writeValue]}]
                     (when (contains? @state :localValue)
                       (swap! state assoc :write-in-progress true)
                       (-> (writeValue (:localValue @state))
                           (.then #(swap! state assoc :write-in-progress false))
                           (.catch #(swap! state assoc
                                           :write-in-progress false
                                           :error (str %))))))})

(defview Text
         (assoc FireMixin :key :label)
         [^js/React.Component this]
         (let [{:keys [view/props className view/state writeValue readValue defaultValue]} this
               {:keys [localValue write-in-progress error focused?]} @state
               persistedValue (readValue)
               diff? (and localValue (not= persistedValue localValue))]
           (ui/Text (merge (dissoc props
                                   :readValue
                                   :writeValue
                                   :defaultValue)
                           {:value      (.getInputValue this)
                            :onSave     #(when (and (not error)
                                                    diff?)
                                           (.saveLocalState this))
                            :inProgress write-in-progress
                            :onFocus    #(swap! state assoc :focused? true)
                            :onBlur     #(swap! state assoc :focused? false)
                            :info       [(cond error [:.dark-red.b.pa1 error]
                                               (or diff? focused?) [:.flex
                                                                    [:.flex-auto]
                                                                    [:div {:onClick #(swap! state assoc :localValue persistedValue)}
                                                                     (update icons/Cancel 1 merge
                                                                             {:style     {:fill "#ddd"}
                                                                              :className "pointer"})]
                                                                    [:.b.pointer.lh-copy.pv1.ph2.br2.ml2
                                                                     (if diff?
                                                                       {:className "pointer hover-dark-blue blue bg-lightest-blue"
                                                                        :onClick   (.-saveLocalState this)}
                                                                       {:className "moon-gray bg-near-white"}) "Save"]]
                                               :else nil)]
                            :onChange   #(.inputChange this %)}))))