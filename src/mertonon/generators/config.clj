(ns mertonon.generators.config
  "The entire Mertonon config should be generatable
  because configs are usually the _most_ combinatoric aspects of a software thing
  
  Hence why they're the biggest points of failure: see Gray's old thingy

  http://jimgray.azurewebsites.net/papers/TandemTR85.7_WhyDoComputersStop.pdf"
  (:require [clojure.test.check.generators :as gen]))

(defn generate-env-mode []
  (gen/elements [:development :production]))

(defn generate-host []
  ;; TODO: buy 10 domains...
  (gen/return "localhost"))

(defn generate-port []
  ;; Some ephermeral port
  (gen/large-integer* {:min 1025 :max 9999}))

(defn generate-feature-flags []
  (gen/return {}))

(def generate-config
  (gen/hash-map
    :mt-env-mode   (generate-env-mode)
    :mt-host       (generate-host)
    :mt-post       (generate-port)
    :feature-flags (generate-feature-flags)))

(comment (gen/generate generate-config))
