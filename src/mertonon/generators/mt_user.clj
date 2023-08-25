(ns mertonon.generators.mt-users
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
    {:mertonon.mt-users [(mtc/->MtUser mt-user-uuid
                                       mt-user-email
                                       mt-user-username)]}))

(def generate-mt-user (generate-mt-user* net-params/test-gen-params))

(comment (gen/generate generate-mt-user))
