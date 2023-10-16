(ns mertonon.generators.net-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.net :as net-gen]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as registry]))

(defspec nets-have-sorted-uuid-pkeys
  tu/many
  (prop/for-all [curr-net (gen/one-of [net-gen/generate-simple-net
                                       net-gen/generate-linear-net
                                       net-gen/generate-dag-net])
                 ;; no weights because they're partitioned
                 table    (gen/elements (remove #{:mertonon.weights} registry/net-tables))]
                (= (mapv :uuid (curr-net (tu/maybe-strip-schema table)))
                   (mapv :uuid (sort-by :uuid (curr-net (tu/maybe-strip-schema table)))))))

(defspec nets-have-nontrivial-tables
  tu/many
  (prop/for-all [curr-net (gen/one-of [net-gen/generate-simple-net
                                       net-gen/generate-linear-net
                                       net-gen/generate-dag-net])
                 table    (gen/elements (remove #{:mertonon.entries} registry/net-tables))]
                (and (vector? (curr-net (tu/maybe-strip-schema table)))
                     (> (count (curr-net (tu/maybe-strip-schema table))) 0))))

(comment (run-tests))
