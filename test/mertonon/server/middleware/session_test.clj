(ns mertonon.server.middleware.session-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.server.handler :as handler]
            [mertonon.server.middleware.session :as mt-session-middleware]
            [mertonon.test-utils :as tu]))

;; TODO: property test

(deftest endpoint-needs-session
  (let [app-handler (handler/app-handler)]
    ;; grid getter
    ;; make the session
    ;; make the unsessioned request
    ;; make sure it fails
    ;; make a sessioned request
    ;; make sure it succeeds
    ))

(comment (run-tests))
