(ns mtfe.events.core
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path]]
            ))

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
 :nav-page-match
 (fn [{:keys [db]} [_ m]]
   (let [db-res    {:db (assoc db :curr-page-match m)}
         total-res (if (-> m :data :before-events)
                     (assoc db-res :dispatch-n ((-> m :data :before-events) m))
                     db-res)]
     total-res)))

(reg-event-fx
 :nav-sidebar-match
 (fn [{:keys [db]} [_ m]]
   {:db (assoc db :curr-sidebar-match m)}))

;; ---
;; Selection
;; ---

(reg-event-fx
 :selection
 (fn [{:keys [db]} [evt resource endpoint params]]
   {:http-xhrio {:method          :get
                 :uri             endpoint
                 :params          params
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:selection-success resource]
                 :on-failure      [:api-request-error evt resource]}
    :db          (-> db
                     (assoc-in [:loading resource] true))}))

(reg-event-fx
  :selection-success
  (fn [{:keys [db]} [evt resource res]]
    {:db (-> db
             (assoc-in [:selections resource] res)
             (assoc-in [:loading resource] false))}))


;; ---
;; Create
;; ---

;; ---
;; Delete
;; ---

;; ---
;; Misc
;; ---

(reg-event-fx
  :api-request-error
  (fn [{:keys [db]} [evt erroring-evt erroring-resource]]
    ;; if 401 then we proc the login thing
    {}))

(reg-event-db :intro-check (fn [db [_ m]]
                             (println "intro check proccing")
                             (assoc db :intro-check nil)))
