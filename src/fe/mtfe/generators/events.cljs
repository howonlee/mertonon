(ns mtfe.generators.events
  "Generate FE event things - params for events, full events, etc"
  (:require [clojure.test.check.generators :as gen]
            [mtfe.generators.api :as api-gen]
            [mtfe.generators.navs :as nav-gen]))

(def gen-selection-event
  (gen/let [resource gen/keyword
            endpoint api-gen/gen-api-endpoints]
    [:selection resource endpoint {}]))

(comment
  (try
    (gen/generate gen-selection-event)
    (catch :default e (cljs.pprint/pprint e))))
