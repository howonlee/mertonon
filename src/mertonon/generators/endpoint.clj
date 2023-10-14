(ns mertonon.generators.endpoint
  "Complex but parameterized Mertonon API endpoints"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.queries :as queries-gen]
            [tick.core :as t]))

(def join-endpoint-gen
  (gen/let [join-q queries-gen/generate-single-joined-query]
    nil))
