(ns mertonon.test-runner
  ;; no, this wont work, eftest is clojure only
  (:require [clojure.test :as ct]
            [eftest.runner :as eftest]))

(defn run-tests
  "Runs all the backend tests"
  [_]
  (eftest/run-tests
    (eftest/find-tests "test/mertonon")
    {
     ;; Look, the default one has pretty colors, the clojure.test one runs in time not-a-million-years
     :report ct/report
     ;; Why did the eftest peeps even make multithread the default...
     :multithread?   false
     ;; It's so long because we have lots of property tests
     :test-warn-time 5000}))
