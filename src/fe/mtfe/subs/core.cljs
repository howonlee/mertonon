(ns mtfe.subs.core
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :curr-page-match
  (fn [db _]
    (:curr-page-match db)))

(reg-sub
  :curr-sidebar-match
  (fn [db _]
    (:curr-sidebar-match db)))

(reg-sub
  :grid-selection
  (fn [db _]
    (:grid-selection db)))
