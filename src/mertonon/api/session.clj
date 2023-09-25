(ns mertonon.api.session
  "API for logging in and out. Not to be confused with password-login"
  (:require [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.models.constructors :as mtc]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.models.mt-session :as mt-session-model]
            [mertonon.util.config :as config]
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
                                                                 curr-user))
            curr-config      (config/config)]
        {:status  200
         :cookies {:ring-session
                   {:value     (str (:uuid session-res))
                    :http-only true
                    :max-age   (* 24 60 60 session-length-days)
                    :same-site :strict
                    :path      "/"
                    :domain    (curr-config :mt-host)}}
         :body    {}}))))

(defn do-logout [m]
  (let [printo (println m)]
    {:status 200
     :body {}}))

(defn session-endpoint []
  {:post   do-login
   :delete do-logout
   :name   ::session})

(defn routes []
  [["/" (session-endpoint)]])
