(ns mtfe.events-test
  "Test events"
  (:require [clojure.data :as cd]
            [clojure.test :as ct :refer [deftest is]]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :as tct]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mtfe.generators.events :as event-gen]
            [mtfe.generators.net-store :as net-store]
            [mtfe.test-utils :as tu]
            [re-frame.core :refer [dispatch]]))

(ct/use-fixtures :once net-store/fill-store!)

(deftest selection-exercise-test
  (is (= true
         ((tc/quick-check
            tu/many
            (prop/for-all [evt event-gen/gen-selection-event]
                          (println evt))) :result))))