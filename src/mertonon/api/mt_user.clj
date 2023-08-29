(ns mertonon.api.mt-user
  "API for mertonon users."
  (:require [clojure.data.json :as json]
            [mertonon.api.util :as api-util]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.util.uuid :as uutils]))

(defn single-user-endpoint []
  {:get    (api-util/get-model mt-user-model/model)
   :delete (api-util/delete-model mt-user-model/model)
   :name   ::mt-user})

(defn mass-user-endpoint []
  {:get    (api-util/get-models mt-user-model/model)
   :post   (api-util/create-model mt-user-model/model)
   :delete (api-util/delete-models mt-user-model/model)
   :name   ::mt-users})

(defn routes []
  [["/" (mass-user-endpoint)]
   ["/:uuid" (single-user-endpoint)]])