(ns mertonon.api.weightset
  "API for weightsets"
  (:require [clojure.data.json :as json]
            [mertonon.api.util :as api-util]
            [mertonon.models.cost-object :as cost-object-model]
            [mertonon.models.layer :as layer-model]
            [mertonon.models.weight :as weight-model]
            [mertonon.models.weightset :as weightset-model]
            [mertonon.util.uuid :as uutils]))

(defn single-endpoint []
  {:get    (api-util/get-model weightset-model/model)
   :delete (api-util/delete-model weightset-model/model)
   :name   ::weightset})

(defn mass-endpoint []
  {:get    (api-util/get-models weightset-model/model)
   :post   (api-util/create-model weightset-model/model)
   :delete (api-util/delete-models weightset-model/model)
   :name   ::weightsets})

 (defn weightset-view-get [match]
   (let [ws-uuid   (api-util/path-uuid match)
         weightset ((weightset-model/model :read-one) ws-uuid)
         src-layer ((layer-model/model :read-one) (:src-layer-uuid weightset))
         tgt-layer ((layer-model/model :read-one) (:tgt-layer-uuid weightset))
         weights   (sort-by :uuid ((weight-model/model :read-where) [:= :weightset-uuid ws-uuid]))
         src-cobjs ((cost-object-model/model :read-where) [:= :layer-uuid (:src-layer-uuid weightset)])
         tgt-cobjs ((cost-object-model/model :read-where) [:= :layer-uuid (:tgt-layer-uuid weightset)])
         body-res  {:weightset weightset
                    :src-layer src-layer
                    :tgt-layer tgt-layer
                    :weights   weights
                    :src-cobjs src-cobjs
                    :tgt-cobjs tgt-cobjs}]
     {:status 200 :body (json/write-str body-res)}))

(defn view-endpoint []
  {:get weightset-view-get :name ::weightset-view})

(defn routes []
  [["/" (mass-endpoint)]
   ["/:uuid" (single-endpoint)]
   ["/:uuid/view" (view-endpoint)]])
