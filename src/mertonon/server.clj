(ns mertonon.server
  (:require [mertonon.server.handler :as handler]
            [mertonon.util.i18n :refer [trs]]
            [mertonon.util.config :as mt-config]
            [mount.core :refer [defstate]]
            [ring.adapter.jetty :as ring-jetty]
            [taoensso.timbre :as timbre :refer [log]])
  (:import [org.eclipse.jetty.server.handler AbstractHandler StatisticsHandler]))

(defn shutdown-server!
  [curr-server]
  (log :info (trs "Shutting down embedded server..."))
  (.stop curr-server)
  (.join curr-server))

(defn server-start!
  "Make sure there's a server"
  []
  ;; TODO: proper configs for jetty
  (log :info (trs "Starting embedded server..."))
  (let [curr-config (mt-config/config)
        curr-server (#'ring-jetty/create-server {:port   (curr-config :mt-port)
                                                 :join?  true})
        _           (.setHandler curr-server (#'ring-jetty/proxy-handler (handler/app-handler)))
        _           (.start curr-server)]
    curr-server))

(defstate server
  "Server singleton"
  :start (server-start!)
  :stop  (shutdown-server! server))
