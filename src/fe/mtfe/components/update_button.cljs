(ns mtfe.components.update-button
  "Update buttons"
  (:require [ajax.core :refer [json-request-format json-response-format]]
            [day8.re-frame.http-fx]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]))

(def default-labels
  {
   ;; State labels
   :initial  "Make your changes."
   :filled   "Press Update button to change things."
   :updating "Updating..."
   :success  "Successfully updated!"
   :failure  "Failed to update See error."
   :finished "Finished!"

   ;; Button labels
   :submit   "Update"
   :finish   "Finish"
   })
