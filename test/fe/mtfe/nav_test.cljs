(ns mtfe.nav-test
  (:require [cljs.test :refer [deftest is]]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.net :as net-gen]))

(deftest a-test
  (is (= 1 2)))
