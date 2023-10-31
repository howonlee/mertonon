(ns mtfe.generators.api
  "Generate FE api endpoints"
  (:require [clojure.test.check.generators :as gen]
            [mtfe.api :as api]
            [mtfe.generators.net-store :as net-store]))

(defn non-generator-api? [k]
  (not (clojure.string/includes? (name k) "generator")))

(def api-endpoint-fns
  (let [publics (ns-publics 'mtfe.api)
        publics (select-keys publics (for [[k v] publics :when (non-generator-api? k)] k))
        publics (-> publics vals vec)]
    publics))

(def api-endpoints
  nil)

(println api-endpoints)
