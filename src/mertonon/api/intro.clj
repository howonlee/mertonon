(ns mertonon.api.intro
  "API for introduction to mertonon. After successful intro, should disable itself"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.models.constructors :as mtc]
            [mertonon.models.mt-session :as mt-session-model]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.server.middleware.validations :as val-mw]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]
            [mertonon.util.validations :as uvals]
            [tick.core :as t]))

(defn- do-intro [m]
  (let [body            (api-util/body-params m)
        ;; TODO: make this in a transaction for real
        username        (:username body)
        email           (:email body)
        new-user        (mtc/->MtUser (uutils/uuid) email username)
        mt-user!        ((mt-user-model/model :create-one!) new-user)
        password        (:password body)
        digest          (password-login-model/hash-password password)
        new-password    (mtc/->PasswordLogin (uutils/uuid) (:uuid new-user) :default digest)
        password-login! ((password-login-model/model :create-one!) new-password)
        new-session     (mtc/->MtSession (uutils/uuid) (:uuid new-user)
                                         ;; we don't actually have non-jank session exp now, so just say they're infinite
                                         (t/>> (t/instant) (t/new-duration 100 :years))
                                         new-user)
        session!        ((mt-session-model/model :create-one!) new-session)]
    {:status 200
     :body {:mt-user mt-user!
            :session (:uuid session!)}}))

(defn intro-endpoint []
  {:post do-intro
   :name ::intro
   :data {:middleware
          [val-mw/wrap-mertonon-validations
           [(uvals/table-count-check mt-user-model/model 0 :already-introed)]]}})

(defn routes []
  [["/" (intro-endpoint)]])
