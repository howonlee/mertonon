(ns mertonon.api.routes
  (:require [mertonon.api.cost-object :as api-cost-object]
            [mertonon.api.entry :as api-entry]
            [mertonon.api.fe-test-generators :as fe-test-generators]
            [mertonon.api.generators :as api-generators]
            [mertonon.api.grid :as api-grid]
            [mertonon.api.health-check :as health-check]
            [mertonon.api.layer :as api-layer]
            [mertonon.api.loss :as api-loss]
            [mertonon.api.input :as api-input]
            [mertonon.api.weight :as api-weight]
            [mertonon.api.weightset :as api-weightset]
            [mertonon.api.allocation-cue :as api-allocation-cue]))

(defn routes []
  [(into ["/health_check"] (health-check/health-check-routes))
   (into ["/fe_generators"] (fe-test-generators/fe-generator-routes))
   (into ["/generators"] (api-generators/generator-routes))
   (into ["/grid"] (api-grid/routes))
   (into ["/layer"] (api-layer/routes))
   (into ["/cost_object"] (api-cost-object/routes))
   (into ["/entry"] (api-entry/routes))
   (into ["/weightset"] (api-weightset/routes))
   (into ["/weight"] (api-weight/routes))
   (into ["/loss"] (api-loss/routes))
   (into ["/input"] (api-input/routes))
   (into ["/allocation_cue"] (api-allocation-cue/routes))])
