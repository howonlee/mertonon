(ns mertonon.server.middleware.auth
  "Middlewares for authing.

  Don't use buddy because it's based on ACL and we want RBAC
  Don't use the validation middleware because we want to complicate it a fair bit"
  (:require [clojure.string :as str]
            [mertonon.util.config :as mt-config]))

(def auth-exception-endpoints
  #{"/"
    "/api/v1/login/"
    "/api/v1/health_check/"})

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
       (if ((mt-config/feature-flags) :auth)
         {:status 401 :body {:message "Unauthorized"}}
         (handler request))))))
