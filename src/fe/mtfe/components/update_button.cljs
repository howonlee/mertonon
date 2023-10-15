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

(defn update-button [config & [labels]]
  (let [{state-path :state-path
         endpoint   :endpoint
         nav-to     :nav-to}    config
        curr-sidebar-state      @(subscribe (util/sidebar-path state-path))
        curr-update-state       (get curr-sidebar-state :update-state :invalid)
        curr-update-params      (get curr-sidebar-state :update-params {})
        curr-labels             (if (seq labels) labels default-labels)
        curr-error              (get curr-sidebar-state :update-error nil)]
    [sc/border-region
     [:div.pa2
      (curr-labels curr-update-state)]
     [:div
      (if (and (empty? (->> curr-sidebar-state :validation-errors))
               (or
                 (= curr-update-state :initial)
                 (= curr-update-state :filled)))
        [util/evl :submit-update
         [sc/button (curr-labels :submit)]
         (assoc config :update-params curr-update-params)]
        [sc/disabled-button (curr-labels :submit)])
      [:span.pa2
       (if (= curr-update-state :updating)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     [:div
      (if (contains? #{:success :failure} curr-update-state)
        [util/evl :finish-and-nav
         [sc/button (curr-labels :finish)]
         nav-to]
        [sc/disabled-button (curr-labels :finish)])]
     [:div
      (if (= :failure curr-update-state)
        [:pre (with-out-str (cljs.pprint/pprint curr-error))])]]))

;; ---
;; Before-fx
;; ---

(defn before-fx [config m]
  ;;;;
  ;;;;
  ;;;;
  ;;;;
  [[:dispatch nil]])
