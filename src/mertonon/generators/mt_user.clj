(ns mertonon.generators.mt-user
  "Net generation for both testing and demo purposes"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.params :as net-params]
            [mertonon.models.constructors :as mtc]))

(defn generate-mt-users*
  [{:keys [name-type] :as params}]
  (gen/let [mt-user-uuid     gen/uuid
            mt-user-email    (gen-data/gen-mt-user-emails name-type)
            mt-user-username (gen-data/gen-mt-user-usernames name-type)]
    {:mt-users [(mtc/->MtUser mt-user-uuid
                              mt-user-email
                              mt-user-username)]}))

(def generate-mt-users (generate-mt-users* net-params/test-gen-params))

(defn generate-password-login*
  [{:keys [name-type] :as params}]
  (gen/let [mt-users        (generate-mt-users* params)
            ;;;;;
            ;;;;;
            ;;;;;
            orig-passwords  (gen/vectors some crap)]
    (let [digests some crap
          password-logins some crap]
      (assoc mt-users
             :orig-passwords  orig-passwords
             :password-logins password-logins))))

(def generate-password-login (generate-password-login* net-params/test-gen-params))

(comment (gen/generate generate-mt-user))
