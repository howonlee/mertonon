(ns mertonon.server.middleware.auth
  "Middlewares for authing.

  Don't use buddy because it's based on ACL and we want RBAC only no ACL

  Why? we want some normal person pushing buttons to do authz -
  ACL is a far more grognardy technology, overall.
  enterpriseland is RBAC-land. or ABAC-land, really.

  Don't use the validation middleware because we want to complicate it a fair bit"
  (:require [clojure.string :as str]
            [mertonon.util.config :as mt-config]))

(def auth-exception-endpoints
  #{"/"
    "/api/v1/session/"
    "/api/v1/health_check/"})

;; TODO: rbac

(defn wrap-mertonon-auth
  ([handler]
   (wrap-mertonon-auth handler {}))
  ([handler options]
   (fn [request]
     (cond
       (contains? auth-exception-endpoints (:uri request))
       (handler request)
       (str/starts-with? (:uri request) "/public")
       (handler request)
       (seq (:session request))
       (handler request)
       :else
       {:status 401 :body {:message "Unauthorized"}}))))
