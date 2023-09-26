(ns mtfe.subs.core
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :curr-page
  (fn [db _]
    (:curr-page db)))

(reg-sub
  :curr-sidebar
  (fn [db _]
    (:curr-sidebar db)))
