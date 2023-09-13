(ns mertonon.models.mt-session
  "A login session of Mertonon
  There are gonna be pretty slow until we have redis integration

  UUID pkey being useful for session id depends upon Java implementation detail
  where java.util.UUID/randomUUID() generates from CSPRNG.
  Don't use FE uuid's for this one, folks"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.models.utils :as mutils]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [mertonon.util.uuid :as uutils]
            [ring.middleware.session.store :refer [SessionStore]]
            [tick.core :as t]))

(defn canonicalize-mt-session [mt-session]
  (-> (mutils/default-canonicalize mt-session)))

(defn member->row [member]
  (-> (canonicalize-mt-session member)
      (mutils/default-member->row)
      (update :value io/maybe-json-encode)))

(defn row->member [row]
  (-> (canonicalize-mt-session row)
      (mutils/default-row->member)
      (update :value io/maybe-json-decode)
      (update :value mt-user-model/row->member)))

(def columns [:uuid :mt-user-uuid :version :created-at :updated-at :expires-at :value])
(def table [:mertonon.mt_session])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))

(deftype MtSessionStore [curr-model]
  SessionStore
  (read-session [_ curr-key]
    ((curr-model :read-one) (uutils/uuid curr-key)))
  (write-session [_ curr-key data]
    ((curr-model :update-one!) (uutils/uuid curr-key) data))
  (delete-session [_ curr-key]
    ((curr-model :hard-delete-one!) (uutils/uuid curr-key))
    nil))

(defn mt-session-ring-session-store
  []
  (MtSessionStore. model))

(comment (mt-session-ring-session-store))
