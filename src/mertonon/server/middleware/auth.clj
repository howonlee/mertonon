(ns mertonon.server.middleware.auth
  "Middlewares for authing.

  Don't use buddy because it's based on ACL and we want RBAC"
  (:require [clojure.string :as str]))

(def auth-exception-endpoints
  #{"/api/v1/login/"})

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
