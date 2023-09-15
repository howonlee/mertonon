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
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.server.handler :as handler]
            [mertonon.test-utils :as tu]
            [mertonon.util.io :as uio]))

(defspec intro-not-idempotent
  1
  (prop/for-all
    [generated    authn-gen/generate-password-logins]
    (tu/with-test-txn
      nil
      )))
