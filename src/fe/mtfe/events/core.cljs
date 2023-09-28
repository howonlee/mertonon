(ns mtfe.events.core
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path]]
            [day8.re-frame.http-fx]))

;; ---
;; Initializations
;; ---

(reg-event-fx
 :initialize-db
 []
 (fn [_ _]
   {:db {:curr-page-match {}}}))

;; ---
;; Navigation
;; ---

(reg-event-fx
 :nav-page
 []
 (fn [db [_ m]]
   (let [db-res    {:db (assoc db :curr-page-match m)}
         total-res (if (-> m :data :before-events)
                     (assoc db-res :dispatch-n ((-> m :data :before-events) m))
                     db-res)]
     total-res)))

(reg-event-db
 :nav-sidebar
 (fn [db [_ m]]
   (assoc db :curr-sidebar-match m)))

;; ---
;; Selection
;; ---

(reg-event-db :selection (fn [db [_ m]]
                           (println "selection proccing")
                           (assoc db :selection nil)))

(reg-event-db :intro-check (fn [db [_ m]]
                             (println "intro check proccing")
                             (assoc db :intro-check nil)))

;; ---
;; Create
;; ---

;; ---
;; Delete
;; ---
