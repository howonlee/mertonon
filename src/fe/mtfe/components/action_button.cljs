(ns mtfe.components.action-button
  "Action buttons for arbitrary action kickoff."
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]))

(def default-labels
  {;; State labels
   :initial  "Press Kickoff button to kick off."
   :filled   "Press Kickoff button to kick off."
   :acting   "Kicking off..."
   :success  "Successfully kicked off!"
   :failure  "Failed to kick off."
   :finished "Finished."

   ;; Button labels
   :submit   "Kickoff"
   :finish   "Finish"})

(defn sidebar-path [state-path]
  (into [:sidebar-state] state-path))

(reg-event-db
  :reset-action-state
  (fn [db [evt {:keys [state-path init-state-fn validations]}]]
    (let [path (sidebar-path state-path)]
      (assoc-in db path
                {:action-params     (init-state-fn)
                 :action-state      :initial
                 :error             nil
                 :validation-errors {}
                 :validations       (or validations [])}))))

(reg-event-fx
  :mutate-action-state
  (fn [{:keys [db]} [evt state-path param-path evt-content]]
    (let [total-path (into (sidebar-path state-path) param-path)
          key-path   (into (sidebar-path state-path) [:action-state])]
      {:dispatch [:validate-action-state state-path]
       :db       (-> db
                     (assoc-in total-path evt-content)
                     (assoc-in key-path :filled))})))

(reg-event-db
  :validate-action-state
  (fn [db [evt state-path]]
    (let [path         (sidebar-path state-path)
          action-state (get-in db path)
          validations  (action-state :validations)]
      (update-in db path #(validations/do-validations! % validations)))))

(reg-event-fx
  :submit-action
  (fn [{:keys [db]}
       [_ {:keys [action-params resource endpoint state-path]}]]
    {:http-xhrio {:method          :post
                  :uri             endpoint
                  :params          action-params
                  :format          (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:succeed-action state-path]
                  :on-failure      [:fail-action state-path]}
     :db          (assoc-in db
                            (into (sidebar-path state-path) [:action-state])
                            :acting)}))

(reg-event-db
  :succeed-action
  (fn [db [_ state-path]]
    (assoc-in db (into (sidebar-path state-path) [:action-state]) :success)))

(reg-event-db
  :fail-action
  (fn [db [_ state-path]]
    (assoc-in db (into (sidebar-path state-path) [:action-state]) :failure)))

(defn action-button [config & [labels]]
  (let [{state-path :state-path
         endpoint   :endpoint
         nav-to     :nav-to}    config
        curr-sidebar-state      @(subscribe (sidebar-path state-path))
        curr-action-state       (get curr-sidebar-state :action-state :invalid)
        curr-action-params      (get curr-sidebar-state :action-params {})
        curr-labels             (if (seq labels) labels default-labels)
        curr-error              (get curr-sidebar-state :action-error nil)]
    [sc/border-region
     [:div.pa2
      (curr-labels curr-action-state)]
     [:div
      (if (and (empty? (->> curr-sidebar-state :validation-errors))
               (or
                 (= curr-action-state :initial)
                 (= curr-action-state :filled)))
        [util/evl :submit-action
         [sc/button (curr-labels :submit)]
         (assoc config :action-params curr-action-params)]
        [sc/disabled-button (curr-labels :submit)])
      [:span.pa2
       (if (= curr-action-state :acting)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     [:div
      (if (contains? #{:success :failure} curr-action-state)
        [util/evl :finish-and-nav
         [sc/button (curr-labels :finish)]
         nav-to]
        [sc/disabled-button (curr-labels :finish)])]
     [:div
      (if (= :failure curr-action-state)
        [:pre (with-out-str (cljs.pprint/pprint curr-error))])]]))
