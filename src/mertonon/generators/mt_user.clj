(ns mertonon.generators.mt-user
  "Net generation for both testing and demo purposes"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.params :as net-params]
            [mertonon.models.constructors :as mtc]))

(defn generate-mt-user*
  [{:keys [name-type] :as params}]
  (gen/let [mt-user-uuid     gen/uuid
            mt-user-email    (gen-data/gen-mt-user-emails name-type)
            mt-user-username (gen-data/gen-mt-user-usernames name-type)]
    {:mt-users [(mtc/->MtUser mt-user-uuid
                              mt-user-email
                              mt-user-username)]}))

(def generate-mt-user (generate-mt-user* net-params/test-gen-params))

(defn generate-mt-user-password*
  [{:keys [name-type] :as params}]
  nil)

(def generate-mt-user-password (generate-mt-user-password* net-params/test-gen-params))

(comment (gen/generate generate-mt-user))
