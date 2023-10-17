(ns mertonon.models.model-test
  "One test suite to test all the models! Model selected at testing time randomly!"
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as reg]))

;; ---
;; Preliminaries
;; ---

(def tables-under-test
  "Order of these matters, with foreign key dependencies.
  Basically the linearized version of the DAG of them
  Better not have cycles in our fkey dependencies!"
  [:mertonon.grids :mertonon.layers :mertonon.cost-objects
   :mertonon.weightsets :mertonon.weights
   :mertonon.losses :mertonon.inputs :mertonon.entries

   :mertonon.mt-users])

(defn test-inp [table generates]
  (merge (reg/table->model table)
         {:gen-net         generates
          :model-instance  (tu/generates->member generates table)
          :model-instances (tu/generates->members generates table)
          :setup           (tu/setup-generates! tables-under-test)}))

(def table-and-generates (tu/table-and-generates tables-under-test))

;; ---
;; Actual tests
;; ---

(defspec model-instance-singular
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (not (vector? (:model-instance (test-inp table generates))))))

(defspec create-and-generate-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-generate-consonance (test-inp table generates)))))

(defspec member->row-round-trip
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/member->row-round-trip (test-inp table generates)))))

(defspec create-and-read-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-read-consonance (test-inp table generates)))))

(defspec create-one-create-many-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-one-create-many-consonance (test-inp table generates)))))

(defspec update-then-update-back
  tu/many
  (prop/for-all [[table generates] (tu/table-and-generates tables-under-test #{:mertonon.mt-users})]
                (tu/with-test-txn (tu/update-then-update-back (test-inp table generates)))))

(defspec update-many-then-update-back
  tu/middle
  (prop/for-all [[table generates] (tu/table-and-generates tables-under-test #{:mertonon.mt-users})]
                (tu/with-test-txn (tu/update-many-then-update-back (test-inp table generates)))))

(defspec read-one-read-many-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn
                  (tu/read-one-read-many-consonance (test-inp table generates)))))

(defspec read-one-read-where-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn
                  (tu/read-one-read-where-consonance (test-inp table generates)))))

(defspec create-and-delete-inversion
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/create-and-delete-inversion (test-inp table generates)))))

(defspec delete-one-delete-many-consonance
  tu/many
  (prop/for-all [[table generates] table-and-generates]
                (tu/with-test-txn (tu/delete-one-delete-many-consonance (test-inp table generates)))))

(comment (read-one-read-many-consonance))
