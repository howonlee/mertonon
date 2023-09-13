(ns mertonon.server.middleware.session
  "Want everything to be authn'ed except for the login endpoint itself, basically

  This is allowlisting (whitelisting), which it really doesn't feel like. But it is."
  (:require [ring.middleware.session :as ring-session]))

(defn wrap-mertonon-session
  "Synchronous only as of this time."
  ([handler]
   (wrap-mertonon-session handler {}))
  ([handler options]
   (let [options (#'ring-session/session-options options)]
     (fn ([request]
          ;;;; if login whitelist something
          (let [wrapped-request (ring-session/session-request request options)
                printo          (println (:session wrapped-request))]
            (-> (handler wrapped-request)
                (ring-session/session-response wrapped-request options))))))))

