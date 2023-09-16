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

(defn simple-dummy-validation [random-keyword]
  (fn [req] random-keyword))

(defn dummy-validation [random-keyword]
  (fn [req] {random-keyword []}))

(defn filled-dummy-validation [random-keyword random-member]
  (fn [req] {random-keyword [random-member]}))

(defspec two-validations-add-reses
  100
  (prop/for-all [curr-keyword gen/keyword
                 members      (gen/vector-distinct gen/string 2)]
                (let [dummy-1 (filled-dummy-validation curr-keyword (first members))
                      dummy-2 (filled-dummy-validation curr-keyword (second members))]
                  (middleware do the thing)
                  (call a trivial handler)
                  )))

;; (defspec unfilled-validation-is-idempotent
;;   100
;;   nil)

(comment (run-tests))
