(ns mertonon.api.intro-tests
  "Intro tests"
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.walk :as walk]
            [mertonon.api.api-tests :as api-tests]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.server.handler :as handler]
            [mertonon.test-utils :as tu]
            [mertonon.util.io :as uio]))

(defn post-intro! [member curr-app]
  (let [endpoint    "/api/v1/intro/"
        res         (curr-app {:uri endpoint :request-method :post :body-params member})
        slurped     (update res :body (comp uio/maybe-slurp uio/maybe-json-decode))]
    slurped))

(defspec intro-not-idempotent
  1
  (prop/for-all
    [fst-generated authn-gen/generate-password-logins
     snd-generated authn-gen/generate-password-logins]
    (tu/with-test-txn
      ;; delete all users
      ;; post into the intro
      ;; post into the intro again
      ;; make sure first post is 200, second is 400
      nil
      )))
