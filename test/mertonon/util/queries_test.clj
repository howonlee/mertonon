(ns mertonon.util.queries-test
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmo]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.queries :as query-gen]
            [mertonon.test-utils :as tu]
            [mertonon.util.queries :as queries]))

(defspec exercise-select-where-single-joined-query
  100
  (prop/for-all [joined-query query-gen/generate-single-joined-query]
                (tu/with-test-txn
                  (queries/select-where-joined joined-query))))

(defspec exercise-select-where-multi-joined-query
  100
  (prop/for-all [joined-query query-gen/generate-multi-joined-query]
                (tu/with-test-txn
                  (queries/select-where-joined joined-query))))

(comment (run-tests))
