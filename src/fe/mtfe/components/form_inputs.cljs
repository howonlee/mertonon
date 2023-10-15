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

(defn- form-path [state-path param-path]
  (-> [:sidebar-state] (into state-path) (into param-path)))

;; ---
;; Inputs and Form Stuff
;; ---

(defn curr-evt-key [evt-key]
  (if (some? evt-key) evt-key :mutate-create-state))

(defn state-text-input [state-path param-path placeholder & [evt-key]]
  [sc/input {:type        "text"
             :placeholder placeholder
             :value       @(subscribe (form-path state-path param-path))
             :on-change   #(dispatch [(curr-evt-key evt-key) state-path param-path (evt->val %)])}])

(defn state-password-input [state-path param-path placeholder & [evt-key]]
  [sc/input {:type        "password"
             :placeholder placeholder
             :value       @(subscribe (form-path state-path param-path))
             :on-change   #(dispatch [(curr-evt-key evt-key) state-path param-path (evt->val %)])}])

(defn state-select-input [state-path param-path choices & [evt-key]]
  [sc/select {:on-change #(dispatch [(curr-evt-key evt-key) state-path param-path (evt->val %)])
              :value     @(subscribe (form-path state-path param-path))}
   [sc/select-option {:value ""} "---"]
   (for [member choices] ^{:key (:uuid member)}
      [sc/select-option {:value (:uuid member)} (:name member)])])

(defn state-range-input
  ([state-path param-path]
  (state-range-input state-path param-path 0 200 1))
  ([state-path param-path _min _max step & [evt-key]]
   [sc/input {:type     "range"
              :on-input #(dispatch [(curr-evt-key evt-key) state-path param-path (evt->val %)])
              :value    @(subscribe (form-path state-path param-path))
              :min      _min
              :max      _max
              :step     step}]))

(defn state-power-range-input
  ([state-path param-path]
   (state-power-range-input state-path param-path 1 100 1))
  ([state-path param-path _min _max step & [evt-key]]
   (let [exp-curr-val (fn [inp-state]
                        (. js/Math pow (evt->val inp-state) 2))]
   [sc/input {:type     "range"
              :on-input #(dispatch [(curr-evt-key evt-key) state-path param-path (exp-curr-val %)])
              :value    (. js/Math sqrt @(subscribe (form-path state-path param-path)))
              :min      _min
              :max      _max
              :step     step}])))

(defn state-datepicker [state-path param-path & [evt-key]]
  [date-picker {:popper-placement "left"
                :selected         @(subscribe (form-path state-path param-path))
                :on-select        #(dispatch [(curr-evt-key evt-key) state-path param-path %])}])
