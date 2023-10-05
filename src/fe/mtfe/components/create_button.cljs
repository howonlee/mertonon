(ns mtfe.components.create-button
  "Create buttons. Because of the nature of mt sessions we can use them as login buttons too"
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]))

(def default-labels
  {
   ;; State labels
   :blank    "Enter data."
   :filled   "Press Create button to create."
   :creating "Creating..."
   :success  "Successfully created"
   :failure  "Failed to create."
   :finished "Finished!"

   ;; Button labels
   :submit   "Submit"
   :finish   "Finish"
   })

;; ---
;; Events
;; ---

(reg-event-db :mutate-create
              ;; path new-val db
              ;; if not filled then fill
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
        curr-labels              (if (seq labels) labels default-labels)]
    [sc/border-region
     [:div.pa2
      (labels curr-state)]
     [:div
      (if (and (empty? (->> curr-sidebar-state :validation-errors))
               (= curr-create-state :filled))
        [util/evl :submit-create
         [sc/button (labels :submit)]
         curr-create-params
         ;; other shit
         ]
        [sc/disabled-button (labels :submit)])
      [:span.pa2
       (if (= curr-create-state :creating)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     [:div
      (if (contains? #{:success :failure} curr-create-state)
        [util/evl :finish-create
         [sc/button (labels :finish)]]
        [sc/disabled-button (labels :finish)])]
     ;; (when the curr sidebar state has failures
     ;;   print em here. with pre and whitespace stuff and whatever
     ]))
