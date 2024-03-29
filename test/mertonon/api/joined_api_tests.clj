(ns mertonon.api.joined-api-tests
  "Joined API tests"
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

;; get the made up random endpoint
;; execute the read properties on it

(defn do-setup! [net]
  nil)

(defn test-inp [net]
  {:read-one (fn [] nil)
   :read-many (fn [] nil)
   :setup (do-setup! net)})

;; get-all-exercise

;; mass-joined-exercise

;; single-joined-exercise
