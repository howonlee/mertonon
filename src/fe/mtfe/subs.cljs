(ns mtfe.subs
  "DB subscriptions for re-frame in Mertonon"
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
  :selection
  (fn [db [evt & path]]
    (get-in db (into [:selections] path))))

(reg-sub
  :curr-error
  (fn [db _]
    (:curr-error db)))
