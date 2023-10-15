(ns mertonon.api.mt-user
  "API for mertonon users."
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.util.uuid :as uutils]))

(def banlist [:password-state :password-digest :recovery-token-hash])

(defn single-user-endpoint []
  {:get    (api-util/get-model mt-user-model/model)
   :put    (api-util/update-model mt-user-model/model)
   :delete (api-util/delete-model mt-user-model/model)
   :name   ::mt-user})

(defn mass-user-endpoint []
  {:get    (api-util/get-models mt-user-model/model)
   :post   (api-util/create-model mt-user-model/model)
   :put    (api-util/update-model mt-user-model/model)
   :delete (api-util/delete-models mt-user-model/model)
   :name   ::mt-users})

(def join-config
  {:join-tables    [:mertonon.password_login]
   :join-col-edges [[:mertonon.mt_user.uuid :mertonon.password_login.mt_user_uuid]]
   :key-banlist    banlist})

(defn password-login-join-endpoint []
  {:get  (api-util/get-joined-model mt-user-model/model join-config)
   :name ::mt-user-password-login})

(defn password-login-mass-join-endpoint []
  {:get  (api-util/get-joined-models mt-user-model/model join-config)
   :name ::mt-user-mass-password-login})

(defn curr-user [m]
  {:status 200 :body (-> m :session :value)})

(defn curr-user-endpoint []
  {:get curr-user
   :name ::curr-mt-user})

(defn routes []
  [["/" (mass-user-endpoint)]
   ["/:uuid" (single-user-endpoint)]
   ["/:uuid/curr_password_login" (password-login-join-endpoint)]
   ["/_/password_login" (password-login-mass-join-endpoint)]
   ["/_/me" (curr-user-endpoint)]])
