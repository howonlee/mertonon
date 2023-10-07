(ns mtfe.components.validation-blurbs
  "Blurb components to go and complain when a validation fails"
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]
            ["react-tiny-popover" :refer [Popover]]))

;; ---
;; Validation failure displays
;; ---

(defn curr-blurb [state-path validation-key blurb]
  (let [procced  @(subscribe [:validation-procced? state-path validation-key])
        contents @(subscribe [:validation-contents state-path validation-key])]
    (if (and procced (fn? blurb))
      (blurb contents)
      blurb)))

(defn validated-link
  "Don't use this if you should be using a whole state machine.

  Doesn't work with function blurbs"
  [state-path validation-key disabled-name contents]
  (if @(subscribe [:validation-procced? state-path validation-key])
    [sc/disabled-button disabled-name]
    contents))

;; Rule of thumb: if user _just_ did something, do popover.
;; If user did something like 15 minutes ago and need to continuously annoy, use toast

(defn validation-toast [state-path validation-key blurb]
  (if @(subscribe [:validation-procced? state-path validation-key])
    [sc/validation-toast (curr-blurb state-path validation-key blurb)]
    [:span]))

(defn validation-popover [state-path validation-key blurb content]
  [popover {:isOpen @(subscribe [:validation-procced? state-path validation-key])
            :positions #js ["left"]
            :content (r/as-element [sc/popper (curr-blurb state-path validation-key blurb)])}
   [:span content]])
