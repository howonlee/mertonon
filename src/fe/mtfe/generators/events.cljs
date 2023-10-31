(ns mtfe.generators.events
  "Generate FE event things - params for events, full events, etc"
  (:require [clojure.test.check.generators :as gen]
            [mtfe.generators.api :as api-gen]
            [mtfe.generators.navs :as nav-gen]))

(def gen-selection-evt
  (gen/let [resource gen/keyword
            endpoint api-gen/gen-api-endpoints]
    [:selection resource endpoint {}]))

(comment
  (try
    (gen/generate gen-selection-evt)
    (catch :default e (println e))))
