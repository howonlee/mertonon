(ns mertonon.models.health-check
  "Health check in mertonon consists of POSTing to the DB, to check that the actual DB connection is good,
  because what is miserable in computer-touching is state. You gotta have some confirmation that state's getting mutated"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [tick.core :as t]))

(defn canonicalize-hc [hc]
  (-> (mutils/default-canonicalize hc)))

(defn member->row [member]
  (-> (canonicalize-hc member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (canonicalize-hc row)
      (mutils/default-row->member)))

(def columns [:uuid :version :created-at :updated-at])
(def table [:mertonon.health_check])

(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))
