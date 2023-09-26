(ns mtfe.subs.core
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :curr-page-match
  (fn [db _]
    (:curr-page-match db)))

(reg-sub
  :curr-view
  (fn [db _]
    (get-in db [:curr-page-match :data :view])))

(reg-sub
  :curr-query-params
  (fn [db _]
    (get-in db [:curr-page-match :query-params])))

(reg-sub
  :curr-sidebar-match
  (fn [db _]
    (:curr-sidebar-match db)))
