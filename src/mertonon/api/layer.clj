(ns mertonon.api.layer
  "API for layers"
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.layer :as layer-model]
            [mertonon.models.weightset :as weightset-model]
            [mertonon.models.cost-object :as cost-object-model]
            [mertonon.models.entry :as entry-model]
            [mertonon.services.matrix-service :as matrix-service]
            [mertonon.util.uuid :as uutils]))

(defn single-endpoint []
  {:get    (api-util/get-model layer-model/model)
   :put    (api-util/update-model layer-model/model)
   :delete (api-util/delete-model layer-model/model)
   :name   ::layer})

(defn mass-endpoint []
  {:get    (api-util/get-models layer-model/model)
   :post   (api-util/create-model layer-model/model)
   :put    (api-util/update-model layer-model/model)
   :delete (api-util/delete-models layer-model/model)
   :name   ::layers})

(defn layer-view-get
  "Denormalized view of layer"
  [match]
  (let [layer-uuid         (api-util/path-uuid match)
        layer              ((layer-model/model :read-one) layer-uuid)
        ;; src-weightsets: weightsets which are upstream from layer
        src-weightsets     ((weightset-model/model :read-where) [:= :tgt-layer-uuid layer-uuid])
        ;; tgt-weightsets: weightsets which are downstream from layer
        tgt-weightsets     ((weightset-model/model :read-where) [:= :src-layer-uuid layer-uuid])
        cost-objects       (sort-by :uuid ((cost-object-model/model :read-where) [:= :layer-uuid layer-uuid]))
        entries            (if (empty? cost-objects) [] ((entry-model/model :read-where) [:in :cobj-uuid (mapv :uuid cost-objects)]))
        body-res           {:layer          layer
                            :src-weightsets src-weightsets
                            :tgt-weightsets tgt-weightsets
                            :cost-objects   cost-objects}]
     {:status 200 :body body-res}))

(defn view-endpoint []
  {:get layer-view-get :name ::layer-view})

(defn routes []
  [["/" (mass-endpoint)]
   ["/:uuid" (single-endpoint)]
   ["/:uuid/view" (view-endpoint)]])
