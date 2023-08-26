(ns mertonon.api.loss
  "API for losss"
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.loss :as loss-model]))

(defn single-endpoint []
  {:get    (api-util/get-model loss-model/model)
   :delete (api-util/delete-model loss-model/model)
   :name   ::loss})

(defn mass-endpoint []
  {:get    (api-util/get-models loss-model/model)
   :post   (api-util/create-model loss-model/model)
   :delete (api-util/delete-models loss-model/model)
   :name   ::losses})

(defn routes []
  [["/" (mass-endpoint)]
   ["/:uuid" (single-endpoint)]])
