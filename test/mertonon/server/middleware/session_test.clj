(ns mertonon.server.middleware.session-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.models.mt-session :as mt-session-model]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.server.handler :as handler]
            [mertonon.server.middleware.session :as mt-session-middleware]
            [mertonon.test-utils :as tu]
            [mertonon.util.config :as mt-config]
            [mertonon.util.io :as uio]))

(defn get-app! [curr-app session]
  (let [endpoint    "/api/v1/grid/"
        req         {:uri endpoint :request-method :get}
        req         (if (seq session)
                      (assoc-in req [:cookies "ring-session" :value] (str (:uuid session)))
                      req)
        res         (curr-app req)
        slurped     (update res :body (comp uio/maybe-slurp uio/maybe-json-decode))]
    slurped))

(defspec endpoint-needs-session-test
  1
  (prop/for-all
    [generated authn-gen/generate-mt-sessions]
    (tu/with-test-txn
    (let [curr-app        (handler/app-handler)
          curr-user       (first (:mt-users generated))
          curr-session    (first (:mt-sessions generated))
          user!           ((mt-user-model/model :create-one!) curr-user)
          session!        ((mt-session-model/model :create-one!) curr-session)
          no-session-req! (get-app! curr-app nil)
          session-req!    (get-app! curr-app curr-session)]
      (and
        (= (no-session-req! :status) 401)
        (= (session-req! :status) 200)
        (vector? (session-req! :body)))))))

(comment (run-tests))
