(ns mtfe.components.update-button
  "Update buttons"
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]))

(def default-labels
  {
   ;; State labels
   :initial  "Make your changes."
   :filled   "Press Update button to change things."
   :updating "Updating..."
   :success  "Successfully updated!"
   :failure  "Failed to update See error."
   :finished "Finished!"

   ;; Button labels
   :submit   "Update"
   :finish   "Finish"
   })

;; ---
;; Events
;; ---

(reg-event-db
  :reset-update-state
  (fn [db [evt {:keys [state-path init-state-path validations]}]]
    (let [path   (util/sidebar-path state-path)]
      ;; Initial update params is set by the selector event, not by this one
      (assoc-in db path
                {:update-state      :initial
                 :error             nil
                 :validation-errors {}
                 :validations       (or validations [])}))))

(reg-event-fx
  :mutate-update-state
  (fn [{:keys [db]} [evt state-path param-path evt-content]]
    (let [total-path (into (util/sidebar-path state-path) param-path)
          key-path   (into (util/sidebar-path state-path) [:update-state])]
      {:dispatch [:validate-update-state state-path]
       :db       (-> db
                     (assoc-in total-path evt-content)
                     (assoc-in key-path :filled))})))

(reg-event-fx
  :select-for-update
  nil)

(reg-event-db
  :validate-update-state
  (fn [db [evt state-path]]
    (let [path         (util/sidebar-path state-path)
          update-state (get-in db path)
          validations  (update-state :validations)]
      (update-in db path #(validations/do-validations! % validations)))))

(reg-event-fx
  :submit-update
  (fn [{:keys [db]}
       [_ {:keys [update-params resource endpoint state-path]}]]
    {:http-xhrio {:method          :put
                  :uri             endpoint
                  :params          update-params
                  :format          (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:succeed-update state-path]
                  :on-failure      [:fail-update state-path]}
     :db          (assoc-in db
                            (into (util/sidebar-path state-path) [:update-state])
                            :updating)}))

(reg-event-db
  :succeed-update
  (fn [db [_ state-path]]
    (assoc-in db (into (util/sidebar-path state-path) [:update-state]) :success)))

(reg-event-db
  :fail-update
  (fn [db [_ state-path]]
    (assoc-in db (into (util/sidebar-path state-path) [:update-state]) :failure)))

;; ---
;; Component
;; ---

