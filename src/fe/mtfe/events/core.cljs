(ns mtfe.events.core
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path]]
            [day8.re-frame.http-fx]))

(reg-event-fx
 :initialise-db
 []
 (fn [_ _]
   {:db {:curr-page :home}}))
