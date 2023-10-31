(ns mtfe.events-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mtfe.generators.events :as event-gen]))

(defspec weightset-matrix-encdec-test
  tu/many
  (prop/for-all [matrix-weights net-gen/generate-matrix-weights]
                (= matrix-weights (-> matrix-weights ms/weights->matrix ms/matrix->weights))))
