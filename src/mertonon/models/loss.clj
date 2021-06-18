(ns mertonon.models.loss
  "Neural network losses, corresponding to targets and KPI's and profit/losses and stuff"
  (:require [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]))

(defn canonicalize-loss [loss]
  (-> (mutils/default-canonicalize loss)
      (update :type keyword)))

(defn member->row [loss-member]
  (-> (canonicalize-loss loss-member)
      (mutils/default-member->row)
      (update :data io/maybe-json-encode)))

(defn row->member [loss-row]
  (-> (canonicalize-loss loss-row)
      (mutils/default-row->member)
      (update :data io/maybe-json-decode)))

(def columns [:uuid :layer-uuid :version :created-at :updated-at :name :label :type :data])
(def table [:mertonon.loss])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))
