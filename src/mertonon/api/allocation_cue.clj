(ns mertonon.api.allocation-cue
  "API for messages cueing people for allocations"
  (:require [clojure.data.json :as json]
            [mertonon.util.io :as uio]))

(defn endpoint []
  {:get  (fn [match]
           {:status 200 :body (json/write-str
                                {:cue "How essential to your work is this entity"})})
   :name ::allocation-cue})

(defn routes []
  [["/" (endpoint)]])
