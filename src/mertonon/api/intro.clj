(ns mertonon.api.intro
  "API for introduction to mertonon. After successful intro, should disable itself"
  (:require [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.models.constructors :as mtc]
            [mertonon.models.mt-session :as mt-session-model]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]
            [mertonon.util.validations :as uvals]
            [tick.core :as t]))

(def validations [(uvals/table-count-check mt-user-model/model 0 :already-introed)])

(defn- need-intro? [m]
  (let [check! (uvals/throw-if-invalid! m validations)]
    {:status 200
     :body {:message true}}))

(defn- do-intro [m]
  (let [check!                (uvals/throw-if-invalid! m validations)
        body                  (api-util/body-params m)
        ;; TODO: make this in a transaction for real.
        ;; Need compatibility with test txn. Means that our wrapper needs to be with respect to savepoints or something
        {username :username
         email    :email
         password :password } body
        new-user              (mtc/->MtUser (uutils/uuid) email username)
        mt-user!              ((mt-user-model/model :create-one!) new-user)
        digest                (password-login-model/hash-password password)
        new-password          (mtc/->PasswordLogin (uutils/uuid) (:uuid new-user) :default digest)
        password-login!       ((password-login-model/model :create-one!) new-password)
        new-session           (mtc/->MtSession (uutils/uuid) (:uuid new-user)
                                               ;; we don't actually have non-jank session expiration now, so just say they're infinite
                                               (t/>> (t/instant) (t/new-duration 400 :days))
                                               new-user)
        session!              ((mt-session-model/model :create-one!) new-session)
        res                   {:mt-user mt-user!
                               :session (:uuid session!)}]
    {:status 200
     :body   res}))

(defn intro-endpoint []
  {:get  need-intro?
   :post do-intro
   :name ::intro})

(defn routes []
  [["/" (intro-endpoint)]])
