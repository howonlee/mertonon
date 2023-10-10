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
