(ns mertonon.models.mt-user-password
  "A password authentication for a user of mertonon"
  (:require [clojure.test.check.generators :as gen]
            [crypto.password.scrypt :as scrypt]
            [mertonon.generators.mt-user :as mt-user-gen]
            [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [tick.core :as t]))

(defn hash-password
  "Idempotent"
  [unhashed-password]
  (scrypt/encrypt unhashed-password))

(defn password-check
  [mt-user-password to-check]
  ;;;;
  ;;;;
  ;;;;
  ;;;;
  nil)

(defn check-password-is-digest
  "Don't actually rely on this, this is just convenience"
  [{:keys [password-digest] :as mt-user-password}]
  ;;;;
  ;;;;
  ;;;;
  nil)

(defn canonicalize-mt-user-password [mt-user-password]
  (-> (mutils/default-canonicalize mt-user-password)
      (check-password-is-digest)
      ;; Only want password digests
      (dissoc :password)))

(defn member->row [member]
  (-> (canonicalize-mt-user-password member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (canonicalize-mt-user-password row)
      (mutils/default-row->member)))

(def columns [:uuid :mt-user-uuid :version :created-at :updated-at
              :password-state :password-digest :recovery-token-hash])
(def table [:mertonon.mt_user_password])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))

(comment
  (require '[crypto.password.scrypt :as scrypt])
  (scrypt/encrypt "bleh"))
