(ns mertonon.models.mt-user-password
  "A password authentication for a user of mertonon"
  (:require [clojure.test.check.generators :as gen]
            [crypto.password.scrypt :as scrypt]
            [mertonon.generators.mt-user :as mt-user-gen]
            [mertonon.models.utils :as mutils]
            [mertonon.util.io :as io]
            [mertonon.util.queries :as q]
            [tick.core :as t]))

(defn digestify-password
  "Idempotent"
  [{:keys [password] :as mt-user-password}]
  (if (nil? password)
    mt-user-password
    (-> mt-user-password
        (assoc :password-digest (scrypt/encrypt password))
        (dissoc :password))))

(defn canonicalize-mt-user-password [mt-user-password]
  (-> (mutils/default-canonicalize mt-user-password)
      (digestify-password)))

(defn member->row [member]
  (-> (canonicalize-mt-user-password member)
      (mutils/default-member->row)))

(defn row->member [row]
  (-> (canonicalize-mt-user-password row)
      (mutils/default-row->member)))

;;;;
;;;;
;;;;
(def columns [:uuid :version :created-at :updated-at :email :username :canonical-username])
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
