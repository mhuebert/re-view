(ns re-view.re-frame-simple
  (:refer-clojure :exclude [get get-in assoc! identity swap!] :rename {get    get*
                                                                       get-in get-in*
                                                                       swap!  swap!*})
  (:require [re-frame.core :as rf]
            [re-frame.db :refer [app-db]])
  (:require-macros [re-view.re-frame-simple]))

(rf/reg-sub :get (fn [db [_ key not-found]]
                   (get* db key not-found)))

(rf/reg-sub :get-in (fn [db [_ path not-found]]
                      (get-in* db path not-found)))

(rf/reg-sub :identity (fn [db [_]] db))

(rf/reg-event-db :update
                 (fn [db [_ key f & args]]
                   (apply update db key f args)))

(rf/reg-event-db :update-in
                 (fn [db [_ path f & args]]
                   (apply update-in db path f args)))

(rf/reg-event-db :assoc
                 (fn [db [_ & keyvals]]
                   (apply assoc db keyvals)))

(rf/reg-event-db :assoc-in
                 (fn [db [_ path value]]
                   (assoc-in db path value)))

(rf/reg-event-db :swap
                 (fn [db [_ & args]]
                   (apply swap!* db args)))

(def ^:dynamic ^boolean *in-query?* false)

(defn get
  "Read a value from db by `key`, not-found or nil if value not present."
  ([key]
   (if *in-query?*
     (get* @app-db key)
     @(rf/subscribe [:get key])))
  ([key not-found]
   (if *in-query?*
     (get* @app-db key not-found)
     @(rf/subscribe [:get key not-found]))))

(defn get-in
  "Read a value from db by `path`, not-found or nil if value not present."
  ([path]
   (if *in-query?*
     (get-in* @app-db path)
     @(rf/subscribe [:get-in path])))
  ([path not-found]
   (if *in-query?*
     (get-in* @app-db path not-found)
     @(rf/subscribe [:get-in path not-found]))))

(defn identity
  "Return current value of db"
  []
  (if *in-query?*
    @app-db
    @(rf/subscribe [:identity])))

(defn update!
  "Applies update to db with args"
  [& args]
  (rf/dispatch (into [:update] args)))

(defn update-in!
  "Applies update-in to db with args"
  [& args]
  (rf/dispatch (into [:update-in] args)))

(defn assoc!
  "Applies assoc to db with args"
  [& args]
  (rf/dispatch (into [:assoc] args)))

(defn assoc-in!
  "Applies assoc-in to db with args"
  [& args]
  (rf/dispatch (into [:assoc-in] args)))

(defn swap!
  "Applies swap! to db with args."
  [& args]
  (rf/dispatch (into [:swap] args)))

(def dispatch "Dispatch a re-frame event." rf/dispatch)
(def dispatch-sync "Synchronous version of `dispatch`" rf/dispatch-sync)