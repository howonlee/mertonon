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
