(ns mertonon.api.password-login
  "API for the separate table for the mertonon password auths corresponding to users.

  Not for directly logging in, but for doing CRUD for password logins"
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.constructors :as mtc]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.util.uuid :as uutils]
            [mertonon.util.validations :as uvals]))

;; TODO: put in the password api validation
(def validations [(uvals/nil-validation)])

(def banlist [:password-state :password-digest :recovery-token-hash])

(defn single-login-endpoint []
  {:get    (api-util/get-model password-login-model/model {:key-banlist banlist})
   :delete (api-util/delete-model password-login-model/model)
   :name   ::password-login})

(defn- password-create
  "Other create endpoints return created thing.
  Can't do that here. Also need to hash the password in BE"
  [m]
  (let [body                         (api-util/body-params m)
        {uuid         :uuid
         password     :password
         mt-user-uuid :mt-user-uuid} body
        digest                       (password-login-model/hash-password password)
        new-password                 ((password-login-model/model :create-one!)
                                      (mtc/->PasswordLogin uuid mt-user-uuid :default digest))]
    {:status 200 :body {:message :success}}))

(defn mass-login-endpoint []
  {:get    (api-util/get-models password-login-model/model {:key-banlist banlist})
   :post   password-create
   :delete (api-util/delete-models password-login-model/model)
   :name   ::password-logins})

(defn routes []
  [["/" (mass-login-endpoint)]
   ["/:uuid" (single-login-endpoint)]])
