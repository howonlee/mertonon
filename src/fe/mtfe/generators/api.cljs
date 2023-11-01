(ns mtfe.generators.api
  "Generate FE api endpoints"
  (:require [clojure.test.check.generators :as gen]
            [mtfe.api :as api]
            [mtfe.generators.net-store :as net-store]))

(defn valid-api? [k]
  (and
    (not= (name k) "host")
    (not (clojure.string/includes? (name k) "generator"))))

(def api-endpoint-fns
  (let [publics (ns-publics 'mtfe.api)
        publics (select-keys publics (for [[k v] publics :when (valid-api? k)] k))
        publics (-> publics vals vec)]
    publics))

(def gen-api-endpoints
  (gen/let [endpoint-fn (gen/elements api-endpoint-fns)
            curr-idx    (gen/choose 0 10)]
    (let [curr-meta (meta endpoint-fn)
          arity     (-> curr-meta :arglists first count)]
      (condp = arity
        0 (endpoint-fn)
        1 (endpoint-fn (-> (@net-store/store (curr-meta :table)) cycle (nth curr-idx) :uuid))
        (str "currently-bad arity")))))

(comment
  (try
    (cljs.pprint/pprint (gen/generate gen-api-endpoints))
    (catch :default e (cljs.pprint/pprint e))))
