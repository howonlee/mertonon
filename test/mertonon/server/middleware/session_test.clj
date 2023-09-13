(ns mertonon.server.middleware.session-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.models.mt-session :as mt-session-model]
            [mertonon.server.handler :as handler]
            [mertonon.server.middleware.session :as mt-session-middleware]
            [mertonon.test-utils :as tu]
            [mertonon.util.io :as uio]))

(defn get-app! [curr-app session]
  (let [endpoint    "/api/v1/grid/"
        req         {:uri endpoint :request-method :get}
        req         (if (some? session)
                      (assoc-in req [:cookies "ring-session"] (:uuid session))
                      req)
        res         (curr-app req)
        slurped     (update res :body (comp uio/maybe-slurp uio/maybe-json-decode))]
    slurped))

(defspec endpoint-needs-session-test
  1
  (prop/for-all
    [generated authn-gen/generate-mt-sessions]
    (let [curr-app        (handler/app-handler)
          session!        ((mt-session-model/model :create-one!)
                           (first (:mt-sessions generated)))
          no-session-req! (get-app! curr-app nil)
          printo          (println no-session-req!)
          session-req!    (get-app! curr-app session!)
          printo          (println session-req!)]
      ;;;;;;
      ;;;;;;
      ;;;;;;
      ;;;;;;
      nil
      )))

(comment (run-tests))
