(ns mertonon.api.password-login
  "API for the separate table for the mertonon password auths corresponding to users."
  (:require [clojure.data.json :as json]
            [mertonon.api.util :as api-util]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.util.uuid :as uutils]))

(defn single-login-endpoint []
  {:get    (api-util/get-model password-login-model/model)
   :delete (api-util/delete-model password-login-model/model)
   :name   ::password-login})

(defn mass-login-endpoint []
  {:get    (api-util/get-models password-login-model/model)
   :post   (api-util/create-model password-login-model/model)
   :delete (api-util/delete-models password-login-model/model)
   :name   ::password-logins})

(defn routes []
  [["/" (mass-login-endpoint)]
   ["/:uuid" (single-login-endpoint)]])
