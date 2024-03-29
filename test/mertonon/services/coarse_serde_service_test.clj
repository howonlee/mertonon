(ns mertonon.services.coarse-serde-service-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.net :as net-gen]
            [mertonon.services.coarse-serde-service :as coarse-serde]
            [mertonon.test-utils :as tu]))

;; Note: no guarantee of ordering in serde. Also only weights, but that's good enough for now
;; TODO: Non-weights
;; TODO: Guarantee of ordering in serde

(defspec coarse-serde-test
  tu/many
  (prop/for-all [net net-gen/generate-dag-net]
                (let [grid-uuid (-> net :grids first :uuid)]
                  (tu/with-test-txn
                    (= (->> net :weights (mapv :uuid) set)
                       (->>
                         (do (coarse-serde/net->db net)
                             (coarse-serde/db->net grid-uuid))
                         :weights (mapv :uuid) set))))))

(comment (run-tests))
