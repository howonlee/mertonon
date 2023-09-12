(ns mertonon.server.middleware.session-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.server.middleware.session :as mt-session-middleware]
            [mertonon.test-utils :as tu]))

(defspec endpoint-needs-session
  100
  (prop/for-all []
                (let [handler app-handler some crap]
                  ;; random non-login endpoint
                  ;; make the session
                  ;; make the request
                  ;; make sure it fails
                  ;; make a sessioned request
                  ;; make sure it succeeds
                  (request some crap))))
