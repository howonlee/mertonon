(ns mtfe.example-test
  (:require [cljs.test :refer [deftest is]]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.net :as net-gen]))

(deftest a-test
  (println (gen/generate net-gen/generate-dag-net))
  (is (= 1 2)))
