(ns mertonon.api.login-tests
  "Logging in tests"
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.walk :as walk]
            [mertonon.api.api-tests :as api-tests]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.server.handler :as app-handler]
            [mertonon.test-utils :as tu]))

(defn post-login! [member curr-app]
  (let [endpoint    "/api/v1/login/"
        res         (curr-app {:uri endpoint :request-method :post :body-params member})]
    (api-tests/process-app-response res)))

(defspec just-login-a-bunch
  20
  (prop/for-all
    [{mt-users        :mt-users
      password-logins :password-logins
      orig-passwords  :orig-passwords} authn-gen/password-logins
     bad-password     (gen/such-that
                        (fn [bad-password]
                          (not (some #(= bad-password %) orig-passwords)))
                        gen/string)]
    (tu/with-test-txn
      (let [insert-mt-users!        some crap
            insert-password-logins! some other crap
            good-login-res          more crap
            bad-login-res           more crap]
        (and (uuid? (uutils/some crap))
             (400? some crap w bad login res))))))

(comment
  (require '[mertonon.models.mt-user :as mt-user-model])
  (require '[mertonon.models.password-login :as password-login-model])
  (require '[mertonon.models.constructors :as mtc])
  (require '[mertonon.util.uuid :as uutils])
  (let [mt-user-uuid (uutils/uuid)
        pwd-uuid     (uutils/uuid)
        pwd          "bleh mleh fleh"
        digest       (password-login-model/hash-password pwd)]
    ((mt-user-model/model :create-one!) (mtc/->MtUser mt-user-uuid ";DROP mt_user;--" ";drop mt_user;--"))
    ((password-login-model/model :create-one!) (mtc/->PasswordLogin pwd-uuid mt-user-uuid :default digest)))
  (let [curr-app (app-handler/app-handler)]
    (post-login! {:username ";DRop mt_user;--" :password "bleh mleh fleh"} curr-app)))
