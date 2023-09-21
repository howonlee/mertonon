(ns mertonon.api.entry
  "API for entries"
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.entry :as entry-model]
            [mertonon.util.uuid :as uutils]))

(defn single-endpoint []
  {:get    (api-util/get-model entry-model/model)
   :delete (api-util/delete-model entry-model/model)
   :name   ::entry})

(defn mass-endpoint []
  {:get    (api-util/get-models entry-model/model)
   :post   (api-util/create-model entry-model/model)
   :delete (api-util/delete-models entry-model/model)
   :name   ::entries})

(defn routes []
  [["/" (mass-endpoint)]
   ["/:uuid" (single-endpoint)]])
