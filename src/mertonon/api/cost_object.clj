(ns mertonon.api.cost-object
  "API for cost objects"
  (:require [mertonon.api.util :as api-util]
            [mertonon.models.cost-object :as cost-object-model]
            [mertonon.models.entry :as entry-model]
            [mertonon.models.input :as input-model]
            [mertonon.models.layer :as layer-model]
            [mertonon.models.loss :as loss-model]
            [mertonon.models.weightset :as weightset-model]
            [mertonon.util.uuid :as uutils]))

(defn single-endpoint []
  {:get    (api-util/get-model cost-object-model/model)
   :put    (api-util/update-model cost-object-model/model)
   :delete (api-util/delete-model cost-object-model/model)
   :name   ::cost-object})

(defn mass-endpoint []
  {:get    (api-util/get-models cost-object-model/model)
   :post   (api-util/create-model cost-object-model/model)
   :put    (api-util/update-model cost-object-model/model)
   :delete (api-util/delete-models cost-object-model/model)
   :name   ::cost-objects})

(defn cost-object-view-get [match]
  (let [cobj-uuid          (api-util/path-uuid match)
        cost-object        ((cost-object-model/model :read-one) cobj-uuid)
        layer              ((layer-model/model :read-one) (:layer-uuid cost-object))
        layer-inputs       (if (nil? layer) []
                             ((input-model/model :read-where) [:= :layer-uuid (:uuid layer)]))
        layer-losses       (if (nil? layer) []
                             ((loss-model/model :read-where) [:= :layer-uuid (:uuid layer)]))
        ;; src-weightsets: weightsets which are upstream from layer
        src-weightsets     ((weightset-model/model :read-where) [:= :tgt-layer-uuid (:uuid layer)])
        ;; tgt-weightsets: weightsets which are downstream from layer
        tgt-weightsets     ((weightset-model/model :read-where) [:= :src-layer-uuid (:uuid layer)])
        entries            ((entry-model/model :read-where) [:= :cobj-uuid cobj-uuid])
        body-res           {:cost-object    cost-object
                            :layer          layer
                            :losses         (sort-by :uuid layer-losses)
                            :inputs         (sort-by :uuid layer-inputs)
                            :entries        (sort-by :uuid entries)
                            :src-weightsets src-weightsets
                            :tgt-weightsets tgt-weightsets}]
    {:status 200 :body body-res}))

(defn view-endpoint []
  {:get cost-object-view-get :name ::cost-object-view})

(defn routes []
  [["/" (mass-endpoint)]
   ["/:uuid" (single-endpoint)]
   ["/:uuid/view" (view-endpoint)]])
