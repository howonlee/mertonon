(ns mtfe.components.create-button
  "Create buttons. Because of the nature of mt sessions we can use them as login buttons too"
  (:require [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]))

(def default-labels
  {;; State labels
   :initial  "Press Delete button to confirm deletion."
   :deleting "Deleting.."
   :success  "Successfully deleted!"
   :failure  "Failed to delete."
   :finished "Finished."

   ;; Button labels
   :delete   "Delete"
   :finish   "Finish"})

;; ---
;; Events
;; ---

;; TODO: actually fill these in


(reg-event-fx :submit-delete
              ;; path of create, whack in the http. on success succeed-create on fail do fail
              nil)

(reg-event-db :reset-delete
              ;; reset path to some crap
              nil)
(reg-event-db :succeed-delete
              ;; change state
              nil)
(reg-event-db :fail-delete
              ;; change state
              nil)

(reg-event-fx :finish-delete
              ;; pop a nav to the place we're going after
              nil)

;; ---
;; Component
;; ---

(defn delete-button [sidebar-state-path member config & [labels]]
  (let [curr-sidebar-state   @(subscribe (into [:sidebar-state] sidebar-state-path))
        curr-delete-state    (curr-sidebar-state :delete-state)
        {endpoint :endpoint} config
        curr-labels          (if (seq labels) labels default-labels)]
    [sc/border-region
     [:div.pa2
      (curr-labels curr-delete-state)]
     [:div
      ;; No validations
      (if (= curr-delete-state :initial)
        [util/evl :submit-delete
         [sc/button (curr-labels :delete)]
         member]
        [sc/disabled-button (curr-labels :delete)])
      [:span.pa2
       (if (= curr-delete-state :deleting)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     [:div
      (if (contains? #{:success :failure} curr-delete-state)
        [util/evl :finish-delete
         [sc/button (curr-labels :finish)]]
        [sc/disabled-button (curr-labels :finish)])]
     [:div
      (if (= :failure curr-create-state)
        [:pre (with-out-str (cljs.pprint/pprint curr-error))])]]))
