(ns mertonon.models.net-model-test
  "One test suite to test all the network models! Model selected at testing time randomly!"
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as reg]))

(def tables-under-test
  "Order of these matters, with foreign key dependencies.
  Basically the linearized version of the DAG of them
  Better not have cycles in our fkey dependencies!"
  [:mertonon.grids :mertonon.layers :mertonon.weightsets
   :mertonon.cost-objects :mertonon.weights :mertonon.losses
   :mertonon.inputs :mertonon.entries])

(def gen-nets
  ;; If not at least 2, update test won't be able to test things
  (gen/vector aug-net-gen/net-enriched-with-entries 2 4))

(defn setup! [net]
  (let [all-members (for [table tables-under-test]
                      [table (tu/net->members net table)])
        ;; `vec` to engender the side effect
        insert-all! (vec (for [[table members] all-members]
                      (((reg/table->model table) :create-many!) (flatten [members]))))]
    nil))

(defn test-inp [net table]
  (merge (reg/table->model table)
         {:gen-net             net
          :model-instance      (tu/net->member net table)
          :model-instances     (tu/net->members net table)
          :setup               setup!}))

(defspec model-instance-singular
  100
  (prop/for-all [net   aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (not (vector? (:model-instance (test-inp net table))))))

(defspec create-and-generate-consonance
  100
  (prop/for-all [net   aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-and-generate-consonance (test-inp net table)))))

(defspec member->row-round-trip
  100
  (prop/for-all [net aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/member->row-round-trip (test-inp net table)))))

(defspec create-and-read-consonance
  100
  (prop/for-all [net aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-and-read-consonance (test-inp net table)))))

(defspec create-one-create-many-consonance
  100
  (prop/for-all [nets gen-nets
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-one-create-many-consonance (test-inp nets table)))))

(defspec update-then-update-back
  100
  (prop/for-all [nets gen-nets
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/update-then-update-back (test-inp nets table)))))

(defspec read-one-read-many-consonance
  100
  (prop/for-all [nets gen-nets
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn
                  (tu/read-one-read-many-consonance (test-inp nets table)))))

(defspec read-one-read-where-consonance
  100
  (prop/for-all [nets gen-nets
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn
                  (tu/read-one-read-where-consonance (test-inp nets table)))))

(defspec create-and-delete-inversion
  100
  (prop/for-all [net aug-net-gen/net-enriched-with-entries
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-and-delete-inversion (test-inp net table)))))

(defspec delete-one-delete-many-consonance
  100
  (prop/for-all [nets gen-nets
                 table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/delete-one-delete-many-consonance (test-inp nets table)))))

(comment (run-tests))
