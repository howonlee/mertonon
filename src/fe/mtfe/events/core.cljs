(ns mtfe.events.core
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path]]
            [day8.re-frame.http-fx]))

(reg-event-fx
 :initialize-db
 []
 (fn [_ _]
   {:db {:curr-page-match {}}}))

(reg-event-db
 :nav-page
 (fn [db [_ m]]
   (assoc db :curr-page-match m)))

(reg-event-db
 :nav-sidebar
 (fn [db [_ m]]
   (assoc db :curr-sidebar-match m)))

;; (reg-event-db :grid-selection (fn [db [_ some-crap]]) nil)

;; (reg-event-db :intro-check (fn [db [_ some-crap]] nil)
