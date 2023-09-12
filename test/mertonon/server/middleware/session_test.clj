(ns mertonon.server.middleware.session-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.server.middleware.session :as mt-session-middleware]
            [mertonon.test-utils :as tu]))

(defspec random thing needs session
  100
  (prop/for-all [some crap]
                (let [some crap]
                  (= some crap))))

(deftest login-is-exempted
  nil)
