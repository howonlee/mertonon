(ns mtfe.statecharts.components
  "Components that embody statecharts and deal a lot with state. Compare to the style components, which are mainly styling

  A lot of the more significant components live here"
  (:require [com.fulcrologic.statecharts :as fsc]
            [mtfe.selectors :as sel]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            ["react-datepicker" :default DatePicker]
            ["react-tiny-popover" :refer [Popover]]))

;; ---
;; React class adapters
;; ---

(def date-picker (r/adapt-react-class DatePicker))
(def popover (r/adapt-react-class Popover))

;; ---
;; Validation failure displays
;; ---

(defn validation-procced? [state validation-key]
  (-> @state :validation-errors (contains? validation-key)))

(defn validation-contents [state validation-key]
  (-> @state :validation-errors validation-key))

(defn curr-blurb [state validation-key blurb]
  (if (and (validation-procced? state validation-key) (fn? blurb))
    (blurb (validation-contents state validation-key))
    blurb))

(defn validated-link
  "Don't use this if you should be using a whole state machine.

  Doesn't work with function blurbs"
  [state validation-key disabled-name contents]
  (if (validation-procced? state validation-key)
    [sc/disabled-button disabled-name]
    contents))

;; Rule of thumb: if user _just_ did something, do popover.
;; If user did something like 15 minutes ago and need to continuously annoy, use toast

(defn validation-toast [state validation-key blurb]
  (if (validation-procced? state validation-key)
    [sc/validation-toast (curr-blurb state validation-key blurb)]
    [:span]))

(defn validation-popover [state validation-key blurb content]
  [popover {:isOpen (validation-procced? state validation-key)
            :positions #js ["left"]
            :content (r/as-element [sc/popper (curr-blurb state validation-key blurb)])}
   [:span content]])

;; ---
;; Inputs and Form Stuff
;; ---

(defn state-text-input [state placeholder path]
  [sc/input {:type        "text"
             :placeholder placeholder
             :on-change   (mt-statechart/mutate-from-dom-event-handler state path)}])

(defn state-password-input [state placeholder path]
  [sc/input {:type        "password"
             :placeholder placeholder
             :on-change   (mt-statechart/mutate-from-dom-event-handler state path)}])

(defn state-select-input [sc-state state choices path]
  [sc/select {:on-change (mt-statechart/mutate-from-dom-event-handler sc-state path)
              :value     (get-in @state path)}
   [sc/select-option {:value ""} "---"]
   (for [member choices] ^{:key (:uuid member)}
     [sc/select-option {:value (:uuid member)} (:name member)])])

(defn state-range-input
  ([sc-state state path]
  (state-range-input sc-state state path 0 1000 5))
  ([sc-state state path _min _max step]
   [sc/input {:type     "range"
              :on-input (mt-statechart/mutate-from-dom-event-handler sc-state path)
              :value    (get-in @state path)
              :min      _min
              :max      _max
              :step     step}]))

(defn state-datepicker [sc-state state path]
  [date-picker {:popper-placement "left"
                :selected         (get-in @state path)
                :on-select        (mt-statechart/mutate-from-plain-function-handler sc-state path)}])

;; ---
;; Creation
;; ---

(defn create-state-blurb [stateset]
  (condp #(contains? %2 %1) stateset
    :blank    "Enter data."
    :filled   "Press Create button to create."
    :creating "Creating..."
    :success  "Successfully created"
    :failure  "Failed to create."
    :finished "Finished!"))

