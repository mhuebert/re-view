(ns re-view-prosemirror.example-toolbar
  (:require [re-view-prosemirror.core :as pm]
            [re-view-material.core :as ui]
            [re-view-material.icons :as icons]))


(def menu-item-element :.pm-toolbar-item)

(defn menu-item
  "Renders a menu item with icon"
  [key pm-state dispatch cmd active? icon]
  (let [enabled? (cmd pm-state)]
    [:.inline-flex.items-center.ph1.h-100
     (-> (if (false? enabled?)
           {:class "pm-toolbar-icon o-30"}
           {:class         (str "pointer hover-bg-darken-more "
                                (when active? "blue"))
            :on-mouse-down (fn [^js/Event e]
                             (.preventDefault e)
                             (cmd pm-state dispatch))})
         (assoc :key (name key)))
     icon]))

(defn mark-strong [state dispatch]
  (menu-item :strong state dispatch (pm/toggle-mark :strong) (pm/has-mark? state :strong) icons/FormatBold))

(defn mark-em [state dispatch]
  (menu-item :em state dispatch (pm/toggle-mark :em) (pm/has-mark? state :em) icons/FormatItalic))

(defn mark-code [state dispatch]
  (menu-item :code state dispatch (pm/toggle-mark :code) (pm/has-mark? state :code) icons/Code))


(defn list-bullet [state dispatch]
  (let [in-bullet-list? (pm/in-list? state "bullet_list")]
    (menu-item :bullet-list state dispatch (cond->> (pm/wrap-in-list :bullet_list)
                                                    in-bullet-list? (pm/chain
                                                                      pm/lift
                                                                      pm/lift-list-item))
               in-bullet-list? icons/FormatListBulleted)))

(defn list-ordered [state dispatch]
  (let [in-ordered-list? (pm/in-list? state "ordered_list")]
    (menu-item :ordered-list state dispatch (cond->> (pm/wrap-in-list :ordered_list)
                                                     in-ordered-list? (pm/chain
                                                                        pm/lift
                                                                        pm/lift-list-item))
               in-ordered-list? icons/FormatListOrdered)))

(defn tab-outdent [state dispatch]
  (menu-item :outdent state dispatch (pm/chain
                                       pm/lift-list-item
                                       pm/lift) false icons/FormatOutdent))

(defn tab-indent [state dispatch]
  (menu-item :indent state dispatch pm/sink-list-item false icons/FormatIndent))

(defn block-code [state dispatch]
  (menu-item :code-block state dispatch (pm/chain
                                          (pm/set-block-type :code_block)
                                          (pm/set-block-type :paragraph)) (pm/has-markup? state :code_block nil) icons/Code))

(defn wrap-quote [state dispatch]
  (menu-item :blockquote state dispatch (pm/wrap-in state :blockquote) false icons/FormatQuote))

(defn block-heading
  "Dropdown menu with paragraph and header block types"
  [heading-n]
  (fn [state dispatch]
    (let [set-p (pm/set-block-type :paragraph)
          set-h1 (pm/set-block-type :heading #js {"level" 1})
          active? (or (set-p state) (set-h1 state))]
      (ui/SimpleMenuWithTrigger
        [menu-item-element (if active? {:class "pointer hover-bg-near-white "}
                                       {:class "o-30"}) icons/FormatSize]
        (ui/SimpleMenuItem {:key          "p"
                            :text-primary "Paragraph"
                            :disabled     (false? (set-p state))
                            :on-click     #(set-p state dispatch)})
        (for [i (range 1 (inc heading-n))
              :let [cmd (pm/set-block-type :heading #js {"level" i})]]
          (ui/SimpleMenuItem {:key          i
                              :disabled     (false? (cmd state))
                              :on-click     #(cmd state dispatch)
                              :text-primary (str "Heading " i)}))))))

(def all-toolbar-items
  [mark-strong
   mark-em
   list-bullet
   list-ordered
   tab-outdent
   tab-indent
   block-code
   wrap-quote
   (block-heading 6)])



#_(defn toolbar-example
    "Render a list of toolbar items"
    [state dispatch]
    [:div
     (for [menu-item [mark-strong
                      mark-em
                      list-bullet
                      wrap-quote]]
       (menu-item state dispatch))])