(ns mertonon.server.routes
  (:require [mertonon.util.config :as mt-config]
            [mertonon.util.i18n :refer [trs]]
            [mertonon.api.routes :as api]
            [reitit.core :as r]
            [reitit.ring :as rr]
            [selmer.parser :as selmer]
            [taoensso.timbre :as timbre :refer [log]]))

(defn- load-template
  "Like any template loader,
  do not accept user input for path and try not to for vars"
  [path variables]
  (try
    (selmer/render-file path variables)
    (catch IllegalArgumentException e
      (let [message (trs "Failed to load template ''{0}''." path)]
        (log :error e message)
        (throw (Exception. message e))))))

(defn routes []
  (let [curr-config   (mt-config/config)
        mt-url        (mt-config/host-url curr-config)
        ;; TODO: find a safer way, probably just some endpoint
        home-bindings {:mt_url mt-url
                       :mt_feature_flags (curr-config :feature-flags)}]
    [["/" {:get
           (fn [_] {:status 200 :body (load-template "public/index.html" home-bindings)})}]
     ["/favicon.ico" {:get (fn [_] {:status 200 :body (load-template "favicon.ico" {})})}]
     ["/public/*" (rr/create-resource-handler)]
     ["/api/v1" (api/routes)]]))

(comment (api/routes)
         (r/match-by-path (routes) "/api/v1/grad"))
