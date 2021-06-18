(ns mertonon.models.cost-object
  "A Cost Object is anything that can have costs assigned to it.
  Comparable to a node in a neural network."
  (:require [mertonon.models.utils :as mutils]
            [mertonon.util.queries :as q]))

(defn member->row [member]
  (-> (mutils/default-canonicalize member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (mutils/default-canonicalize row)
      (mutils/default-row->member)))

(def columns [:uuid :layer-uuid :version :created-at :updated-at :name :label :activation :delta])
(def table [:mertonon.cost-object])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))
