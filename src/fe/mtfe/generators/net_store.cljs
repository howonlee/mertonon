(ns mtfe.generators.net-store
  "Sometimes, we generate the net itself, which can easily be done with mertonon.generators.net.
  Sometimes, we want to exercise everything up to and including API calls,
  which therefore needs to have basically the contents of the net BE dumped in them.
  
  But it's a hard ask to dump everything for every test, so here's this in-memory thing here to dump into"
  (:require [ajax.core :refer [GET POST json-request-format json-response-format]]
            [mtfe.api :as api]))

;; Not a ratom, just an ordinary cljs atom. Shouldn't be reactive
(def store (atom {}))

(defn fill! [api-endpoint store-key]
  (GET api-endpoint
       {:format          (json-request-format)
        :response-format (json-response-format {:keywords? true})
        :handler         (fn [resp]
                           (swap! store #(assoc % store-key resp)))}))

(defn fill-store! []
  (do
    (fill! (api/grid) :grids)
    (fill! (api/layer) :layers)
    (fill! (api/cost-object) :cost-objects)
    (fill! (api/weightset) :weightsets)
    (fill! (api/weight) :weights)
    (fill! (api/entry) :entries)
    (fill! (api/input) :inputs)
    (fill! (api/loss) :losses)
    (fill! (api/mt-user) :mt-users)
    (fill! (api/password-login) :password-logins)))

(comment (fill-store!)
         (cljs.pprint/pprint @store))
