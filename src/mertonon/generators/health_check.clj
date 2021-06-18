(ns mertonon.generators.health-check
  "Health check generator for testing"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.models.constructors :as mtc]))

(defn generate-health-check []
  (gen/let [hc-uuid  gen/uuid]
    (mtc/->HealthCheck hc-uuid)))
