(ns mertonon.test-runner
  (:require [clojure.test :as ct]
            [eftest.runner :as eftest]))

(defn run-tests
  "Runs all the backend tests"
  [_]
  (eftest/run-tests
    (eftest/find-tests "test/mertonon")
    {
     ;; Why did the eftest peeps even make multithread the default...
     :multithread?   false
     ;; It's so long because we have lots of property tests
     :test-warn-time 5000}))
