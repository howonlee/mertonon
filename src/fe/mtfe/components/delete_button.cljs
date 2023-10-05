(ns mtfe.components.delete-button
  "Delete buttons. Because of the nature of mt sessions we can use them as logout buttons too.

  They're all the same so we just stick the sidebar semantics here"
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
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

(defn delete-state-path [state-path]
  (-> [:sidebar-state]
      (into state-path)
      (into [:delete-state])))


;; ---
;; Events
;; ---

(reg-event-db
  :reset-delete-state
  (fn [db [_ state-path]]
    (assoc-in db (into [:sidebar-state] state-path)
              {:delete-state :initial
               :error        nil})))

(reg-event-fx
  :submit-delete
  (fn [{:keys [db]} [_ resource endpoint state-path member]]
    {:http-xhrio {:method          :delete
                  :uri             endpoint
                  :params          {}
                  :format          (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success      [:succeed-delete state-path]
                  :on-failure      [:fail-delete state-path]}
     :db          (assoc-in db
                            (delete-state-path state-path)
                            :deleting)}))

(reg-event-db
  :succeed-delete
  (fn [db [_ state-path]]
    (assoc-in db (delete-state-path state-path) :success)))

(reg-event-db
  :fail-delete
  (fn [db [_ state-path]]
    (assoc-in db (delete-state-path state-path) :failure)))

(reg-event-fx
  :finish-delete
  (fn [cofx [_ nav-to]]
    {:dispatch [:nav-page nav-to]}))

;; ---
;; Component
;; ---

(defn delete-button [sidebar-state-path member config & [labels]]
  (let [curr-sidebar-state  @(subscribe (into [:sidebar-state] sidebar-state-path))
        curr-delete-state   (curr-sidebar-state :delete-state)
        curr-error          (curr-sidebar-state :error)
        {resource :resource
         endpoint :endpoint
         nav-to   :nav-to}  config
        curr-labels         (if (seq labels) labels default-labels)]
    [sc/border-region
     [:div.pa2
      (curr-labels curr-delete-state)]
     [:div
      ;; No validations
      (if (= curr-delete-state :initial)
        [util/evl :submit-delete
         [sc/button (curr-labels :delete)]
         resource
         endpoint
         sidebar-state-path
         member]
        [sc/disabled-button (curr-labels :delete)])
      [:span.pa2
       (if (= curr-delete-state :deleting)
         [sc/spinny-icon]
         [sc/blank-icon])]]
     [:div
      (if (contains? #{:success :failure} curr-delete-state)
        [util/evl :finish-delete
         [sc/button (curr-labels :finish)]
         nav-to]
        [sc/disabled-button (curr-labels :finish)])]
     [:div
      (if (= :failure curr-delete-state)
        [:pre (with-out-str (cljs.pprint/pprint curr-error))])]]))

;; ---
;; Before-fx
;; ---

(defn before-fx [config _]
  (let [{resource   :resource
         endpoint   :endpoint
         state-path :state-path} config]
    [[:dispatch-n [[:selection resource endpoint {}]
                   [:reset-delete-state state-path]]]]))

;; ---
;; Sidebar
;; ---

(defn delete-model-sidebar [config m]
  (let [{endpoint   :endpoint 
         resource   :resource
         state-path :state-path
         model-name :model-name
         nav-to     :nav-to}     config]
    (fn [m]
      (let [member @(subscribe [:selection resource])]
        [:<>
         [:h1 "Delete " model-name]
         [:p "Delete " [:strong (or (->> member :name)
                                    (->> member :username))]]
         [:p "UUID " [:strong (str (->> member :uuid))]]
         [:p "?"]
         [delete-button state-path member config]]))))
