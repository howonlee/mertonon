(ns mertonon.generators.aug-net
  "Augmentation of generated net with entries, matrix form only"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.net :as net-gen]
            [mertonon.generators.params :as gen-params]
            [mertonon.models.constructors :as mtc]
            [mertonon.services.graph-service :as gs]))

(def entry-val-gen
  "Entry to be distributed ~ power law.

  Power law distribution calc from
  https://stats.stackexchange.com/questions/173242/random-sample-from-power-law-distribution"
  (let [x-min               1
        power-law-transform (fn [member]
                              (bigdec (Math/floor
                                        (* x-min
                                           (Math/pow
                                             (- 1 member)
                                             (/ (- 1) 1))))))]
    (gen/fmap power-law-transform (gen/double* {:infinite? false :NaN? false :min 0.0 :max 0.99}))))

;; ---
;; Individual table row gens
;; ---

(defn gen-entry-row
  [{:keys [name-type label-type] :as params} cobj]
  (gen/let [entry-uuid  gen/uuid
            entry-name  (gen-data/gen-entry-names name-type)
            entry-label (gen-data/gen-labels label-type)
            entry-type  (gen/return :abstract.arbitrary.value)
            entry-val   entry-val-gen]
    (mtc/->Entry entry-uuid (cobj :uuid) entry-name entry-label entry-type entry-val)))

;; ---
;; Generate with respect to net
;; ---

(defn generate-net-entries
  "Given a network, generate some entries for its cost-objects"
  [net {:keys [label-type name-type] :as params}]
  (let [layers                (:layers net)
        cost-objects          (:cost-objects net)
        weightsets            (:weightsets net)
        cost-objects-by-layer (group-by :layer-uuid cost-objects)
        net-graph             (gs/net->graph layers weightsets)
        initial-layers        (gs/initial-layer-uuids net-graph)
        terminal-layers       (gs/terminal-layer-uuids net-graph)
        init-cobjs            (->> (for [layer-uuid initial-layers] (cost-objects-by-layer layer-uuid))
                                   flatten
                                   vec)
        term-cobjs            (->> (for [layer-uuid terminal-layers] (cost-objects-by-layer layer-uuid))
                                   flatten
                                   vec)]
    (gen/let [entries-per-cobj (gen/choose 2 8)
              init-entries     (apply gen/tuple (map #(gen/vector (gen-entry-row params %) entries-per-cobj) init-cobjs))
              term-entries     (apply gen/tuple (map #(gen/vector (gen-entry-row params %) entries-per-cobj) term-cobjs))]
      {:entries (-> (into init-entries term-entries) flatten vec net-gen/norm)})))

(def net-and-entries
  (gen/let [net     net-gen/generate-linear-net
            entries (generate-net-entries net gen-params/test-gen-params)]
    [net entries]))

(def net-enriched-with-entries
  "Used for model testing, keep it small"
  (gen/let [net     net-gen/generate-simple-net
            entries (generate-net-entries net gen-params/test-gen-params)]
    (merge net entries)))

(def dag-net-and-entries
  "Used for examples and other stuff"
  (gen/let [dag-net net-gen/generate-dag-net
            entries (generate-net-entries dag-net gen-params/test-gen-params)]
    [dag-net entries]))

(def merged-dag-net-and-entries
  (gen/let [dag-net net-gen/generate-dag-net
            entries (generate-net-entries dag-net gen-params/test-gen-params)]
    (merge dag-net entries)))

(def dag-demo-net-and-entries
  "Used for demo"
  (gen/let [dag-net net-gen/generate-dag-demo-net
            entries (generate-net-entries dag-net gen-params/demo-gen-params)]
    [dag-net entries]))

(comment (gen/generate net-and-entries))
