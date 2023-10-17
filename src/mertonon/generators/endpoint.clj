(ns mertonon.generators.endpoint
  "Complex but parameterized Mertonon API endpoints"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.queries :as queries-gen]
            [mertonon.util.registry :as registry]
            [mertonon.api.util :as api-util]
            [tick.core :as t]))

(defn table->raw-table [table]
  (-> table name (clojure.string/split #"\.") second keyword))

(def mass-join-endpoint-gen
  (gen/let [join-q queries-gen/generate-single-joined-query]
    (let [{table            :table
           table->model     :table->model} join-q
          curr-model                       (table->model (registry/raw-table->table (table->raw-table table)))
          endpoint                         (api-util/get-joined-models curr-model join-q)]
      endpoint)))

(def single-join-endpoint-gen
  (gen/let [join-q queries-gen/generate-single-joined-query]
    (let [{table            :table
           table->model     :table->model} join-q
          curr-model                       (table->model (registry/raw-table->table (table->raw-table table)))
          endpoint                         (api-util/get-joined-model curr-model join-q)]
      endpoint)))

(comment
  (let [endpoint (gen/generate mass-join-endpoint-gen)]
    (println (endpoint {}))))
