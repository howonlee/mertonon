(ns mertonon.api.intro
  "API for introduction to mertonon. After successful intro, should disable itself"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]))

(defn- do-intro [m]
  (let [body     (-> m :body-params
                     uio/maybe-slurp
                     uio/maybe-json-decode
                     walk/keywordize-keys)
        username nil
        password nil]
    nil))

(defn intro-endpoint []
  {:post do-intro
   :name ::intro})

(defn routes []
  [["/" (intro-endpoint)]])
