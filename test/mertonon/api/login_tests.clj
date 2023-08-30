(ns mertonon.api.login-tests
  "Logging in tests"
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.walk :as walk]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.server.handler :as app-handler]
            [mertonon.test-utils :as tu]))

(defspec just-login-a-bunch
  100
  (prop/for-all
    [some crap]
    nil))
