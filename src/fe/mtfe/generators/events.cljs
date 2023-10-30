(ns mtfe.generators.events
  "Generate FE events"
  (:require [clojure.test.check.generators :as gen]))

(try
  (defn nav-path []
    "bleh")
  (catch :default e
    (println e)))

(gen/generate (gen/elements [1 2 3]))
