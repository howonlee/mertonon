(ns mertonon.models.mt-user-test
  "Specific mt user models"
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.generators.mt-user :as mt-user-gen]
            [mertonon.generators.net :as net-gen]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as reg]))

;; ---
;; User tests
;; ---

(defspec unique-constraint some crap)

;; generate capital vs lowercase and check constraint?
;; (defspec canonicalized-usernames some crap)

;; ---
;; Password login tests
;; ---

(comment (run-tests))
