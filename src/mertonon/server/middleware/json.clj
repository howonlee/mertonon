(ns mertonon.server.middleware.json
  "ring-json doesn't work with data.json, cheshire only. which is why this exists"
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [ring.util.response :refer [content-type]]))

(def json-endpoints "/api/v1")

(defn wrap-json
  [handler]
  (fn [request]
    (if (some #(str/includes? % json-endpoints) (:uri request))
      (update-in (handler request) [:body] json/write-str)
      (handler request))))
