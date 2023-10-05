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

(defn sidebar-path [state-path]
  (into [:sidebar-state] state-path))

;; ---
;; Events
;; ---

(reg-event-db
  :reset-create-state
  (fn [db [evt state-path init-state-fn]]
    (let [path (sidebar-path state-path)]
      (assoc-in db path
                {:create-params    (init-state-fn)
                 :create-state     :blank
                 :error            nil
                 :validation-error {}}))))

(reg-event-db
  :mutate-create-state
  (fn [db [evt state-path param-path evt-content]]
    (let [total-path (into (sidebar-path state-path) param-path)
          key-path   (into (sidebar-path state-path) [:create-state])]
      (-> db
          (assoc-in total-path evt-content)
          (assoc-in key-path :filled)))))

;; TODO: validation again
(reg-event-db :validate-create
              nil)

(reg-event-fx
  :submit-create
  (fn [{:keys [db]}
       [_ {:keys [create-params resource endpoint state-path ctr ctr-params]}]]
    (let [param-list (vec (for [member-param ctr-params]
                            (create-params member-param)))
          new-member (apply ctr param-list)
          printo     (println ctr-params)
          printo     (println new-member)]
    {:http-xhrio {:method          :post
                  :uri             endpoint
                  :params          new-member
                  :format          (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:succeed-create state-path]
                  :on-failure      [:fail-create state-path]}
     :db          (assoc-in db
                            (into (sidebar-path state-path) [:create-state])
                            :creating)})))

(reg-event-db
  :succeed-create
  (fn [db [_ state-path]]
    (assoc-in db (into (sidebar-path state-path) [:create-state]) :success)))

(reg-event-db
  :fail-create
  (fn [db [_ state-path]]
    (assoc-in db (into (sidebar-path state-path) [:create-state]) :failure)))

(reg-event-fx
  :finish-create
  (fn [cofx [_ nav-to]]
    (println nav-to)
    {:dispatch [:nav-page nav-to]}))

;; ---
;; Component
;; ---

(defn create-button [config & [labels]]
  (let [{state-path :state-path
         endpoint   :endpoint
         ctr        :ctr
         param-list :param-list
         nav-to     :nav-to}     config
        curr-sidebar-state       @(subscribe (sidebar-path state-path))
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
         (assoc config :create-params curr-create-params)]
        [sc/disabled-button (curr-labels :submit)])
      [:span.pa2
       (if (= curr-create-state :creating)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     [:div
      (if (contains? #{:success :failure} curr-create-state)
        [util/evl :finish-create
         [sc/button (curr-labels :finish)]
         nav-to]
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
