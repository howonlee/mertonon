(ns mertonon.api.session-tests
  "Logging in, logging out tests"
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
            [mertonon.server.handler :as handler]
            [mertonon.test-utils :as tu]
            [mertonon.util.io :as uio]))

(defn post-login! [member curr-app]
  (let [endpoint    "/api/v1/session/"
        res         (curr-app {:uri endpoint :request-method :post :body-params member})
        slurped     (update res :body (comp walk/keywordize-keys uio/maybe-slurp uio/maybe-json-decode))]
    slurped))

(defn post-logout! [curr-app]
  (let [endpoint    "/api/v1/session/"
        res         (curr-app {:uri endpoint :request-method :delete})
        slurped     (update res :body (comp walk/keywordize-keys uio/maybe-slurp uio/maybe-json-decode))]
    slurped))

(defn grid-get [curr-app]
  (let [endpoint    "/api/v1/grid/"
        res         (curr-app {:uri endpoint :request-method :get :body-params {}})]
    res))

(defspec just-login-a-bunch
  tu/few
  (prop/for-all
    [generated    authn-gen/generate-password-logins
     ;; bad by dint of being too long
     bad-password (gen/fmap clojure.string/join (gen/vector gen/char 100))]
    (tu/with-test-txn
      (let [{mt-users        :mt-users
             password-logins :password-logins
             orig-passwords  :orig-passwords} generated
            insert-mt-users!                  ((mt-user-model/model :create-many!) mt-users)
            insert-password-logins!           ((password-login-model/model :create-many!) password-logins)
            curr-app                          (handler/app-handler)
            good-login-res                    (post-login!  {:username (-> mt-users first :canonical-username)
                                                             :password (-> orig-passwords first)} curr-app)
            wrong-user-res                    (post-login! {:username (-> mt-users first :canonical_username)
                                                            :password (-> orig-passwords second)} curr-app)
            bad-password-res                  (post-login! {:username (-> mt-users first :canonical_username)
                                                            :password bad-password} curr-app)]
        (and (= 200 (:status good-login-res))
             (= 401 (:status wrong-user-res))
             (= 401 (:status bad-password-res)))))))

(defspec login-logout
  tu/few
  (prop/for-all
    [generated    authn-gen/generate-password-logins]
    (tu/with-test-txn
      (let [{mt-users        :mt-users
             password-logins :password-logins
             orig-passwords  :orig-passwords} generated
            insert-mt-users!                  ((mt-user-model/model :create-many!) mt-users)
            insert-password-logins!           ((password-login-model/model :create-many!) password-logins)
            curr-app                          (handler/app-handler)
            login-res                         (post-login!  {:username (-> mt-users first :canonical-username)
                                                             :password (-> orig-passwords first)} curr-app)
            logout-res                        (post-logout! curr-app)
            invalid-grid-get                  (grid-get curr-app)]
        (and (= 200 (:status login-res))
             (= 200 (:status logout-res))
             (= 401 (:status invalid-grid-get)))))))

(comment (run-tests))
