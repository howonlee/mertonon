(ns mertonon.core
  (:require [clojure.tools.namespace.repl :as tn]
            [mertonon.api.generators :as generator-api]
            [mertonon.models.constructors :as mc]
            [mertonon.server :refer [server]]
            [mertonon.server.handler :as handler]
            [mertonon.util.migrations :as migrations]
            [mertonon.util.i18n :refer [trs]]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]
            [mount.core :as mount :refer [defstate]]
            [migratus.core :as migratus]
            [taoensso.timbre :as timbre :refer [log]])
  ;; Meaning, this is entry point for uberjar
  (:gen-class))

(defn- db-setup! []
  (do
    (migratus/init migrations/config)
    (migratus/migrate migrations/config)))

(defn- warmup!
  []
  (log :info "Warmup request sending...")
  ((handler/app-handler) {:uri            "/api/v1/health_check"
                          :request-method :post
                          :body-params    (mc/->HealthCheck (uutils/uuid))})
  (uio/load-array-impl!)
  (log :info "Finished sending warmup request."))

(defstate db-migration
  "DB migration"
  :start (db-setup!))

(defstate warmup
  :start (warmup!))

(defstate generate
  :start (generator-api/generate-demo-net-atom-and-set!))

(defn -main
 "Launch Mertonon with args"
  [& [cmd & args]]
  (log :info "Mertonon initialization beginning...")
  (mount/start)
  (log :info "Mertonon initialization finished!"))

;; ---
;; Utils for repling
;; ---

(defn- reset-all []
  (mount/stop)
  (tn/refresh)
  (mount/start))

(comment (reset-all))

(comment (tn/refresh))

(comment (migratus/create migrations/config "session-fixes"))

