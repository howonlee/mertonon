(ns mertonon.api.password-login
  "API for the separate table for the mertonon password auths corresponding to users.

  Not for directly logging in, but for doing CRUD for password logins"
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.util.uuid :as uutils]
            [mertonon.util.validations :as uvals]
            ))

(def validations [(uvals/join-count-check mt-user-model/model :password_login [some crap])])

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
