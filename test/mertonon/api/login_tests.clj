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
            [mertonon.models.mt-user :as mt-user-model]
            [mertonon.models.password-login :as password-login-model]
            [mertonon.server.handler :as app-handler]
            [mertonon.test-utils :as tu]))

(defn post-login! [member curr-app]
  (let [endpoint    "/api/v1/login/"
        res         (curr-app {:uri endpoint :request-method :post :body-params member})]
    (api-tests/process-app-response res)))

(defspec just-login-a-bunch
  20
  (prop/for-all
    [generated authn-gen/generate-password-logins]
    (tu/with-test-txn
      (let [{mt-users        :mt-users
             password-logins :password-logins
             orig-passwords  :orig-passwords} generated
            insert-mt-users!                  ((mt-user-model/model :create-many!) mt-users)
            insert-password-logins!           ((password-login-model/model :create-many!) password-logins)
            curr-app                          (app-handler/app-handler)
            good-login-res                    (post-login! {:username (-> mt-users first :canonical_username)
                                                            :password (-> orig-passwords first)} curr-app)
            printo                            (println good-login-res)]
        ;; bad-password-res        (post-login! {:username (-> mt-users first :canonical_username)
        ;;                                       :password bad-password} curr-app)
        ;; wrong-user-res          (post-login! {:username (-> mt-users first :canonical_username)
        ;;                                       :password (-> orig-passwords second)} curr-app)]
        false))))

(comment
  (run-tests))
