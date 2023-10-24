(ns mertonon.models.weight
  "A Weight is a relation between two cost objects"
  (:require [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [mertonon.util.uuid :as uutils]))

(defn canonicalize-weight [weight]
  (-> (mutils/default-canonicalize weight)
      (update :src-cobj-uuid uutils/uuid)
      (update :tgt-cobj-uuid uutils/uuid)
      (update :type keyword)
      (update :value io/maybe-parse-int)))

(defn member->row [member]
  (-> (canonicalize-weight member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (canonicalize-weight row)
      (mutils/default-row->member)))

;; Note: no name, just label
(def columns [:uuid :weightset-uuid :src-cobj-uuid :tgt-cobj-uuid
              :version :created-at :updated-at :label :type :value :grad])
(def table [:mertonon.weight])
(def query-info {:columns         columns
                 :table           table
                 :member->row     member->row
                 :row->member     row->member})

(def model
  (q/default-model query-info))
