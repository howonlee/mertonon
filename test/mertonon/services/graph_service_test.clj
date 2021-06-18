(ns mertonon.services.graph-service-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.services.graph-service :as gs]
            [mertonon.generators.net :as net-gen]
            [mertonon.generators.aug-net :as aug-net-gen]))

;; (defspec net-graph-is-dag-test
;;   100
;;   nil)
;; 
;; (defspec if-both-initial-and-terminal-is-trivial-node-test
;;   100
;;   nil)
