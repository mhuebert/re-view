(ns re-view.hiccup-test
  (:require [cljs.test :refer [deftest is are testing]]
            ["react" :as react]
            ["react-dom" :as react-dom]
            [re-view.hiccup.core :refer [element]]
            [re-view.hiccup.hiccup :as hiccup]))

(enable-console-print!)

(defn element-args [form]
  (let [[_ k id classes] (hiccup/parse-key-memoized (form 0))
        [props children] (hiccup/parse-args form)]
    (-> (into [k (hiccup/props->js k id classes props)] children)
        (update 1 js->clj :keywordize-keys true))))

(deftest hiccup


  (testing "Parse props"

    (is (= (element-args [:h1#page-header])
           ["h1" {:id "page-header"}])
        "Parse ID from element tag")

    #_(is (= ["div" {:className "red"}]
           (element-args [:div.red])
           (element-args [:div {:class "red"}])
           (element-args [:div {:classes ["red"]}]))
        "Three ways to specify a class")

    #_(is (= ["div" {:className "red"}]
           (element-args [:div.red nil]))
        "Three ways to specify a class")

    #_(is (= (element-args [:.red {:class   "white black"
                                 :classes ["purple"]}])
           ["div" {:className "red white black purple"}])
        "Combine classes from element tag, :class, and :classes")

    #_(is (= (element-args [:.red])
           ["div" {:className "red"}])
        "If tag name is not specified, use a `div`")

    #_(is (= (element-args [:div {:data-collapse true
                                :aria-label    "hello"}])
           ["div" {:data-collapse true
                   :aria-label    "hello"}])
        "Do not camelCase data- and aria- attributes")

    #_(is (= (element-args [:div {:some-attr true
                                :someAttr  "hello"}])
           ["div" {:some-attr true
                   :someAttr  "hello"}])
        "Do not camelCase custom attributes")

    (is (= (element-args [:div {:style {:font-family "serif"
                                        :custom-attr "x"}}])
           ["div" {:style {:fontFamily "serif"
                           :customAttr "x"}}])
        "camelCase ALL style attributes")

    (is (= (element-args [:custom-element])
           ["custom-element" {}])
        "Custom element tag")

    (is (= (element-args [:custom-element/special])
           ["custom-element:special" {}])
        "Custom element tag with namespace")





    #_(is (= (element-args [:special/effect#el.pink {:data-collapse true
                                                   :aria-label    "hello"
                                                   :class         "bg-black"
                                                   :classes       ["white"]
                                                   :style         {:font-family "serif"
                                                                   :font-size   12}}])
           ["special:effect" {:data-collapse true
                              :aria-label    "hello"
                              :className     "pink bg-black white"
                              :style         {:fontFamily "serif"
                                              :fontSize   12}
                              :id            "el"}])
        "All together")))
