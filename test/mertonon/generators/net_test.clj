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
  100
  (prop/for-all [curr-net (gen/one-of [net-gen/generate-simple-net
                                       net-gen/generate-linear-net
                                       net-gen/generate-dag-net])
                 ;; no weights because they're partitioned
                 table    (gen/elements (remove #{:mertonon.weights} registry/net-tables))]
                (= (mapv :uuid (curr-net (tu/maybe-strip-schema table)))
                   (mapv :uuid (sort-by :uuid (curr-net (tu/maybe-strip-schema table)))))))

(defspec nets-have-nontrivial-tables
  100
  (prop/for-all [curr-net (gen/one-of [net-gen/generate-simple-net
                                       net-gen/generate-linear-net
                                       net-gen/generate-dag-net])
                 table    (gen/elements (remove #{:mertonon.entries} registry/net-tables))]
                (and (vector? (curr-net (tu/maybe-strip-schema table)))
                     (> (count (curr-net (tu/maybe-strip-schema table))) 0))))

(defspec different-arity-group-by-dependent-uuid-agree
  100
  (prop/for-all [fk-uuids  (gen/vector gen/uuid 4)
                 pk-uuids  (gen/vector gen/uuid 12)
                 some-vals (gen/vector gen/small-integer 12)]
                (let [first-constructor  (fn [fk-uuid pk-uuid] {:uuid pk-uuid :snd-uuid fk-uuid})
                      second-constructor (fn [fk-uuid pk-uuid some-val] {:uuid pk-uuid :snd-uuid fk-uuid :some-val some-val})
                      partitioned-uuids  (partition 4 pk-uuids)
                      partitioned-vals   (partition 4 some-vals)
                      arity-filter       #(dissoc % :some-val)
                      first-res          (net-gen/group-by-dependent-uuid
                                           first-constructor
                                           fk-uuids
                                           partitioned-uuids)
                      second-res         (mapv arity-filter (net-gen/group-by-dependent-uuid
                                                              second-constructor
                                                              fk-uuids
                                                              partitioned-uuids
                                                              partitioned-vals))]
                  (= first-res second-res))))

;; (defspec no-weights-with-duplicate-src-tgt-cobjs
;;   ;; for simple linear and dag
;;   100
;;   nil)

(comment (run-tests))
