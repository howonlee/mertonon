(ns mertonon.api.input
  "API for inputs"
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.input :as input-model]))

(defn single-endpoint []
  {:get    (api-util/get-model input-model/model)
   :put    (api-util/update-model input-model/model)
   :delete (api-util/delete-model input-model/model)
   :name   ::input})

(defn mass-endpoint []
  {:get    (api-util/get-models input-model/model)
   :post   (api-util/create-model input-model/model)
   :put    (api-util/update-model input-model/model)
   :delete (api-util/delete-models input-model/model)
   :name   ::inputs})

;; TODO: denormalized input stuff
;; TODO: forward pass stuff
;; TODO: grad stuff

(defn routes []
  [["/" (mass-endpoint)]
   ["/:uuid" (single-endpoint)]])
