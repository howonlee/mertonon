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
  (require '[mertonon.models.constructors :as mtc])
  (require '[mertonon.util.uuid :as uuuid])
  ((mt-user-model/model :create-one!) (mtc/->MtUser some crap))
  (let [curr-app (app-handler/app-handler)]
    (post-login! {:username "bob dobbs" :password "flehblehwhleh"} curr-app)))
