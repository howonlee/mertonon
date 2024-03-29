(ns mertonon.models.mt-user
  "A user of Mertonon"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [tick.core :as t]))

(defn canonicalize-username [{:keys [username] :as mt-user}]
  (let [canonicalized ((fnil clojure.string/lower-case "") username)]
    (assoc mt-user :canonical-username canonicalized)))

(defn canonicalize-mt-user [mt-user]
  (-> (mutils/default-canonicalize mt-user)
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

(comment (let [req    (require '[mertonon.generators.authn :as authn-gen])
               member (-> (gen/generate authn-gen/generate-grid)
                          :mertonon.mt-users
                          first)]
           (row->member (member->row member))))
