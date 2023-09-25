(ns mertonon.api.routes
  (:require [mertonon.api.allocation-cue :as api-allocation-cue]
            [mertonon.api.cost-object :as api-cost-object]
            [mertonon.api.entry :as api-entry]
            [mertonon.api.fe-test-generators :as fe-test-generators]
            [mertonon.api.generators :as api-generators]
            [mertonon.api.grid :as api-grid]
            [mertonon.api.health-check :as health-check]
            [mertonon.api.input :as api-input]
            [mertonon.api.intro :as api-intro]
            [mertonon.api.layer :as api-layer]
            [mertonon.api.session :as api-session]
            [mertonon.api.loss :as api-loss]
            [mertonon.api.mt-user :as api-mt-user]
            [mertonon.api.password-login :as api-password-login]
            [mertonon.api.weight :as api-weight]
            [mertonon.api.weightset :as api-weightset]
            ))

(defn routes []
  [(into ["/allocation_cue"] (api-allocation-cue/routes))
   (into ["/cost_object"] (api-cost-object/routes))
   (into ["/entry"] (api-entry/routes))
   (into ["/fe_generators"] (fe-test-generators/fe-generator-routes))
   (into ["/generators"] (api-generators/generator-routes))
   (into ["/grid"] (api-grid/routes))
   (into ["/health_check"] (health-check/health-check-routes))
   (into ["/input"] (api-input/routes))
   (into ["/intro"] (api-intro/routes))
   (into ["/layer"] (api-layer/routes))
   (into ["/loss"] (api-loss/routes))
   (into ["/mt_user"] (api-mt-user/routes))
   (into ["/password_login"] (api-password-login/routes))
   (into ["/session"] (api-session/routes))
   (into ["/weightset"] (api-weightset/routes))
   (into ["/weight"] (api-weight/routes))])
