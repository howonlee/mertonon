(ns mertonon.api.intro-tests
  "Intro tests"
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.walk :as walk]
            [mertonon.api.api-tests :as api-tests]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.server.handler :as handler]
            [mertonon.test-utils :as tu]
            [mertonon.util.io :as uio]))

(defn post-intro! [member curr-app]
  (let [endpoint    "/api/v1/intro/"
        res         (curr-app {:uri endpoint :request-method :post :body-params member})
        slurped     (update res :body (comp uio/maybe-slurp uio/maybe-json-decode))]
    slurped))

(defspec intro-not-idempotent
  1
  (prop/for-all
    [generates authn-gen/generate-password-logins]
    (tu/with-test-txn
      (let [fst-generated (first (:mt-users generates))
            snd-generated (second (:mt-users generates))
            all-users     ((mt-user-model/model :read-all))
            deletion!     ((mt-user-model/model :hard-delete-many!) (mapv :uuid all-users))
            curr-app      (handler/app-handler)
            ;;;; actually conform to endpoint
            ;;;; actually conform to endpoint
            ;;;; actually conform to endpoint
            ;;;; actually conform to endpoint
            fst-intro!    (post-intro! fst-generated curr-app)
            snd-intro!    (post-intro! snd-generated curr-app)]
        (and (= 200 (:status fst-intro!))
             (= 400 (:status snd-intro!)))))))

(comment (run-tests))
