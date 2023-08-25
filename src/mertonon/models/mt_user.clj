(ns mertonon.models.mt-user
  "A user of Mertonon"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.generators.net :as net-gen]
            [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [tick.core :as t]))

;;;;;;;;
;;;;;;;;
;;;;;;;;
;;;;;;;;
;;;;;;;;
;;;;;;;;

(defn canonicalize-grid [grid]
  (-> (mutils/default-canonicalize grid)
      (update :optimizer-type keyword)
      (update :version io/maybe-parse-int)))

(defn member->row [member]
  (-> (canonicalize-grid member)
      (mutils/default-member->row)
      (update :hyperparams io/maybe-json-encode)))

(defn row->member [row]
  (-> (canonicalize-grid row)
      (mutils/default-row->member)
      (update :hyperparams io/maybe-json-decode)))

(def columns [:uuid :version :created-at :updated-at :name :label :optimizer-type :hyperparams])
(def table [:mertonon.grid])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))

(comment (let [member (-> (gen/generate net-gen/generate-grid)
                          :grids
                          first)]
           (row->member (member->row member))))
