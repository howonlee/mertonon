(ns mertonon.api.generator-consonance-tests
  "Generator API has a bunch of generated API endpoints which are denormalized.
  We always want formal consonance between the generated API denormalized endpoints and
  the denormalized endpoints strewn about the rest of the API. These tests enforce the consonance.

  There are always trouble spots in testing regimes. This is one of them, as of August 2023.
  The reason why can be intuited pretty straightforwardly when you look at the tests."
  (:require [clojure.data :as cd]
            [clojure.core.matrix :as cm]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.walk :as walk]
            [mertonon.api.generators :as gen-api]
            [mertonon.api.grid :as grid-api]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.generators.grad-net :as grad-net-gen]
            [mertonon.models.entry :as entry-model]
            [mertonon.server.handler :as app-handler]
            [mertonon.services.coarse-serde-service :as coarse-serde]
            [mertonon.test-utils :as tu]
            [mertonon.util.io :as uio]
            [tick.core :as t]))

(defn first-uuid [net table]
  (->> net table first :uuid))

(defn strip-updated-at
  "We mutate these update things, changing the updated-at times, so we strip these"
  [form]
  (walk/postwalk #(if (map? %) (dissoc % "updated-at") %) form))

(defn app-get [app endpoint & [query-params]]
  (let [req {:uri endpoint
             :request-method :get}
        req (if (some? query-params)
              (assoc req :query-params query-params)
              req)]
    (->> (app req)
         :body uio/maybe-slurp uio/maybe-json-decode strip-updated-at)))

(defn app-post [app endpoint data]
  (->> (app {:uri            endpoint
             :request-method :post
             :body-params    data})
       :body uio/maybe-slurp uio/maybe-json-decode strip-updated-at))

(def test-app (app-handler/app-handler))

(defspec grid-dump-consonance-test
  3
  (prop/for-all [net-and-backprop-and-updates (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (tu/with-test-txn
                  (do
                    (gen-api/set-generated-net-atom! net-and-backprop-and-updates)
                    (coarse-serde/net->db (first net-and-backprop-and-updates))
                    (let [[net patterns & _]   net-and-backprop-and-updates
                          grid-uuid            (->> net :grids first :uuid)
                          _                    ((entry-model/model :create-many!) (:entries patterns))
                          entry-date           (-> (:cobj->entries patterns) first second first :entry-date)
                          before               (str (t/<< entry-date (t/new-duration 100 :minutes)))
                          after                (str (t/>> entry-date (t/new-duration 100 :minutes)))

                          dump-endpoint        (format "/api/v1/grid/%s/dump" grid-uuid)
                          query-data           {:start-date before :end-date after :grid-uuid grid-uuid}
                          demo-dump-endpoint   "/api/v1/generators/dump"
                          demo-dump-res        (app-get test-app demo-dump-endpoint)
                          dump-res             (dissoc (app-get test-app dump-endpoint query-data) "query")]
                      (= dump-res demo-dump-res))))))

(defspec grid-graph-consonance-test
  3
  (prop/for-all [net-and-backprop-and-updates (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (tu/with-test-txn
                  (do
                    (gen-api/set-generated-net-atom! net-and-backprop-and-updates)
                    (coarse-serde/net->db (first net-and-backprop-and-updates))
                    (let [[net & _]          net-and-backprop-and-updates
                          grid-endpoint      (format "/api/v1/grid/%s/graph" (str (first-uuid net :grids)))
                          grid-res           (app-get test-app grid-endpoint)
                          demo-grid-endpoint "/api/v1/generators/graph"
                          demo-grid-res      (app-get test-app demo-grid-endpoint)]
                      (= grid-res demo-grid-res))))))

(defspec grid-view-consonance-test
  3
  (prop/for-all [net-and-backprop-and-updates (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (tu/with-test-txn
                  (do
                    (gen-api/set-generated-net-atom! net-and-backprop-and-updates)
                    (coarse-serde/net->db (first net-and-backprop-and-updates))
                    (let [[net & _]          net-and-backprop-and-updates
                          grid-endpoint      (format "/api/v1/grid/%s/view" (str (first-uuid net :grids)))
                          grid-res           (app-get test-app grid-endpoint)
                          demo-grid-endpoint "/api/v1/generators/grids"
                          demo-grid-res      (app-get test-app demo-grid-endpoint)]
                      (= grid-res demo-grid-res))))))

(defspec layer-consonance-test
  ;; Layer-entry patterns will not be consonant, because the generator takes from all the entries every time
  ;; TODO: Make layer-entry patterns consonant
  3
  (prop/for-all [net-and-backprop-and-updates (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (tu/with-test-txn
                  (do
                    (gen-api/set-generated-net-atom! net-and-backprop-and-updates)
                    (coarse-serde/net->db (first net-and-backprop-and-updates))
                    ;; cobj index
                    (coarse-serde/cobj-changes->db (nth net-and-backprop-and-updates 4))
                    (let [[net & _]           net-and-backprop-and-updates
                          layer-endpoint      (format "/api/v1/layer/%s/view" (str (first-uuid net :layers)))
                          layer-res           (dissoc (app-get test-app layer-endpoint) "patterns")
                          demo-layer-endpoint (format "/api/v1/generators/layers/%s" (str (first-uuid net :layers)))
                          demo-layer-res      (dissoc (app-get test-app demo-layer-endpoint) "patterns")]
                      (= layer-res demo-layer-res))))))

(defspec weightset-consonance-test
  3
  (prop/for-all [net-and-backprop-and-updates (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (tu/with-test-txn
                  (do
                    (gen-api/set-generated-net-atom! net-and-backprop-and-updates)
                    (coarse-serde/net->db (first net-and-backprop-and-updates))
                    ;; weight idx
                    (coarse-serde/weight-changes->db (nth net-and-backprop-and-updates 5))
                    (let [[net & _]               net-and-backprop-and-updates
                          weightset-endpoint      (format "/api/v1/weightset/%s/view" (str (first-uuid net :weightsets)))
                          weightset-res           (app-get test-app weightset-endpoint)
                          demo-weightset-endpoint (format "/api/v1/generators/weightsets/%s" (str (first-uuid net :weightsets)))
                          demo-weightset-res      (app-get test-app demo-weightset-endpoint)]
                      (= weightset-res demo-weightset-res))))))

(defspec cobj-consonance-test
  3
  (prop/for-all [net-and-backprop-and-updates (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (tu/with-test-txn
                  (do
                    (gen-api/set-generated-net-atom! net-and-backprop-and-updates)
                    (coarse-serde/net->db (first net-and-backprop-and-updates))
                    ;; cobj index
                    (coarse-serde/cobj-changes->db (nth net-and-backprop-and-updates 4))
                    (let [[net patterns & _]        net-and-backprop-and-updates
                          _                         ((entry-model/model :create-many!) (:entries patterns))
                          cost-object-endpoint      (format "/api/v1/cost_object/%s/view" (str (first-uuid net :cost-objects)))
                          cost-object-res           (app-get test-app cost-object-endpoint)
                          demo-cost-object-endpoint (format "/api/v1/generators/cost_objects/%s" (str (first-uuid net :cost-objects)))
                          demo-cost-object-res      (app-get test-app demo-cost-object-endpoint)]
                      (= cost-object-res demo-cost-object-res))))))

(defspec weight-consonance-test
  3
  (prop/for-all [net-and-backprop-and-updates (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (tu/with-test-txn
                  (do
                    (gen-api/set-generated-net-atom! net-and-backprop-and-updates)
                    (coarse-serde/net->db (first net-and-backprop-and-updates))
                    ;; weight idx
                    (coarse-serde/weight-changes->db (nth net-and-backprop-and-updates 5))
                    (let [[net & _]            net-and-backprop-and-updates
                          weight-endpoint      (format "/api/v1/weight/%s/view" (str (first-uuid net :weights)))
                          weight-res           (app-get test-app weight-endpoint)
                          demo-weight-endpoint (format "/api/v1/generators/weights/%s" (str (first-uuid net :weights)))
                          demo-weight-res      (app-get test-app demo-weight-endpoint)]
                      (= weight-res demo-weight-res))))))

;; TODO: losses

(defspec grad-consonance-test
  3
  (prop/for-all [net-and-backprop-and-updates (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (tu/with-test-txn
                  (do
                    (gen-api/set-generated-net-atom! net-and-backprop-and-updates)
                    (coarse-serde/net->db (first net-and-backprop-and-updates))
                    (uio/load-array-impl!)
                    (let [[net patterns & _]   net-and-backprop-and-updates
                          grid-uuid            (->> net :grids first :uuid)
                          _                    ((entry-model/model :create-many!) (:entries patterns))

                          entry-date           (-> (:cobj->entries patterns) first second first :entry-date)
                          before               (str (t/<< entry-date (t/new-duration 100 :minutes)))
                          after                (str (t/>> entry-date (t/new-duration 100 :minutes)))
                          grad-endpoint        "/api/v1/grid/_/grad"
                          post-data            {:start-date before :end-date after :grid-uuid grid-uuid}
                          demo-grad-endpoint   "/api/v1/generators/grad"
                          demo-grad-res        (app-get test-app demo-grad-endpoint)
                          ;; Vars are generated a la minute, therefore summarize grads to compare them
                          demo-grad-rollup     (->> (demo-grad-res "grads") vals (map cm/esum) sort)
                          grad-res             (app-post test-app grad-endpoint post-data)
                          grad-rollup          (->> (grad-res "grads") vals (map cm/esum) sort)]
                      (= grad-rollup demo-grad-rollup))))))

(comment
  (run-tests))
