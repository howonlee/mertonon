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
            [mertonon.server.middleware.validations :as val-mw]
            [mertonon.test-utils :as tu]
            [mertonon.util.config :as mt-config]
            [mertonon.util.io :as uio]))

(defn simple-dummy-validation [random-keyword]
  (fn [req] random-keyword))

(defn dummy-validation [random-keyword]
  (fn [req] {random-keyword []}))

(defn filled-dummy-validation [random-keyword random-member]
  (fn [req] {random-keyword [random-member]}))

(def dummy-handler (fn [req] {:status 200 :body "handled"}))

(defspec two-validations-add-reses
  20
  (prop/for-all [curr-keyword gen/keyword
                 members      (gen/vector-distinct gen/string {:num-elements 2})]
                (let [dummy-1 (filled-dummy-validation curr-keyword (first members))
                      dummy-2 (filled-dummy-validation curr-keyword (second members))
                      ;; Makes sure that they're a vec, not a set
                      dummy-3 (filled-dummy-validation curr-keyword (first members))
                      resp    ((val-mw/wrap-mertonon-validations
                                 dummy-handler
                                 [dummy-1 dummy-2 dummy-3]) {})]
                  (and
                    (= (:status resp) 400)
                    (= (get-in resp [:body curr-keyword])
                       [(first members)
                        (second members)
                        (first members)])))))


(defspec mix-simple-and-nonsimple-validation
  20
  (prop/for-all [[fst-keyword snd-keyword] (gen/vector-distinct gen/keyword {:num-elements 2})]
                (let [dummy        (dummy-validation fst-keyword)
                      simple-dummy (simple-dummy-validation snd-keyword)
                      nil-dummy    (simple-dummy-validation nil)
                      resp         ((val-mw/wrap-mertonon-validations
                                      dummy-handler
                                      ;; Double them to make sure idempotent
                                      [dummy
                                       simple-dummy
                                       nil-dummy
                                       dummy
                                       simple-dummy
                                       nil-dummy]) {})]
                  (and
                    (= (:status resp) 400)
                    (= (->> resp :body keys count) 2)))))

(deftest nil-dummy-passes-through
  (let [nil-dummy (simple-dummy-validation nil)
        resp      ((val-mw/wrap-mertonon-validations
                     dummy-handler
                     [nil-dummy nil-dummy]) {})]
    (is (= 200 (:status resp)))
    (is (= "handled" (:body resp)))))

(comment (run-tests))
