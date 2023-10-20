(ns mtfe.generators.events
  "Generate FE events"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]))

(println (gen/generate gen/nat))
