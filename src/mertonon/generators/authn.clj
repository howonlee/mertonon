(ns mertonon.generators.authn
  "Mertonon user, login, other authn-related generates"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.params :as net-params]
            [mertonon.models.constructors :as mtc]
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as pwd-model]
            [tick.core :as t]))

(defn generate-mt-users*
  [{:keys [name-type] :as params}]
  (gen/let [num-entries       (gen/choose 2 5)
            mt-user-uuids     (gen/vector gen/uuid num-entries)
            mt-user-emails    (gen/vector-distinct (gen-data/gen-mt-user-emails name-type) {:num-elements num-entries})
            mt-user-usernames (gen/vector-distinct (gen-data/gen-mt-user-usernames name-type) {:num-elements num-entries})]
    (let [vecs (map vector mt-user-uuids mt-user-emails mt-user-usernames)]
      {:mt-users (mapv (fn [[mt-user-uuid mt-user-email mt-user-username]]
                         (-> (mtc/->MtUser mt-user-uuid
                                           mt-user-email
                                           mt-user-username)
                             (mt-user-model/canonicalize-username)))
                       vecs)})))

(def generate-mt-users (generate-mt-users* net-params/test-gen-params))

;; scrypt is designed to be slow, so expect slowness in all tests with this one

(defn generate-password-logins*
  [{:keys [name-type] :as params}]
  (gen/let [mt-users        (generate-mt-users* params)
            orig-passwords  (gen/vector-distinct
                              (gen/fmap clojure.string/join (gen/vector gen/char 1 20))
                              {:num-elements (-> mt-users :mt-users count)})
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
  (gen/let [password-logins (generate-password-logins* params)
            uuids           (gen/vector gen/uuid (->> password-logins :mt-users count))
            ;;;; not the expires-at, the delta we add to get the expires-ats
            expirations     (gen/vector gen/nat (->> password-logins :mt-users count))]
    (let [curr-time     (t/instant)
          mt-user->sess (fn [idx mt-user]
                          (mtc/->MtSession
                            (nth uuids idx)
                            (:uuid mt-user)
                            (t/>> curr-time (t/new-duration (nth expirations idx) :minutes))
                            mt-user))
          mt-sessions (vec (map-indexed mt-user->sess (:mt-users password-logins)))]
      (assoc password-logins :mt-sessions mt-sessions))))

(def generate-mt-sessions (generate-mt-sessions* net-params/test-gen-params))

(comment (gen/generate generate-password-logins))
