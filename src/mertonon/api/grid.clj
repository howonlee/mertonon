(ns mertonon.api.grid
  "API for grids"
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [loom.graph :as graph]
            [mertonon.api.util :as api-util]
            [mertonon.models.entry :as entry-model]
            [mertonon.models.grid :as grid-model]
            [mertonon.models.input :as input-model]
            [mertonon.models.layer :as layer-model]
            [mertonon.models.loss :as loss-model]
            [mertonon.models.weightset :as weightset-model]

            [mertonon.services.coarse-serde-service :as coarse-serde-service]
            [mertonon.services.grad-service :as grad-service]
            [mertonon.services.graph-service :as graph-service]
            [mertonon.services.matrix-service :as matrix-service]
            [mertonon.util.io :as uio]
            [mertonon.util.registry :as registry]
            [mertonon.util.uuid :as uutils]
            [tick.core :as t]))

(defn single-endpoint []
  {:get    (api-util/get-model grid-model/model)
   :delete (api-util/delete-model grid-model/model)
   :name   ::grid})

(defn mass-endpoint []
  {:get    (api-util/get-models grid-model/model)
   :post   (api-util/create-model grid-model/model)
   :delete (api-util/delete-models grid-model/model)
   :name   ::grids})

(defn grad-create!
  "This one mutates all the reference cobjs and weights to put in deltas and grads"
  [match]
  (let [body-params                 (-> match :body-params)
        start-date                  (-> body-params :start-date t/instant)
        end-date                    (-> body-params :end-date t/instant)
        grid-uuid                   (-> body-params :grid-uuid uutils/uuid)

        entry-res                   ((grid-model/model :read-where-joined)
                                     {:join-tables      [:mertonon.layer :mertonon.cost_object :mertonon.entry]
                                      :join-col-edges   [[:mertonon.grid.uuid :mertonon.layer.grid_uuid]
                                                         [:mertonon.layer.uuid :mertonon.cost_object.layer_uuid]
                                                         [:mertonon.cost_object.uuid :mertonon.entry.cobj_uuid]]
                                      :where-clause     [:and
                                                         [:> :mertonon.entry.entry-date start-date]
                                                         [:< :mertonon.entry.entry-date end-date]
                                                         [:= :mertonon.grid.uuid grid-uuid]]
                                      :raw-table->table registry/raw-table->table
                                      :table->model     registry/table->model})
        entries                     (entry-res :mertonon.entries)
        net                         (coarse-serde-service/db->net grid-uuid)
        matrix-net                  (matrix-service/row-net->matrix-net net)
        {cost-objects :cost-objects
         layers       :layers
         weightsets   :weightsets}  matrix-net
        graph                       (graph-service/net->graph layers weightsets)
        initial-uuids               (graph-service/initial-layer-uuids graph)
        terminal-uuids              (graph-service/terminal-layer-uuids graph)
        patterns                    (matrix-service/entries->patterns
                                      {:entries              (sort-by :cobj-uuid entries)
                                       :pattern-layer-uuids  (into initial-uuids terminal-uuids)
                                       :cost-objects         cost-objects})
        forward-res                 (grad-service/net-patterns->forward-pass matrix-net patterns)
        grad                        (grad-service/forward-pass->grad forward-res)
        to-update                   (grad-service/grad->to-update grad)
        _                           (grad-service/update-grad-fields! to-update)]
    {:status 200 :body (json/write-str (merge grad to-update))}))

(defn grad-endpoint []
  {:post grad-create! :name ::grad-create})

(defn grid-dump-get
  "Basically-completely denormalized view of whole net"
  [match]
  (let [params      (-> match :query-params walk/keywordize-keys)
        start-date  (-> params :start-date t/instant)
        end-date    (-> params :end-date t/instant)
        grid-uuid   (-> match :path-params :uuid uutils/uuid)
        entry-res   ((grid-model/model :read-where-joined)
                     {:join-tables      [:mertonon.layer :mertonon.cost_object :mertonon.entry]
                      :join-col-edges   [[:mertonon.grid.uuid :mertonon.layer.grid_uuid]
                                         [:mertonon.layer.uuid :mertonon.cost_object.layer_uuid]
                                         [:mertonon.cost_object.uuid :mertonon.entry.cobj_uuid]]
                      :where-clause     [:and
                                         [:> :mertonon.entry.entry-date start-date]
                                         [:< :mertonon.entry.entry-date end-date]
                                         [:= :mertonon.grid.uuid grid-uuid]]
                      :raw-table->table registry/raw-table->table
                      :table->model     registry/table->model})
        entry-res   (->> entry-res :mertonon.entries (sort-by :uuid))
        net-res     (coarse-serde-service/db->net grid-uuid)]
    {:status 200 :body (json/write-str (assoc net-res
                                              :entries entry-res
                                              :query params))}))

(defn dump-endpoint []
  {:get grid-dump-get :name ::grad-dump})

(defn grid-graph-get
  "Denormalized view of grid"
  [match]
  (let [grid-uuid    (-> match :path-params :uuid uutils/uuid)
        grid         ((grid-model/model :read-one) grid-uuid)
        layers       ((layer-model/model :read-where) [:= :grid-uuid grid-uuid])
        weightsets   (if (empty? layers) [] ((weightset-model/model :read-where) [:in :tgt-layer-uuid (mapv :uuid layers)]))
        weightsets   (-> weightsets dedupe vec)
        net-graph    (graph-service/net->graph layers weightsets)
        nodes        (graph/nodes net-graph)
        edges        (graph/edges net-graph)]
    {:status 200 :body (json/write-str {:grids      [grid]
                                        :nodes      nodes
                                        :layers     layers
                                        :edges      edges
                                        :weightsets weightsets})}))

(defn graph-endpoint []
  {:get grid-graph-get :name ::grid-graph})

(defn grid-view-get [match]
  (let [grid-uuid    (-> match :path-params :uuid uutils/uuid)
        grid         ((grid-model/model :read-one) grid-uuid)
        layers       ((layer-model/model :read-where) [:= :grid-uuid grid-uuid])
        inputs       (if (empty? layers) []
                       ((input-model/model :read-where) [:in :layer-uuid (mapv :uuid layers)]))
        losses       (if (empty? layers) []
                       ((loss-model/model :read-where) [:in :layer-uuid (mapv :uuid layers)]))]
    {:status 200 :body (json/write-str {:grids  [grid]
                                        :losses (sort-by :uuid losses)
                                        :inputs (sort-by :uuid inputs)})}))

(defn view-endpoint []
  {:get grid-view-get :name ::grid-view})

(defn routes []
  [["/" (mass-endpoint)]
   ["/_/grad" (grad-endpoint)]
   ["/:uuid" (single-endpoint)]
   ["/:uuid/dump" (dump-endpoint)]
   ["/:uuid/graph" (graph-endpoint)]
   ["/:uuid/view" (view-endpoint)]])
