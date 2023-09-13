(ns mertonon.core
  (:require [clojure.tools.namespace.repl :as tn]
            [mertonon.server :refer [server]]
            [mertonon.setup :as setup :refer [db-migration warmup generate]]
            [mount.core :as mount :refer [defstate]]
            [taoensso.timbre :as timbre :refer [log]])
  ;; Meaning, this is entry point for uberjar
  (:gen-class))

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
