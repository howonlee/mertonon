(ns mertonon.models.other-model-test
  "One test suite to test all the other models! Model selected at testing time randomly!"
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.mt-user :as mt-user-gen]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as reg]))

(def tables-under-test
  [:mertonon.mt-users])

(def tables->generates
  {:mertonon.mt-users mt-user-gen/generate-mt-users})

(defn setup! [generates]
  (let [all-members (for [table tables-under-test]
                      [table (get generates table)])
        insert-all! (doall
                      (for [[table members] all-members]
                        (((reg/table->model table) :create-many!) (flatten [members]))))]
    nil))

(defn test-inp [table generates]
  (let [curr-gens (get generates table)]
    (merge (reg/table->model table)
           {:gen-net             net
            :model-instance      (first curr-gens)
            :model-instances     curr-gens
            :setup               setup!})))

(defspec create-and-generate-consonance
  100
  (prop/for-all [table     (gen/elements tables-under-test)
                 generates some crap]
                (tu/with-test-txn (tu/create-and-generate-consonance (test-inp table generates)))))

(defspec member->row-round-trip
  100
  (prop/for-all [table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/member->row-round-trip (test-inp table generates)))))

(defspec create-and-read-consonance
  100
  (prop/for-all [table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-and-read-consonance (test-inp table generates)))))

(defspec create-one-create-many-consonance
  100
  (prop/for-all [table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-one-create-many-consonance (test-inp table generates)))))

(defspec update-then-update-back
  100
  (prop/for-all [table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/update-then-update-back (test-inp table generates)))))

(defspec read-one-read-many-consonance
  100
  (prop/for-all [table (gen/elements tables-under-test)]
                (tu/with-test-txn
                  (tu/read-one-read-many-consonance (test-inp table generates)))))

(defspec read-one-read-where-consonance
  100
  (prop/for-all [table (gen/elements tables-under-test)]
                (tu/with-test-txn
                  (tu/read-one-read-where-consonance (test-inp table generates)))))

(defspec create-and-delete-inversion
  100
  (prop/for-all [table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/create-and-delete-inversion (test-inp table generates)))))

(defspec delete-one-delete-many-consonance
  100
  (prop/for-all [table (gen/elements tables-under-test)]
                (tu/with-test-txn (tu/delete-one-delete-many-consonance (test-inp table generates)))))
