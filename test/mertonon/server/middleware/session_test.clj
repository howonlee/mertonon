(ns mertonon.server.middleware.session-test
  (:require [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.authn :as authn-gen]
            [mertonon.server.handler :as handler]
            [mertonon.server.middleware.session :as mt-session-middleware]
            [mertonon.test-utils :as tu]))

(defn post-app! [member curr-app session]
  (let [endpoint    "/api/v1/grid/"
        req         {:uri endpoint :request-method :post :body-params member}
        req         (if (some? session)
                      (assoc-in req [:cookes "ring-session"] (:uuid session))
                      req)
        res         (curr-app req)
        slurped     (update res :body (comp uio/maybe-slurp uio/maybe-json-decode))]
    slurped))

(defspec endpoint-needs-session-test
  1
  (prop/for-all
    [generated authn-gen/generate-password-logins]
    (let [app-handler    (handler/app-handler)
          session!       (some crap)
          no-session-req some crap
          session-req    some crap]
      nil
      )))

(defproc endpoint-needs-session
  (let [app-handler (handler/app-handler)
        endpoint    "some crap"
        session     some crap]
    ;; make the session
    ;; make the unsessioned request
    ;; make sure it fails
    ;; make a sessioned request
    ;; make sure it succeeds
    ))

(comment (run-tests))
