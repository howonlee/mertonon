(ns mertonon.api.login
  "API for logging in. Not to be confused with password-login"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]))

(defn do-login [m]
  (let [body      (-> m :body-params uio/maybe-slurp uio/maybe-json-decode walk/keywordize-keys)
        user      (get where joined)
        is-valid? some crap]
    (if (not is-valid?)
      some crap
      (let [session-res create the session!]
        {:status 200 :body {:session-uuid session-res}}))))

(defn login-endpoint []
  {:post do-login
   :name ::login})

(defn routes []
  [["/" (login-endpoint)]])
