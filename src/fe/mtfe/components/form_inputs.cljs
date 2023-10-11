(ns mtfe.components.form-inputs
  "Form inputs"
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]
            ["react-datepicker" :default DatePicker]))

;; ---
;; React class adapters
;; ---

(def date-picker (r/adapt-react-class DatePicker))

;; ---
;; Utils
;; ---

(defn evt->val [synthetic-evt]
  (.. synthetic-evt
      -nativeEvent
      -srcElement
      -value))

;; ---
;; Inputs and Form Stuff
;; ---

;; TODO: be less idiosyncratic to create

(defn curr-evt-key [evt-key]
  (if (some? evt-key) evt-key :mutate-create-state))

(defn state-text-input [state-path param-path placeholder & [evt-key]]
  [sc/input {:type        "text"
             :placeholder placeholder
             :on-change   #(dispatch [(curr-evt-key evt-key) state-path param-path (evt->val %)])}])

(defn state-password-input [state-path param-path placeholder & [evt-key]]
  [sc/input {:type        "password"
             :placeholder placeholder
             :on-change   #(dispatch [(curr-evt-key evt-key) state-path param-path (evt->val %)])}])

(defn state-select-input [state-path param-path choices & [evt-key]]
  [sc/select {:on-change #(dispatch [(curr-evt-key evt-key) state-path param-path (evt->val %)])
              :value     @(subscribe (-> [:sidebar-state] (into state-path) (into param-path)))}
   [sc/select-option {:value ""} "---"]
   (for [member choices] ^{:key (:uuid member)}
      [sc/select-option {:value (:uuid member)} (:name member)])])

;; TODO: get it to be logspace!!!!
(defn state-range-input
  ([state-path param-path]
  (state-range-input state-path param-path 0 200 1))
  ([state-path param-path _min _max step & [evt-key]]
   [sc/input {:type     "range"
              ;;;; logarithmic values...
              ;;;;;;;
              ;;;;;;;
              ;;;;;;;
              ;;;;;;;
              :on-input #(dispatch [(curr-evt-key evt-key) state-path param-path (evt->val %)])
              :value    @(subscribe (-> [:sidebar-state] (into state-path) (into param-path)))
              :min      _min
              :max      _max
              :step     step}]))

(defn state-datepicker [state-path param-path & [evt-key]]
  [date-picker {:popper-placement "left"
                :selected         @(subscribe (-> [:sidebar-state] (into state-path) (into param-path)))
                :on-select        #(dispatch [(curr-evt-key evt-key) state-path param-path %])}])
