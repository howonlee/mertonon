(ns mertonon.server.middleware.json
  "ring-json doesn't work with data.json, cheshire only. which is why this exists"
  (:require [clojure.data.json :as json]
            [ring.util.response :refer [content-type]]))

(def json-endpoints
  #{"/api/v1"})

(defn wrap-json
  [handler]
  (fn [request]
    (if (contains? json-endpoints (:uri request))
      (let [resp         (handler request)
            updated-resp (update-in resp [:body] json/write-str)]
        (if (contains? (:headers updated-resp) "Content-Type")
          updated-resp
          (content-type updated-resp "application/json; charset=utf-8")))
      (handler request))))
