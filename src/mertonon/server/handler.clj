(ns mertonon.server.handler
  (:require [mertonon.server.routes :as routes]
            [mertonon.util.i18n :refer [trs]]
            [mount.core :as mount :refer [defstate]]
            [muuntaja.core :as m]
            [reitit.core :as r]
            [reitit.ring :as rr]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
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
   [wrap-cors :access-control-allow-origin [#".*"]
    :access-control-allow-methods [:get :post]]
   coercion/coerce-request-middleware
   coercion/coerce-response-middleware])

(defn- prod-router-middlewares []
  ;;;;
  ;;;;
  ;;;;
  nil)

(defn- test-router-middlewares []
  ;;;;
  ;;;;
  ;;;;
  nil)

(defn- router [curr-routes curr-middlewares]
  (rr/router
    [curr-routes]
    {:data {:muuntaja   m/instance
            :middleware curr-middlewares}
     :router r/trie-router}))

(defn- prod-router []
  (router (routes/routes) (base-router-middlewares)))

(defn- test-router []
  ;;;;
  ;;;;
  ;;;;
  nil)

;; ---
;; Handling
;; ---

(defn- prod-handler []
  (rr/ring-handler
    (base-router)
    (rr/redirect-trailing-slash-handler)
    (rr/create-default-handler)))

(defn test-handler [] nil)

(comment (prod-handler))