(defn create-button [curr-create-state create-sc-state sidebar-state]
  [sc/border-region
   [:div.pa2
    (create-state-blurb (->> curr-create-state ::fsc/configuration))]
   [:div
    (if (and (empty? (->> @sidebar-state :validation-errors))
             (->> curr-create-state ::fsc/configuration :filled))
      [util/stl create-sc-state :submit
       [sc/button "Create"]
       (->> @sidebar-state :curr-create-params)]
      [sc/disabled-button "Create"])
    [:span.pa2
     (if (->> curr-create-state ::fsc/configuration :creating)
       [sc/spinny-icon]
       [sc/blank-icon])]]
   [:div
    (if (seq (clojure.set/intersection #{:success :failure} (->> curr-create-state ::fsc/configuration)))
      [util/stl create-sc-state :finish
       [sc/button "Finish"]]
      [sc/disabled-button "Finish"])]])

;; ---
;; Deletion
;; ---

(defn delete-state-blurb [stateset]
  (condp #(contains? %2 %1) stateset
    :initial  "Press Delete button to confirm deletion."
    :deleting "Deleting.."
    :success  "Successfully deleted!"
    :failure  "Failed to delete."
    :finished "Finished."))

(defn delete-button [curr-delete-state delete-sc-state member]
  [sc/border-region
   [:div.pa2
    (delete-state-blurb (->> curr-delete-state ::fsc/configuration))]
   [:div
    ;; No validations
    (if (->> curr-delete-state ::fsc/configuration :initial)
      [util/stl delete-sc-state :submit
       [sc/button "Delete"]
       member]
      [sc/disabled-button "Delete"])
    [:span.pa2
     (if (->> curr-delete-state ::fsc/configuration :deleting)
       [sc/spinny-icon]
       [sc/blank-icon])]]
   [:div
    (if (seq (clojure.set/intersection #{:success :failure} (->> curr-delete-state ::fsc/configuration)))
      [util/stl delete-sc-state :finish
       [sc/button "Finish"]]
      [sc/disabled-button "Finish"])]])

(defn delete-model-render [model-name member delete-sc-state]
  [:<>
   [:h1 "Delete " model-name]
   [:p "Delete " [:strong (->> member :name)]]
   [:p "UUID " [:strong (str (->> member :uuid))]]
   [:p "?"]
   [delete-button @delete-sc-state delete-sc-state member]])

(defn delete-model-sidebar [sidebar-state api-endpoint delete-sc-state model-name m]
  (let [curr-match-uuid (->> m :path-params :uuid)
        curr-state-uuid (->> @sidebar-state :selection :uuid)
        _               (mt-statechart/send-reset-event-if-finished! delete-sc-state)]
    (if (not= curr-match-uuid curr-state-uuid)
      (sel/set-selection! sidebar-state api-endpoint curr-match-uuid))
    [delete-model-render
     model-name
     (:selection @sidebar-state)
     delete-sc-state]))

;; ---
;; (Arbitrary) Action
;; ---

(defn action-state-blurb [stateset]
  (condp #(contains? %2 %1) stateset
    :initial  "Press Kickoff button to kick off."
    :filled   "Press Kickoff button to kick off."
    :acting   "Kicking off..."
    :success  "Successfully kicked off!"
    :failure  "Failed to kick off."
    :finished "Finished."))

(defn action-button [curr-action-state action-sc-state sidebar-state]
  [sc/border-region
   [:div.pa2
    (action-state-blurb (->> curr-action-state ::fsc/configuration))]
   [:div
    (if (and (empty? (->> @sidebar-state :validation-errors))
             (or
               (->> curr-action-state ::fsc/configuration :initial)
               (->> curr-action-state ::fsc/configuration :filled)))
      [util/stl action-sc-state :submit
       [sc/button "Kickoff"]
       (->> @sidebar-state :curr-action-params)]
      [sc/disabled-button "Kickoff"])
    [:span.pa2
     (if (->> curr-action-state ::fsc/configuration :acting)
       [sc/spinny-icon]
       [sc/blank-icon])]]
   [:div
    (if (seq (clojure.set/intersection #{:success :failure} (->> curr-action-state ::fsc/configuration)))
      [util/stl action-sc-state :finish
       [sc/button "Finish"]]
      [sc/disabled-button "Finish"])]])
