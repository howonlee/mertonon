(ns mertonon.api.intro
  "API for introduction to mertonon. After successful intro, should disable itself"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [mertonon.api.util :as api-util]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.util.io :as uio]
            [mertonon.util.uuid :as uutils]))

(defn- do-intro [m]
  (let [body            (api-util/body-params m)
        username        nil
        mt-user!        nil
        password        nil
        password-login! nil]
    nil))

(defn intro-endpoint []
  {:post do-intro
   :name ::intro
   :data {:middlewares
          [:validation-that-this-thing-doesnt-already-have-mt-user]}})

(defn routes []
  [["/" (intro-endpoint)]])
