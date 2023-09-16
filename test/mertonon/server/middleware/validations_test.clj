(ns mertonon.server.middleware.validations-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.models.mt-session :as mt-session-model]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.server.handler :as handler]
            [mertonon.server.middleware.validations :as mt-validations-middleware]
            [mertonon.test-utils :as tu]
            [mertonon.util.config :as mt-config]
            [mertonon.util.io :as uio]))

(defn dummy-validation [req random-keyword]
  {random-keyword []})

(defn filled-dummy-validation [req random-keyword random-member]
  {random-keyword [random-member]})

(defspec two-dummy-validations-add-to-two-validation-res-thingies
  100
  nil)

(defspec individual-dummy-validation-is-idempotent
  100
  nil)
