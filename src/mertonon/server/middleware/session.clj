(ns mertonon.server.middleware.session
  "Want everything to be authn'ed except for the login endpoint itself, basically

  This is allowlisting (whitelisting), which it really doesn't feel like. But it is."
  (:require [mertonon.models.mt-session :as mt-session-model]
            [ring.middleware.session :as ring-session]))

(defn wrap-mertonon-session
  "Synchronous only as of this time. Overwrites your store"
  ([handler]
   (wrap-mertonon-session handler {}))
  ([handler options]
   (let [curr-store (mt-session-model/mt-session-ring-session-store)
         options    (#'ring-session/session-options (assoc options :store curr-store))]
     (fn [request]
          (let [wrapped-request (ring-session/session-request request options)]
            (-> (handler wrapped-request)
                (ring-session/session-response wrapped-request options)))))))
