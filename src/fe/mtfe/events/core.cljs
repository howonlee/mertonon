(ns mtfe.events.core
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path]]
            [day8.re-frame.http-fx]))

(reg-event-fx
 :initialize-db
 []
 (fn [_ _]
   {:db {:curr-page    :home
         :curr-sidebar :home}}))

(reg-event-fx
 :nav-page
 []
 (fn [{:keys [db]} [_ {:keys [page] :as m}]]
   (println m)
   {:db (assoc db :curr-page page)}))

(reg-event-fx
 :nav-page
 []
 (fn [{:keys [db]} [_ {:keys [sidebar]}]]
   {:db (assoc db :curr-sidebar sidebar)}))
