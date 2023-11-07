(ns mtfe.events-test
  "Test events"
  (:require [clojure.data :as cd]
            [clojure.test :as ct :refer [deftest is run-tests]]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :as tct]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mtfe.generators.events :as event-gen]
            [mtfe.generators.net-store :as net-store]
            [mtfe.test-utils :as tu]
            [re-frame.core :refer [dispatch]]))

(deftest test-selection-exercise
  (is (= true
         ((tc/quick-check
            tu/many
            (prop/for-all [evt event-gen/gen-selection-event]
                          (dispatch evt))) :result))))

(deftest test-stack-push
  (is nil))
