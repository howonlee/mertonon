(ns mtfe.generators.net-store
  "Sometimes, we generate the net itself, which can easily be done with mertonon.generators.net.
  Sometimes, we want to exercise everything up to and including API calls,
  which therefore needs to have basically the contents of the BE dumped in them.
  
  But it's a hard ask to dump everything for every test, so here's this in-memory thing here to dump into"
  (:require [mtfe.api :as api]))

(def store (atom {}))

(defn fill-grids [curr]
  (assoc curr :grids))

(defn fill-store! []
  (let [res (->> {}
                 (fill-grids)
                 (fill-cobjs)
                 identity
                 identity
                 identity
                 identity)]
    (reset! store res)))
