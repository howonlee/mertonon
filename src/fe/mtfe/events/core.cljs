(ns mtfe.events.core
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path]]
            [day8.re-frame.http-fx]))

(reg-event-fx
 :initialize-db
 []
 (fn [_ _]
   {:db {:curr-page    :home
         :curr-sidebar :home}}))

(reg-event-db
 :nav-page
 (fn [db [_ m]]
   (assoc db :curr-page (get-in m [:data :name]))))

(reg-event-db
 :nav-sidebar
 (fn [db [_ m]]
   (assoc db :curr-sidebar (get-in m [:data :name]))))