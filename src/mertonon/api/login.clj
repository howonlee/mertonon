(ns mertonon.api.login
  "API for logging in. Not to be confused with password-login"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.models.constructors :as mtc]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.models.mt-session :as mt-session-model]
            [mertonon.util.io :as uio]
            [mertonon.util.registry :as registry]
            [mertonon.util.uuid :as uutils]
            [tick.core :as t]))

;; TODO: setting
(def session-length-days 400)

(defn do-login [m]
  ;; TODO: login attempt limits
  (let [body      (api-util/body-params m)
        user-q    ((mt-user-model/model :read-where-joined)
                   {:join-tables      [:mertonon.password_login]
                    :join-col-edges   [[:mertonon.mt_user.uuid :mertonon.password_login.mt_user_uuid]]
                    :where-clause     [:= :mertonon.mt_user.canonical_username
                                       (-> body
                                           mt-user-model/canonicalize-username
                                           :canonical-username)]
                    :raw-table->table registry/raw-table->table
                    :table->model     registry/table->model})
        is-valid? (and (= (count (:mertonon.mt-users user-q)) 1)
                       (= (count (:mertonon.password-logins user-q)) 1)
                       (password-login-model/password-check
                         (:password body)
                         (->> user-q :mertonon.password-logins first :password-digest)))]
    (if (not is-valid?)
      {:status 401 :body {:message "Login invalid somehow. Check the username and password."}}
      (let [curr-user   (->> user-q :mertonon.mt-users first)
            curr-time   (t/instant)
            expiration  (t/>> curr-time (t/new-duration session-length-days :days))
            session-res ((mt-session-model/model :create-one!) (mtc/->MtSession
                                                                 (uutils/uuid)
                                                                 (curr-user :uuid)
                                                                 expiration
                                                                 curr-user))]
        {:status 200
         ;;;;;
         ;;;;;
         ;;;;;
         :body (json/write-str {:session (:uuid session-res)})}))))

(defn login-endpoint []
  {:post do-login
   :name ::login})

(defn routes []
  [["/" (login-endpoint)]])
