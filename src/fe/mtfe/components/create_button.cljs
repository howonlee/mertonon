(ns mtfe.components.create-button
  "Create buttons. Because of the nature of mt sessions we can use them as login buttons too"
  (:require [mtfe.stylecomps :as sc]
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

;; ---
;; Events
;; ---

;; TODO: actually fill these in

(reg-event-db :mutate-create
              ;; path new-val db
              ;; if not filled then fill
              nil)
(reg-event-db :validate-create
              nil)

(reg-event-fx :submit-create 
              ;; path of create, whack in the http. on success succeed-create on fail do fail
              nil)

(reg-event-db :reset-create
              ;; reset path to some crap
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

(defn create-button [sidebar-state-path config & [labels]]
  (let [curr-sidebar-state       @(subscribe (into [:sidebar-state] sidebar-state-path))
        curr-create-state        (curr-sidebar-state :create-state)
        curr-create-params       (curr-sidebar-state :create-params)
        {endpoint   :endpoint
         ctr        :ctr
         param-list :param-list} config
        curr-labels              (if (seq labels) labels default-labels)
        curr-error               (curr-sidebar-state :create-error)]
    [sc/border-region
     [:div.pa2
      (curr-labels curr-state)]
     [:div
      (if (and (empty? (->> curr-sidebar-state :validation-errors))
               (= curr-create-state :filled))
        [util/evl :submit-create
         [sc/button (curr-labels :submit)]
         curr-create-params
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
