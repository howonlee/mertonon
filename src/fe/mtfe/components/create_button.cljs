(ns mtfe.components.create-button
  "Create buttons. Because of the nature of mt sessions we can use them as login buttons too"
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]))

(def default-labels
  {
   ;; State labels
   :blank    "Enter data."
   :filled   "Press Create button to create."
   :creating "Creating..."
   :success  "Successfully created"
   :failure  "Failed to create. See error."
   :finished "Finished!"

   ;; Button labels
   :submit   "Submit"
   :finish   "Finish"
   })

(defn create-state-path [state-path]
  (-> [:sidebar-state]
      (into state-path)
      (into [:create-state])))

;; ---
;; Events
;; ---

(reg-event-db
  :reset-create-state
  (fn [db [evt state-path init-state]]
    (let [total-path (into (create-state-path state-path) [:curr-create-params])]
      (assoc-in db total-path init-state))))

(reg-event-db
  :mutate-create-state
  (fn [db [evt state-path param-path evt-content]]
    (let [total-path (into (create-state-path state-path) param-path)]
      (assoc-in db total-path evt-content))))

(reg-event-db :validate-create
              nil)

(reg-event-fx :submit-create 
              ;; path of create, whack in the http. on success succeed-create on fail do fail
              nil)


(reg-event-db :succeed-create
              ;; change state
              nil)
(reg-event-db :fail-create
              ;; change state
              nil)

(reg-event-fx :finish-create
              ;; pop a nav to the place we're going after
              nil)

;; ---
;; Component
;; ---

(defn create-button [config & [labels]]
  (let [{state-path :state-path
         endpoint   :endpoint
         ctr        :ctr
         param-list :param-list} config
        curr-sidebar-state       @(subscribe (into [:sidebar-state] state-path))
        curr-create-state        (curr-sidebar-state :create-state)
        curr-create-params       (curr-sidebar-state :create-params)
        curr-labels              (if (seq labels) labels default-labels)
        curr-error               (curr-sidebar-state :create-error)]
    [sc/border-region
     [:div.pa2
      (curr-labels curr-create-state)]
     [:div
      (if (and (empty? (->> curr-sidebar-state :validation-errors))
               (= curr-create-state :filled))
        [util/evl :submit-create
         [sc/button (curr-labels :submit)]
         curr-create-params
         ;; other shit
         ;; other shit
         ;; other shit
         ;; other shit
         ]
        [sc/disabled-button (curr-labels :submit)])
      [:span.pa2
       (if (= curr-create-state :creating)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     [:div
      (if (contains? #{:success :failure} curr-create-state)
        [util/evl :finish-create
         [sc/button (curr-labels :finish)]]
        [sc/disabled-button (curr-labels :finish)])]
     [:div
      (if (= :failure curr-create-state)
        [:pre (with-out-str (cljs.pprint/pprint curr-error))])]]))

;; ---
;; Before-fx
;; ---

(defn before-fx [config _]
  (let [{init-params :init-params
         state-path  :state-path} config]
    [[:dispatch [:reset-create-state state-path init-params]]]))
