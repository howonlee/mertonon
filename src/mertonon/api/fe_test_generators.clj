(ns mertonon.api.fe-test-generators
  "API for generated data for purposes of frontend testing. Generated whenever API is called.
  Do not use for BE testing, use `mertonon.generators` stuff for that
  Do not use for demos, there's a `mertonon.api.generators` for that"
  (:require [clojure.core.matrix :as cm]
            [clojure.data.json :as json]
            [clojure.test.check.generators :as gen]
            [loom.graph :as graph]
            [mertonon.generators.net :as net-gen]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.services.grad-service :as grad-service]
            [mertonon.services.graph-service :as graph-service]
            [mertonon.services.matrix-service :as matrix-service]
            [mertonon.util.uuid :as uutils]))

(defn grid-get [_]
  (let [res (->> (gen/sample net-gen/generate-grid)
                 (mapv :grids))]
    {:status 200 :body (json/write-str res)}))

(def fe-generated-grid-endpoint {:get grid-get :name ::fe-generated-grid-endpoint})

(defn graph-get [_]
  (let [genned-nets (gen/sample net-gen/generate-dag-net)
        res         (vec (for [net genned-nets
                               :let [graph (graph-service/net->graph (:layers net) (:weightsets net))]]
                           {:nodes (graph/nodes graph) :edges (graph/edges graph) :weightsets (:weightsets net)}))]
    {:status 200 :body (json/write-str res)}))

(def fe-generated-graph-endpoint {:get graph-get :name ::fe-generated-graph-endpoint})

(defn loss-get [_]
  (let [res (->> (gen/sample net-gen/generate-dag-net)
                 (mapv #(select-keys % [:grids :losses])))]
    {:status 200 :body (json/write-str res)}))

(def fe-generated-loss-endpoint {:get loss-get :name ::fe-generated-loss-endpoint})

(defn fe-generator-routes []
  [["/grid" fe-generated-grid-endpoint]
   ["/graph" fe-generated-graph-endpoint]
   ["/loss" fe-generated-loss-endpoint]])
