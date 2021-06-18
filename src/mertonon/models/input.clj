(ns mertonon.models.input
  "Neural network inputs, corresponding to inputs to KPI targets"
  (:require [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]))

(defn canonicalize-input [input]
  (-> (mutils/default-canonicalize input)
      (update :type keyword)))

(defn member->row [input-member]
  (-> (canonicalize-input input-member)
      (mutils/default-member->row)
      (update :data io/maybe-json-encode)))

(defn row->member [input-row]
  (-> (canonicalize-input input-row)
      (mutils/default-row->member)
      (update :data io/maybe-json-decode)))

(def columns [:uuid :layer-uuid :version :created-at :updated-at :name :label :type :data])
(def table [:mertonon.input])
(def query-info {:columns         columns
                 :table           table
                 :member->row     member->row
                 :row->member     row->member})

(def model
  (q/default-model query-info))
