(ns mertonon.models.layer
  "A Layer contains the data for a grouping of cost objects that can have relations to other cost objects."
  (:require [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [mertonon.util.uuid :as uutils]))

(defn member->row [member]
  (-> (mutils/default-canonicalize member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (mutils/default-canonicalize row)
      (mutils/default-member->row)))

(def columns [:uuid :grid-uuid :version :created-at :updated-at :name :label])
(def table [:mertonon.layer])
(def query-info {:columns         columns
                 :table           table
                 :member->row     member->row
                 :row->member     row->member})

(def model
  (q/default-model query-info))
