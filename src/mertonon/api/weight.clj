(ns mertonon.api.weight
  "API for weights"
  (:require [clojure.data.json :as json]
            [mertonon.api.util :as api-util]
            [mertonon.models.cost-object :as cost-object-model]
            [mertonon.models.weight :as weight-model]
            [mertonon.models.weightset :as weightset-model]
            [mertonon.util.uuid :as uutils]))

(defn single-endpoint []
  {:get    (api-util/get-model weight-model/model)
   :delete (api-util/delete-model weight-model/model)
   :name   ::weight})

(defn mass-endpoint []
  {:get    (api-util/get-models weight-model/model)
   :post   (api-util/create-model weight-model/model)
   :delete (api-util/delete-models weight-model/model)
   :name   ::weights})

(defn weight-view-get [match]
  (let [weight-uuid (uutils/uuid (-> match :path-params :uuid))
        weight      ((weight-model/model :read-one) weight-uuid)
        src-cobj    ((cost-object-model/model :read-one) (:src-cobj-uuid weight))
        tgt-cobj    ((cost-object-model/model :read-one) (:tgt-cobj-uuid weight))
        weightset   ((weightset-model/model :read-one) (:weightset-uuid weight))
        body-res    {:weight    weight
                     :src-cobj  src-cobj
                     :tgt-cobj  tgt-cobj
                     :weightset weightset}]
    {:status 200 :body (json/write-str body-res)}))

(defn view-endpoint []
  {:get weight-view-get :name ::weight-view})

(defn routes []
  [["/" (mass-endpoint)]
   ["/:uuid" (single-endpoint)]
   ["/:uuid/view" (view-endpoint)]])
