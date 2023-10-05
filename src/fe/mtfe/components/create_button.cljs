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

(reg-event-fx :submit-create nil)

(reg-event-fx :succeed-at-submit nil)
(reg-event-fx :fail-at-submit nil)

(reg-event-fx :finish-create nil)

;; ---
;; Component
;; ---

(defn create-button [sidebar-state-path config & [labels]]
  (let [curr-sidebar-state @(subscribe (into [:sidebar-state] sidebar-state-path))
        curr-create-state  (curr-sidebar-state :curr-create-state)
        curr-labels        (if (seq labels) labels default-labels)]
    [sc/border-region
     [:div.pa2
      (labels curr-state)]
     [:div
      (if (and (empty? (->> curr-sidebar-state :validation-errors))
               (= curr-create-state :filled))
        [util/evl :submit-create
         [sc/button (labels :submit)]
         (->> curr-sidebar-state :create-params)]
        [sc/disabled-button (labels :submit)])
      [:span.pa2
       (if (= curr-create-state :creating)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     ;; (when the curr sidebar state has failures
     ;;   print em here)
     [:div
      (if (contains? #{:success :failure} curr-create-state)
        [util/evl :finish-create
         [sc/button (labels :finish)]]
        [sc/disabled-button (labels :finish)])]]))
