(ns mtfe.generators.net-store
  "Sometimes, we generate the net itself, which can easily be done with mertonon.generators.net.
  Sometimes, we want to exercise everything up to and including API calls,
  which therefore needs to have basically the contents of the net BE dumped in them.
  
  But it's a hard ask to dump everything for every test, so here's this in-memory thing here to dump into"
  (:require [ajax.core :refer [GET POST]]
            [mtfe.api :as api]))

;; Not a ratom! Just an ordinary cljs atom
(def store (atom {}))

(defn fill-grids [curr]
  ;; ajax and endpoint the thing i guess?
  (reset! curr (assoc some crap :grids)))

(defn fill-store! []
  (do
    (fill-grids!)
    (fill-cost-objects!)))
