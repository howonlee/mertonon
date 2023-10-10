(ns mertonon.api.allocation-cue
  "API for messages cueing people for allocations"
  (:require [mertonon.util.io :as uio]))

(defn endpoint []
  {:get  (fn [match]
           {:status 200 :body {:cue "How essential is this cost node to the workings of this other cost node?"}})
   :name ::allocation-cue})

(defn routes []
  [["/" (endpoint)]])
