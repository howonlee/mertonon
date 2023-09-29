(ns mtfe.generators.db
  "Generate FE state"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]))

(def generate-state nil)
  
