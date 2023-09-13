(ns mertonon.server.middleware.auth
  "Middlewares for authing.

  Don't use buddy because it's based on ACL and we want RBAC"
  (:require [mertonon.models.mt-session :as mt-session-model]))

(defn wrap-mertonon-auth
  ([handler]
   (wrap-mertonon-auth handler {}))
  ([handler options]
   (fn [request]
     ;;;;
     ;;;;
     ;;;;
     ;;;;
     nil)))
