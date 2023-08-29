(ns mertonon.generators.mt-user
  "Mertonon user, login, other authn-related generates"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.params :as net-params]
            [mertonon.models.constructors :as mtc]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as pwd-model]
            ))

(defn generate-mt-users*
  [{:keys [name-type] :as params}]
  (gen/let [mt-user-uuid     gen/uuid
            mt-user-email    (gen-data/gen-mt-user-emails name-type)
            mt-user-username (gen-data/gen-mt-user-usernames name-type)]
    {:mt-users [(-> (mtc/->MtUser mt-user-uuid
                              mt-user-email
                              mt-user-username)
                    mt-user-model/canonicalize-username)]}))

(def generate-mt-users (generate-mt-users* net-params/test-gen-params))

(defn generate-password-logins*
  [{:keys [name-type] :as params}]
  (gen/let [mt-users        (generate-mt-users* params)
            orig-passwords  (gen/vector gen/string (-> mt-users :mt-users count))
            uuids           (gen/vector gen/uuid (-> mt-users :mt-users count))]
    (let [digests         (mapv pwd-model/hash-password orig-passwords)
          password-logins (vec
                            (for [[mt-user digest uuid] (map vector (:mt-users mt-users) digests uuids)]
                              (mtc/->PasswordLogin uuid
                                                   (:uuid mt-user)
                                                   :default
                                                   digest)))]
      (assoc mt-users
             :orig-passwords  orig-passwords
             :password-logins password-logins))))

(def generate-password-logins (generate-password-logins* net-params/test-gen-params))

(defn generate-mt-sessions*
  [{:keys [name-type] :as params}]
  nil)

(def generate-mt-sessions (generate-mt-sessions* net-params/test-gen-params))

(comment (gen/generate generate-password-logins))
