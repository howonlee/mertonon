(ns mtfe.components.delete-button
  "Delete buttons. Because of the nature of mt sessions we can use them as logout buttons too"
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
;; Before-fx
;; ---

(defn before-fx [api-endpoint m]
  (fn [m]
    [[:dispatch-n [[:reset-the-thing]
                   [:get-the-thing]]]]))

;; ---
;; Events
;; ---

(reg-event-db :reset-delete
              ;;;;;;
              ;;;;;;
              ;;;;;;
              ;; reset path to some crap
              nil)

(reg-event-fx :submit-delete
              ;; path of create, whack in the http. on success succeed-create on fail do fail
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

;; ---
;; Sidebar
;; ---

(defn delete-model-render [model-name member delete-sc-state]
  [:<>
   [:h1 "Delete " model-name]
   [:p "Delete " [:strong (->> member :name)]]
   [:p "UUID " [:strong (str (->> member :uuid))]]
   [:p "?"]
   [delete-button @delete-sc-state delete-sc-state member]])

(defn delete-model-sidebar [m]
  [:<>
   [:h1 "Delete stuff here"]])
