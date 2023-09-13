(ns mertonon.setup
  (:require [mertonon.api.generators :as generator-api]
            [mertonon.models.constructors :as mc]
            [mertonon.server.handler :as handler]
            [mertonon.util.io :as uio]
            [mertonon.util.migrations :as migrations]
            [mertonon.util.uuid :as uutils]
            [migratus.core :as migratus]
            [mount.core :as mount :refer [defstate]]
            [taoensso.timbre :as timbre :refer [log]]))

(defn- db-setup! []
  (do
    (migratus/init migrations/config)
    (migratus/migrate migrations/config)))

(defstate db-migration
  "DB migration"
  :start (db-setup!))

(defn- warmup!
  []
  (log :info "Warmup request sending...")
  ;;;;;;;
  ;;;;;;;
  ;;;;;;; make the session here too
  ;;;;;;;
  ;;;;;;;
  ((handler/app-handler) {:uri            "/api/v1/health_check"
                          :request-method :post
                          :body-params    (mc/->HealthCheck (uutils/uuid))})
  (uio/load-array-impl!)
  (log :info "Finished sending warmup request."))

(defstate warmup
  :start (warmup!))

(defstate generate
  :start (generator-api/generate-demo-net-atom-and-set!))
