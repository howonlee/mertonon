(ns mertonon.api.health-check
  "API for a deepish health check.
  Not only the up-ness of the overall program but also having a valid DB connection we can insert into,
  which exercises a moderate amount of the set of classes. But not anywhere close to a decent test.

  Monitoring and QA are not fundamentally different things. Machine learning and testing are not formally different things.

  This poses authn and authz difficulties, but you would need to deal with those in any good faith testing anyways."
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.health-check :as hc-model]
            [mertonon.util.uuid :as uutils]))

;; ---
;; DB Insert Health Check
;; ---

(defn mass-endpoint []
  {:post   (api-util/create-model hc-model/model)
   :name   ::health-checks})

(defn health-check-routes []
  [["/" (mass-endpoint)]])
