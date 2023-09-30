(ns mtfe.events.core
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.api :as api]
            [mtfe.util :as util]
            [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path]]))

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

;; Given a page _route match_, nav to it and run before-fx fx's
(reg-event-fx
 :nav-page-match
 (fn [{:keys [db]} [_ m]]
   (let [db-res    {:db (assoc db :curr-page-match m)}
         total-res (if (-> m :data :before-fx)
                     (assoc db-res :fx ((-> m :data :before-fx) m))
                     db-res)]
     total-res)))

;; Given a page _path_, nav to it
(reg-event-fx
  :nav-page
  (fn [_ [_ path-target]]
    {:main-path path-target}))

;; Given a sidebar _route match_, nav to it
(reg-event-fx
 :nav-sidebar-match
 (fn [{:keys [db]} [_ m]]
   {:db (assoc db :curr-sidebar-match m)}))

;; Given a non-page route path, like sidebars, nav to it
(reg-event-fx
  :nav-route
  (fn [_ [_ event-id path]]
    {:non-main-path [event-id path]}))

;; Nav to the canonical default sidebar view, which corresponds to the 'default modal' if we think of sidebar as permanent modal"
(reg-event-fx
  :nav-to-sidebar-for-current-main-view
  (fn [_ _]
    (let [pathname (subs (.-hash (.-location js/window)) 1)]
      (if (clojure.string/blank? pathname)
        {:non-main-path ["sidebar-change" "/"]}
        {:non-main-path ["sidebar-change" pathname]}))))

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
;; Error-Handling
;; ---

(reg-event-fx
  :error
  (fn [{:keys [db]} [evt error-res]]
    {:fx [[:dispatch [:nav-page "#/error"]]]
     :db (assoc db :curr-error error-res)}))

(reg-event-fx
  :api-request-error
  (fn [{:keys [db]} [evt erroring-evt erroring-resource error-res]]
    (let [status-res (:status error-res)]
      (case status-res
        401   {:fx [[:dispatch [:intro-check]]]}
        403   {:fx [[:dispatch [:error error-res]]]}
        500   {:fx [[:dispatch [:error error-res]]]}
        {:fx [[:dispatch [:error error-res]]]}))))

(reg-event-fx
  :intro-check
  (fn [db _]
    {:http-xhrio {:method :get
                   :uri (api/intro)
                   :params {}
                   :response-format (json-response-format {:keywords? true})
                   :on-success [:nav-page "#/intro"]
                   :on-failure [:nav-page "#/login"]}}))

;; ---
;; Misc
;; ---
