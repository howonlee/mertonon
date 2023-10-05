(ns mtfe.components.form-inputs
  "Form inputs"
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]
            ["react-datepicker" :default DatePicker]
            ["react-tiny-popover" :refer [Popover]]))

;; ---
;; React class adapters
;; ---

(def date-picker (r/adapt-react-class DatePicker))
(def popover (r/adapt-react-class Popover))

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

(defn state-text-input [state-path param-path placeholder]
  [sc/input {:type        "text"
             :placeholder placeholder
             :on-change   #(dispatch [:mutate-create-state state-path param-path (evt->val %)])}])

(defn state-password-input [state-path param-path placeholder]
  [sc/input {:type        "password"
             :placeholder placeholder
             :on-change   #(dispatch [:mutate-create-state state-path param-path (evt->val %)])}])

;; (defn state-select-input [state-path choices path]
;;   [sc/select {:on-change (mt-statechart/mutate-from-dom-event-handler sc-state path)
;;               :value     (get-in @state path)}
;;    [sc/select-option {:value ""} "---"]
;;    (for [member choices] ^{:key (:uuid member)}
;;      [sc/select-option {:value (:uuid member)} (:name member)])])
;; 
;; (defn state-range-input
;;   ([state-path]
;;   (state-range-input state-path 0 1000 5))
;;   ([state-path _min _max step]
;;    [sc/input {:type     "range"
;;               :on-input (mt-statechart/mutate-from-dom-event-handler sc-state path)
;;               :value    (get-in @state path)
;;               :min      _min
;;               :max      _max
;;               :step     step}]))
;; 
;; (defn state-datepicker [state-path]
;;   [date-picker {:popper-placement "left"
;;                 :selected         (get-in @state path)
;;                 :on-select        (mt-statechart/mutate-from-plain-function-handler sc-state path)}])
