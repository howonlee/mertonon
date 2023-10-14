(ns mertonon.generators.endpoint
  "Complex but parameterized Mertonon API endpoints"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.queries :as queries-gen]
            [mertonon.api.util :as api-util]
            [tick.core :as t]))

(def join-endpoint-gen
  (gen/let [join-q queries-gen/generate-single-joined-query]
    (let [{table            :table
           table->model     :table->model} join-q
          curr-model                       (table->model table)
          endpoint                         (api-util/get-joined-models curr-model join-q)]
      endpoint)))

(comment (println (gen/generate join-endpoint-gen)))
