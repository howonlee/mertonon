(ns mertonon.models.mt-user-test
  "Specific mt user models"
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.generators.net :as net-gen]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.test-utils :as tu]
            [mertonon.util.registry :as reg]))

;; ---
;; User tests
;; ---

(defspec hit-the-unique-constraint
  100
  (prop/for-all [users-gen authn-gen/generate-mt-users]
                (let [user-vec
                      (:mt-users users-gen)
                      same-username-users
                      (vec (for [user user-vec]
                             (assoc user
                                    :username (->> user-vec first :username)
                                    :canonical-username (->> user-vec
                                                             first
                                                             mt-user-model/canonicalize-username
                                                             :canonical-username))))]
                  (tu/expect-thrown
                    {:curr-fn (fn [] (tu/with-test-txn
                                       ((mt-user-model/model :create-many!) same-username-users)))
                     :checker (fn [e] (= org.postgresql.util.PSQLException (type e)))}))))

;; ---
;; Password login tests
;; ---

(defspec password-is-digest
  ;; The problem with running scrypt in property tests is that scrypt is designed to be slow
  3
  (prop/for-all [password-login-gen authn-gen/generate-password-logins]
                (let [{password-logins :password-logins} password-login-gen]
                  (every? password-login-model/is-digest? password-logins))))

(comment (run-tests))
