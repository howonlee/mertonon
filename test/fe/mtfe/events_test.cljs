(ns mtfe.events-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mtfe.generators.events :as event-gen]
            [mtfe.test-utils :as tu]
            [re-frame :refer [dispatch]]))

(defspec selection-exercise-test
  tu/many
  (prop/for-all [evt event-gen/gen-selection-event]
                (dispatch evt)))
