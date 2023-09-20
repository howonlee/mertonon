(ns mertonon.api.intro-tests
  "Intro tests"
  (:require [clojure.data :as cd]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.walk :as walk]
            [mertonon.api.api-tests :as api-tests]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.generators.data :as gen-data]
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
    [usernames  (gen/vector-distinct-by str/lower-case (gen-data/gen-mt-user-usernames :line-noise) {:num-elements 2})
     emails     (gen/vector-distinct (gen-data/gen-mt-user-emails :line-noise) {:num-elements 2})
     passwords  (gen/vector-distinct (gen-data/gen-passwords :line-noise) {:num-elements 2})]
    (tu/with-test-txn
      (let [[fst-user snd-user]   usernames
            [fst-email snd-email] emails
            [fst-pass snd-pass]   passwords
            curr-app      (handler/test-handler)
            fst-body      {:username fst-user :password fst-pass :email fst-email}
            snd-body      {:username snd-user :password snd-pass :email snd-email}

            ;; all-users     ((mt-user-model/model :read-all))
            ;; deletion!     ((mt-user-model/model :hard-delete-many!) (mapv :uuid all-users))
            curr-count    ((mt-user-model/model :count))
            printo        (println "=====")
            printo        (println curr-count)
            ;;;; get the app handler with some kind of with-test-transaction thing.
            ;;;; print out current state fuckery
            fst-intro!    (post-intro! fst-body curr-app)
            snd-intro!    (post-intro! snd-body curr-app)
            printo        (println "=====")
            printo        (println fst-intro!)
            printo        (println snd-intro!)]
        (and (= 200 (:status fst-intro!))
             (= fst-user (-> fst-intro! :body :mt-user :username))
             (nil? (-> fst-intro! :body :password))
             (= 400 (:status snd-intro!)))))))

(comment (run-tests))

(comment (let [all-users     ((mt-user-model/model :read-all))
               deletion!     ((mt-user-model/model :hard-delete-many!) (mapv :uuid all-users))]
           ((mt-user-model/model :count))))
