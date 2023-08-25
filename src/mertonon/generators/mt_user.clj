(ns mertonon.generators.mt-users
  "Generating mertonon users"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [loom.graph :as graph]
            [loom.alg :as graph-alg]
            [loom.attr :as graph-attr]
            [loom.alg-generic :as graph-alg-generic]))

(defn generate-mt-user*
  [{:keys [name-type label-type] :as params}]
  (gen/let [mt-user-uuid  gen/uuid
            mt-user-name  (gen-data/gen-user-names name-type)
            mt-user-label (gen-data/gen-labels label-type)]
    [(mtc/->MtUser mt-user-uuid
                   mt-user-name
                   mt-user-label)]))


