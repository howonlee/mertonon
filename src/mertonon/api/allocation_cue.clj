(ns mertonon.api.allocation-cue
  "API for messages cueing people for allocations"
  (:require [mertonon.util.io :as uio]))

(defn endpoint []
  {:get  (fn [match]
           {:status 200 :body {:cue "How essential to your work is this entity"}})
   :name ::allocation-cue})

(defn routes []
  [["/" (endpoint)]])
