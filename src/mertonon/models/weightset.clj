(ns mertonon.models.weightset
  "A Weightset is a set of weights which corresponds to a (sparse) matrix in the neural net."
  (:require [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [mertonon.util.uuid :as uutils]))

(defn canonicalize-weightset [weightset]
  (-> (mutils/default-canonicalize weightset)
      (update :src-layer-uuid uutils/uuid)
      (update :tgt-layer-uuid uutils/uuid)))

(defn member->row [member]
  (-> (canonicalize-weightset member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (canonicalize-weightset row)
      (mutils/default-row->member)))

(def columns [:uuid :src-layer-uuid :tgt-layer-uuid :version :created-at :updated-at :name :label])
(def table [:mertonon.weightset])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))
