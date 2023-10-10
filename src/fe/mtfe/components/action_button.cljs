(ns mtfe.components.action-button
  "Action buttons for arbitrary action kickoff."
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]))

(def default-labels
  {;; State labels
   :initial  "Press Kickoff button to kick off."
   :filled   "Press Kickoff button to kick off."
   :acting   "Kicking off..."
   :success  "Successfully kicked off!"
   :failure  "Failed to kick off."
   :finished "Finished."

   ;; Button labels
   :kickoff  "Kickoff"
   :finish   "Finish"})

;; (defn action-button [curr-action-state action-sc-state sidebar-state]
;;   [sc/border-region
;;    [:div.pa2
;;     (action-state-blurb (->> curr-action-state ::fsc/configuration))]
;;    [:div
;;     (if (and (empty? (->> @sidebar-state :validation-errors))
;;              (or
;;                (->> curr-action-state ::fsc/configuration :initial)
;;                (->> curr-action-state ::fsc/configuration :filled)))
;;       [util/stl action-sc-state :submit
;;        [sc/button "Kickoff"]
;;        (->> @sidebar-state :curr-action-params)]
;;       [sc/disabled-button "Kickoff"])
;;     [:span.pa2
;;      (if (->> curr-action-state ::fsc/configuration :acting)
;;        [sc/spinny-icon]
;;        [sc/blank-icon])]]
;;    [:div
;;     (if (seq (clojure.set/intersection #{:success :failure} (->> curr-action-state ::fsc/configuration)))
;;       [util/stl action-sc-state :finish
;;        [sc/button "Finish"]]
;;       [sc/disabled-button "Finish"])]])
