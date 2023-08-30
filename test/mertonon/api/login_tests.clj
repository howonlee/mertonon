(ns mertonon.api.login-tests
  "Logging in tests"
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.walk :as walk]
            [mertonon.api.api-tests :as api-tests]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.server.handler :as app-handler]
            [mertonon.test-utils :as tu]))

(defn post-login! [member curr-app]
  (let [endpoint    "/api/v1/login/"
        res         (curr-app {:uri endpoint :request-method :post :body-params member})
        processed   (api-tests/process-app-response res)]
    processed))

(defspec just-login-a-bunch
  100
  (prop/for-all
    []
    nil))

(comment
  (require '[mertonon.models.mt-user :as mt-user-model])
  (require '[mertonon.models.password-login :as password-login-model])
  (require '[mertonon.models.constructors :as mtc])
  (require '[mertonon.util.uuid :as uutils])
  (let [mt-user-uuid (uutils/uuid)
        pwd-uuid     (uutils/uuid)
        pwd          "bleh mleh fleh"
        digest       (password-login-model/hash-password pwd)]
    ((mt-user-model/model :create-one!) (mtc/->MtUser mt-user-uuid ";DROP mt_user;--" ";drop mt_user;--"))
    ((password-login-model/model :create-one!) (mtc/->PasswordLogin pwd-uuid mt-user-uuid :default digest)))
  (let [curr-app (app-handler/app-handler)]
    (post-login! {:username ";DRop mt_user;--" :password "bleh mleh fleh"} curr-app)))
