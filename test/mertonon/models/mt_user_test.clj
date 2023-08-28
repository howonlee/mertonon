(ns mertonon.models.mt-user-test
  "Specific mt user models"
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.generators.mt-user :as mt-user-gen]
            [mertonon.generators.net :as net-gen]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as reg]))

;; ---
;; User tests
;; ---

(defspec hit-the-unique-constraint
  100
  (prop/for-all [user-vec (gen/vector mt-user-gen/generate-mt-user 2)]
                (let [same-username-users (vec (for [member user-vec]
                                                 (assoc member
                                                        :username (->> user-vec first :username)
                                                        :canonical_username (->> user-vec first :username some canonicalization))))]
                  (tu/with-test-txn
                    (some crap)))))
                  ;; (t/is (t/thrown? some crap)))))

;; ---
;; Password login tests
;; ---

(comment (run-tests))
