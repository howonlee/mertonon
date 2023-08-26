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
        init-cobj-uuids       (->> (for [layer-uuid initial-layers] (cost-objects-by-layer layer-uuid))
                                   flatten
                                   vec
                                   (map :uuid))
        term-cobj-uuids       (->> (for [layer-uuid terminal-layers] (cost-objects-by-layer layer-uuid))
                                   flatten
                                   vec
                                   (map :uuid))]
    (gen/let [entries-per-cost-object (gen/choose 2 8)
              total-num-inits         (gen/return (* (count init-cobj-uuids) entries-per-cost-object))
              init-entry-uuids        (gen/vector gen/uuid total-num-inits)
              init-entry-names        (gen/vector (gen-data/gen-entry-names name-type) total-num-inits)
              init-entry-labels       (gen/vector (gen-data/gen-labels label-type) total-num-inits)
              init-entry-types        (gen/vector (gen/return :abstract.arbitrary.value) total-num-inits)
              init-entry-vals         (gen/vector entry-val-gen total-num-inits)

              total-num-terms         (gen/return (* (count term-cobj-uuids) entries-per-cost-object))
              term-entry-uuids        (gen/vector gen/uuid total-num-terms)
              term-entry-names        (gen/vector (gen-data/gen-entry-names name-type) total-num-inits)
              term-entry-labels       (gen/vector (gen-data/gen-labels label-type) total-num-inits)
              term-entry-types        (gen/vector (gen/return :abstract.arbitrary.value) total-num-terms)
              term-entry-vals         (gen/vector entry-val-gen total-num-terms)]
      (let [init-entries-by-cobj  (partition entries-per-cost-object init-entry-uuids)
            init-entry-names      (partition entries-per-cost-object init-entry-names)
            init-entry-labels     (partition entries-per-cost-object init-entry-labels)
            init-entry-types      (partition entries-per-cost-object init-entry-types)
            init-entry-vals       (partition entries-per-cost-object init-entry-vals)

            term-entries-by-cobj  (partition entries-per-cost-object term-entry-uuids)
            term-entry-names      (partition entries-per-cost-object term-entry-names)
            term-entry-labels     (partition entries-per-cost-object term-entry-labels)
            term-entry-types      (partition entries-per-cost-object term-entry-types)
            term-entry-vals       (partition entries-per-cost-object term-entry-vals)

            ;; TODO: also generate entry dates

            init-entries          (net-gen/group-by-dependent-uuid mtc/->Entry
                                                                   init-cobj-uuids
                                                                   init-entries-by-cobj
                                                                   init-entry-names
                                                                   init-entry-labels
                                                                   init-entry-types
                                                                   init-entry-vals)
            term-entries          (net-gen/group-by-dependent-uuid mtc/->Entry
                                                                   term-cobj-uuids
                                                                   term-entries-by-cobj
                                                                   term-entry-names
                                                                   term-entry-labels
                                                                   term-entry-types
                                                                   term-entry-vals)]
        {:entries (vec (into init-entries term-entries))}))))

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

