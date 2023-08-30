(ns mertonon.api.login
  "API for logging in. Not to be confused with password-login"
  (:require [clojure.data.json :as json]
            [mertonon.api.util :as api-util]
            [mertonon.util.uuid :as uutils]))

(defn do-login [m]
  (println (:body-params m))
  nil)

(defn login-endpoint []
  {:post do-login
   :name ::login})

(defn routes []
  [["/" (login-endpoint)]])
