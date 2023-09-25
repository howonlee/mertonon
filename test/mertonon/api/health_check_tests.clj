(ns mertonon.api.health-check-tests
  "Health check only does one thing, which does change state. Check it does that one thing."
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.walk :as walk]
            [mertonon.api.api-tests :as api-tests]
            [mertonon.generators.health-check :as hc-gen]
            [mertonon.models.health-check :as hc-model]
            [mertonon.server.handler :as handler]
            [mertonon.util.db :as db]
            [mertonon.test-utils :as tu]))

(defn post-to-health-check! [member curr-txn]
  (let [app         (tu/app-with-test-txn curr-txn)
        endpoint    "/api/v1/health_check/"
        row->member (hc-model/model :row->member)
        res         (app {:uri endpoint :request-method :post :body-params member})
        processed   (api-tests/process-app-response res)]
    (row->member processed)))

;; Exercising only
(defspec just-post-a-bunch
  100
  (prop/for-all [hc (hc-gen/generate-health-check)]
                (tu/with-test-txn
                  (some? (:uuid (post-to-health-check! hc db/*defined-connection*))))))

(comment (run-tests))
