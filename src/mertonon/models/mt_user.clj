(ns mertonon.models.mt-user
  "A user of Mertonon"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.generators.mt-user :as mt-user-gen]
            [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [tick.core :as t]))

(defn canonicalize-username [{:keys [username] :as mt-user}]
  (let [canonicalized (str/lowercase username)]
    (assoc mt-user :canonical-username canonicalized)))

(defn canonicalize-mt-user [mt-user]
  (-> (mutils/default-canonicalize mt-user)
      (update :version io/maybe-parse-int)
      (canonicalize-username)))

(defn member->row [member]
  (-> (canonicalize-mt-user member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (canonicalize-mt-user row)
      (mutils/default-row->member)))

(def columns [:uuid :version :created-at :updated-at :email :username :canonical-username])
(def table [:mertonon.mt_user])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))

(comment (let [member (-> (gen/generate mt-user-gen/generate-grid)
                          :mertonon.mt-users
                          first)]
           (row->member (member->row member))))
