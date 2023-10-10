(ns mtfe.subs
  "DB subscriptions for re-frame in Mertonon"
  (:require [re-frame.core :refer [reg-sub]]))

;; ---
;; Matches
;; ---

(reg-sub
  :curr-page-match
  (fn [db _]
    (:curr-page-match db)))

(reg-sub
  :curr-sidebar-match
  (fn [db _]
    (:curr-sidebar-match db)))

;; ---
;; View and sidebar state
;; ---

(reg-sub
  :selection
  (fn [db [evt & path]]
    (get-in db (into [:selection] path))))

(reg-sub
  :sidebar-state
  (fn [db [evt & path]]
    (get-in db (into [:sidebar-state] path))))

(reg-sub
  :is-demo?
  (fn [db _]
    (:is-demo? db)))

(reg-sub
  :weightset-mode
  (fn [db _]
    (get-in db [:sidebar-state :weightset :mode])))

;; ---
;; Validation and other errors
;; ---

(reg-sub
  :validation-procced?
  (fn [db [_ state-path validation-key]]
    (let [total-path (-> [:sidebar-state]
                         (into state-path)
                         (into [:validation-errors]))]
      (-> db (get-in total-path) (contains? validation-key)))))

(reg-sub
  :validation-contents
  (fn [db [_ state-path validation-key]]
    (let [total-path (-> [:sidebar-state]
                         (into state-path)
                         (into [:validation-errors validation-key]))]
      (get-in db total-path))))

(reg-sub
  :curr-error
  (fn [db _]
    (:curr-error db)))
