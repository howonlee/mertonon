(ns mertonon.api.login
  "API for logging in. Not to be confused with password-login"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.models.mt-session :as mt-session-model]
            [mertonon.util.io :as uio]
            [mertonon.util.registry :as registry]
            [mertonon.util.uuid :as uutils]))

(defn do-login [m]
  ;; TODO: login attempt limits
  (let [body      (-> m :body-params uio/maybe-slurp uio/maybe-json-decode walk/keywordize-keys)
        user      ((mt-user-model/model :read-where-joined)
                   {:join-tables      [:mertonon.mt_user :mertonon.password_login]
                    :join-col-edges   [[:mertonon.mt_user.uuid :mertonon.password_login.mt_user_uuid]]
                    :where-clause     [:= :mertonon.mt_user.username (:username body)]
                    :raw-table->table registry/raw-table->table
                    :table->model     registry/table->model})
        is-valid? (password-login-model/password-check (:password body) (:password-digest user))]
    (if (not is-valid?)
      {:status 401 :body {:message "Login invalid somehow. Check the username and password."}}
      (let [session-res ((mt-session-model/model :create-one!) (mtc/->MtSession
                                                                 some crap
                                                                  some crap
                                                                  some crap
                                                                  some other crap))]
        {:status 200 :body {:session-uuid session-res}}))))

(defn login-endpoint []
  {:post do-login
   :name ::login})

(defn routes []
  [["/" (login-endpoint)]])
