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

(defn- router [curr-routes curr-middlewares]
  (rr/router
    [curr-routes]
    {:data {:muuntaja   m/instance
            :middleware curr-middlewares}
     :router r/trie-router}))

(defn- base-router []
  (router (routes/routes) (base-router-middlewares)))

;; ---
;; Handling
;; ---

(defn- base-handler []
  (rr/ring-handler
    (base-router)
    (rr/redirect-trailing-slash-handler)
    (rr/create-default-handler)))

(defn app-handler [] (base-handler))

(defn test-handler [] nil)

(comment (base-router))
