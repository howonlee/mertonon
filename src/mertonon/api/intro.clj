(ns mertonon.api.intro
  "API for introduction to mertonon. After successful intro, should disable itself"
  (:require [clojure.data.json :as json]
            [mertonon.api.util :as api-util]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.util.uuid :as uutils]))

(defn intro-endpoint []
  {:post (fn [m]
           nil)
   :name ::intro})

(defn routes []
  [["/" (intro-endpoint)]])
