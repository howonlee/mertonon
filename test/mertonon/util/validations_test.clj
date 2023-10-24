(ns mertonon.util.validations-test
  "Tests for the act of validating and for the individual validations if we see the need."
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
            [mertonon.test-utils :as tu]
            [mertonon.util.config :as mt-config]
            [mertonon.util.io :as uio]
            [mertonon.util.validations :as uval]))

;; ---
;; Tests for the act of validating
;; ---

(defn simple-dummy-validation [random-keyword]
  (fn [req] random-keyword))

(defn dummy-validation [random-keyword]
  (fn [req] {random-keyword []}))

(defn filled-dummy-validation [random-keyword random-member]
  (fn [req] {random-keyword [random-member]}))

(defspec two-validations-add-reses
  tu/middle
  (prop/for-all [curr-keyword gen/keyword
                 members      (gen/vector-distinct gen/string {:num-elements 2})]
                (let [dummy-1 (filled-dummy-validation curr-keyword (first members))
                      dummy-2 (filled-dummy-validation curr-keyword (second members))
                      ;; Makes sure that they're a vec, not a set
                      dummy-3 (filled-dummy-validation curr-keyword (first members))
                      resp    (uval/validate {} [dummy-1 dummy-2 dummy-3])]
                  (and
                    (seq resp)
                    (= (resp curr-keyword)
                       [(first members)
                        (second members)
                        (first members)])))))

(defspec mix-simple-and-nonsimple-validation
  tu/middle
  (prop/for-all [[fst-keyword snd-keyword] (gen/vector-distinct gen/keyword {:num-elements 2})]
                (let [dummy        (dummy-validation fst-keyword)
                      simple-dummy (simple-dummy-validation snd-keyword)
                      nil-dummy    (simple-dummy-validation nil)
                      resp         (uval/validate
                                      {}
                                      [dummy simple-dummy nil-dummy
                                       dummy simple-dummy nil-dummy])]
                  (and
                    (seq resp)
                    (= (->> resp keys count) 2)))))

(deftest nil-dummy-passes-through
  (let [nil-dummy (simple-dummy-validation nil)
        resp      (uval/validate {} [nil-dummy nil-dummy])]
    (is (= {} resp))))

(comment (run-tests))
