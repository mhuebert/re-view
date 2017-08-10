(ns re-view-material.persisted.core
  (:require [re-view.core :as v :refer [defview]]
            [re-view-material.core :as ui]
            [re-view-material.icons :as icons]
            react))

(defn input-change [{:keys [view/state]} ^react/SyntheticEvent e]
  (swap! state assoc :local-value (.. e -target -value)))

(defn save-local-state [{:keys [view/state write-value]}]
  (when (contains? @state :local-value)
    (swap! state assoc :write-in-progress true)
    (-> (write-value (:local-value @state))
        (.then #(swap! state assoc :write-in-progress false))
        (.catch #(swap! state assoc
                        :write-in-progress false
                        :error (str %))))))

(defn get-input-value [{:keys [read-value default-value view/state]}]
  (or (:local-value @state)
      (read-value)
      default-value))

(defview Text
         {:key            :label
          :inputChange    input-change
          :getInputValue  get-input-value
          :saveLocalState save-local-state}
         [this]
         (let [{:keys [view/props class view/state write-value read-value default-value]} this
               {:keys [local-value write-in-progress error focused]} @state
               persistedValue (read-value)
               diff? (and local-value (not= persistedValue local-value))]
           (ui/Text (merge (dissoc props
                                   :read-value
                                   :write-value
                                   :default-value)
                           {:value       (.getInputValue this)
                            :on-save     #(when (and (not error)
                                                     diff?)
                                            (.saveLocalState this))
                            :in-progress write-in-progress
                            :on-focus    #(swap! state assoc :focused true)
                            :on-blur     #(swap! state assoc :focused false)
                            :info        [(cond error [:.dark-red.b.pa1 error]
                                                (or diff? focused) [:.flex
                                                                     [:.flex-auto]
                                                                     [:div {:on-click #(swap! state assoc :local-value persistedValue)}
                                                                      (update icons/Cancel 1 merge
                                                                              {:style      {:fill "#ddd"}
                                                                               :class "pointer"})]
                                                                     [:.b.pointer.lh-copy.pv1.ph2.br2.ml2
                                                                      (if diff?
                                                                        {:class "pointer hover-dark-blue blue bg-lightest-blue"
                                                                         :on-click   (.-saveLocalState this)}
                                                                        {:class "moon-gray bg-near-white"}) "Save"]]
                                                :else nil)]
                            :on-change   #(.inputChange this %)}))))