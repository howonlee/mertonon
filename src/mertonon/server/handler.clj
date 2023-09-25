(ns mertonon.server.handler
  (:require [mertonon.server.middleware.auth :as mt-auth-middleware]
            [mertonon.server.middleware.json :as mt-json-middleware]
            [mertonon.server.middleware.session :as mt-session-middleware]
            [mertonon.server.routes :as routes]
            [mertonon.util.i18n :refer [trs]]
            [mount.core :as mount :refer [defstate]]
            [muuntaja.core :as m]
            [reitit.core :as r]
            [reitit.ring :as rr]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as ring-exception]
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
   wrap-cookies
   ;; muuntaja makes everything a wibbley wobbley binary stream so
   ;; shove anything dealing with plain request response after it
   ;; I understand this kind of wrecks the superfast promises but I don't care yet.
   ;; Will care someday.
   ;; TODO: care
   muuntaja/format-middleware
   mt-json-middleware/wrap-json
   ;; Note order matters.
   ;; TODO: Tighten up origins
   [wrap-cors
    :access-control-allow-origin [#".*"]
    :access-control-allow-methods [:get :post :put]]
   ring-exception/exception-middleware
   coercion/coerce-request-middleware
   coercion/coerce-response-middleware])

(defn- prod-router-middlewares []
  (into (base-router-middlewares)
        [mt-session-middleware/wrap-mertonon-session
         mt-auth-middleware/wrap-mertonon-auth]))

(defn- test-router-middlewares [middlewares]
  (into (base-router-middlewares) middlewares))

(defn- router [curr-routes curr-middlewares]
  (rr/router
    [curr-routes]
    {:data {:muuntaja   m/instance
            :middleware curr-middlewares}
     :router r/trie-router}))

(defn- prod-router []
  (router (routes/routes) (prod-router-middlewares)))

(defn- test-router [middlewares]
  (router (routes/routes) (test-router-middlewares middlewares)))

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
  If you are using this in production something terrible has happened to your life

  TODO: enforce not using in production"
  [middlewares]
  (rr/ring-handler
    (test-router middlewares)
    (rr/redirect-trailing-slash-handler)
    (rr/create-default-handler)))

(comment (app-handler))
