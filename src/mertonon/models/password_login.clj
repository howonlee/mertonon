(ns mertonon.models.password-login
  "A password authentication for a user of mertonon.

  Session-based password auth only at this time, we will suck our teeth a moderate amount if we get asked for JWT's.
  Getting JWT to actually be secure is a pretty baroque exercise, is the problem"
  (:require [clojure.test.check.generators :as gen]
            [crypto.password.scrypt :as scrypt]
            [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [tick.core :as t]))

(defn hash-password
  [unhashed-password]
  (scrypt/encrypt unhashed-password))

(defn password-check
  [to-check password-login]
  (scrypt/check to-check password-login))

(defn is-digest?
  "Don't actually rely on this, this is just convenience"
  [{:keys [password-digest] :as password-login}]
  (clojure.string/starts-with? password-digest "$s0$"))

(defn canonicalize-password-login [password-login]
  (-> (mutils/default-canonicalize password-login)
      (dissoc :password)))

(defn member->row [member]
  (-> (canonicalize-password-login member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (canonicalize-password-login row)
      (mutils/default-row->member)))

(def columns [:uuid :mt-user-uuid :version :created-at :updated-at
              :password-state :password-digest :recovery-token-hash])
(def table [:mertonon.password-login])
(def query-info {:columns     columns
                 :table       table
                 :member->row member->row
                 :row->member row->member})

(def model
  (q/default-model query-info))

(comment
  (require '[crypto.password.scrypt :as scrypt])
  (scrypt/encrypt "bleh"))
