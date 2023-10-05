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

(defn create-button [sidebar-state-path button-state-path & [labels]]
  (let [curr-sidebar-state @(subscribe (into [:some crap] sidebar-state-path))
        curr-button-state  @(subscribe (into [:some crap] button-state-path))
        curr-create-state  nil
        curr-labels        (if (seq labels) labels default-labels)]
    [sc/border-region
     [:div.pa2
      (labels curr-state)]
     [:div
      (if (and (empty? (->> curr-sidebar-state :validation-errors))
               (= curr-create-state :filled))
        [util/stl create-sc-state :submit
         [sc/button (labels :submit)]
         (->> @sidebar-state :curr-create-params)]
        [sc/disabled-button (labels :submit)])
      [:span.pa2
       (if (= curr-create-state :creating)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     [:div
      (if (contains? #{:success :failure} curr-create-state)
        [util/stl create-sc-state :finish
         [sc/button (labels :finish)]]
        [sc/disabled-button (labels :finish)])]]))
