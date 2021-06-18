(ns mertonon.models.entry
  "Journal entries. These are transaction data which get combined into patterns for the net"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [tick.core :as t]))

(defn canonicalize-entry [entry]
  (-> (mutils/default-canonicalize entry)
      (update :value bigdec)
      (update :type keyword)
      (update :entry-date t/instant)))

(defn member->row [member]
  (-> (canonicalize-entry member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (canonicalize-entry row)
      (mutils/default-row->member)))

(def columns [:uuid :cobj-uuid :version :created-at :updated-at :name :label :entry-date :type :value])
(def table [:mertonon.entry])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))
