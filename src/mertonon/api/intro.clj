(ns mertonon.api.intro
  "API for introduction to mertonon. After successful intro, should disable itself"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.server.middleware.validations :as val-mw]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]
            [mertonon.util.validations :as uvals]
            ))

(defn- do-intro [m]
  (let [body            (api-util/body-params m)
        username        (:username body)
        ;;;;
        ;;;;
        ;;;;
        mt-user!        nil
        password        (:password body)
        ;; don't keep it
        ;;;;
        ;;;;
        ;;;;
        password-login! nil
        ;;;;
        ;;;;
        ;;;;
        session!        nil]
    {:mt-user mt-user!
     :session session!}))

(defn intro-endpoint []
  {:post do-intro
   :name ::intro
   :data {:middleware
          [val-mw/wrap-mertonon-validations
           [(uvals/table-count-check mt-user-model/model 0 :already-introed)]]}})

(defn routes []
  [["/" (intro-endpoint)]])
