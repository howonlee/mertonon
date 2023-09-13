(ns mertonon.server.handler
  (:require [mertonon.server.middleware.auth :as mt-auth-middleware]
            [mertonon.server.middleware.session :as mt-session-middleware]
            [mertonon.server.routes :as routes]
            [mertonon.util.i18n :refer [trs]]
            [mount.core :as mount :refer [defstate]]
            [muuntaja.core :as m]
            [reitit.core :as r]
            [reitit.ring :as rr]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.params :as params]
            [reitit.ring.middleware.exception :as rr-exceptions]
            [taoensso.timbre :as timbre :refer [log]]))

;; ---
;; Routing
;; ---

(defn- base-router-middlewares []
  [params/wrap-params
   muuntaja/format-middleware
   ;; Note order matters.
   ;; TODO: Tighten up origins
   [wrap-cors
    :access-control-allow-origin [#".*"]
    :access-control-allow-methods [:get :post :put]]
   coercion/coerce-request-middleware
   coercion/coerce-response-middleware])

(defn- prod-router-middlewares []
  (into (base-router-middlewares)
        [mt-session-middleware/wrap-mertonon-session
         mt-auth-middleware/wrap-mertonon-auth]))

(defn- test-router-middlewares []
  (base-router-middlewares))

(defn- router [curr-routes curr-middlewares]
  (rr/router
    [curr-routes]
    {:data {:muuntaja   m/instance
            :middleware curr-middlewares}
     :router r/trie-router}))

(defn- prod-router []
  (router (routes/routes) (prod-router-middlewares)))

(defn- test-router []
  (router (routes/routes) (test-router-middlewares)))

;; ---
;; Handling
;; ---

(defn app-handler
  "Production handler."
  []
  (rr/ring-handler
    (prod-router)
    (rr/redirect-trailing-slash-handler)
    (rr/create-default-handler)))

(defn test-handler
  "Unsecured handler in order to not eat huge slowdowns in testing.
  Do not use in production.

  TODO: enforce not using in production"
  []
  (rr/ring-handler
    (test-router)
    (rr/redirect-trailing-slash-handler)
    (rr/create-default-handler)))

(comment (app-handler))
