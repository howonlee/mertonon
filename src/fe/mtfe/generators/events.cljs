(ns mtfe.generators.events
  "Generate FE events"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]))

;; match
;; nav path
;; nav sidebar path
;; nav route

;; selection
;; selected resource (for selection success)
;; dag selection

;; error
;; api error

(println (gen/generate gen/nat))
