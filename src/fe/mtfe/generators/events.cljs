(ns mtfe.generators.events
  "Generate FE events"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]))

(defn gen-match* []
  (gen/let [some-crap :some-crap]
    nil))

(def gen-match (gen-match*)

;; match
;; nav path
;; nav sidebar path
;; nav route

;; selection
;; selected resource (for selection success)
;; dag selection

;; error
;; api error
