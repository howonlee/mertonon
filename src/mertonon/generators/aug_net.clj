(ns mertonon.generators.aug-net
  "Augmentation of generated net with entries, matrix form, forward pass, backward pass, whatever."
  (:require [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.net :as net-gen]
            [mertonon.generators.params :as gen-params]
            [mertonon.models.constructors :as mtc]
            [mertonon.services.grad-service :as grad]
            [mertonon.services.graph-service :as gs]
            [mertonon.services.matrix-service :as ms]
            [mertonon.util.schemas :as mts]
            [schema.core :as s]))

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

(def dag-demo-net-and-entries
  "Used for demo"
  (gen/let [dag-net net-gen/generate-dag-demo-net
            entries (generate-net-entries dag-net gen-params/demo-gen-params)]
    [dag-net entries]))

;; ---
;; Generate the matrix net (so net enriched with matrix)
;; ---

(defn matrix-net-and-patterns
  [net-and-entries-gen]
  (gen/let [[net entries] net-and-entries-gen]
    (let [matrix-net                  (ms/row-net->matrix-net net)
          {cost-objects :cost-objects
           layers       :layers
           weightsets   :weightsets}  matrix-net
          cobjs-by-layer              (group-by :layer-uuid cost-objects)
          graph                       (gs/net->graph layers weightsets)
          initial-uuids               (gs/initial-layer-uuids graph)
          terminal-uuids              (gs/terminal-layer-uuids graph)
          patterns                    (ms/entries->patterns
                                        {:entries              (sort-by :cobj-uuid (entries :entries))
                                         :pattern-layer-uuids  (into initial-uuids terminal-uuids)
                                         :cost-objects         cost-objects})]
      [matrix-net patterns])))

;; ---
;; Generate the full net, forward pass run, backward pass run and all
;; ---

(defn net-and-backprop
  [net-and-entries-gen]
  (gen/let [[matrix-net patterns] (matrix-net-and-patterns net-and-entries-gen)]
    (let [forward  (grad/net-patterns->forward-pass matrix-net patterns)
          backward (grad/forward-pass->grad forward)]
      [matrix-net patterns forward backward])))

;; ---
;; Generate the full net, with the cost objects' deltas and activations, and weights' gradients
;; in separate to-update maps which we won't be using to actually update because we don't want it to take a thousand years
;; ---

(defn net-and-backprop-and-updates
  [net-and-entries-gen]
  (gen/let [[matrix-net patterns forward backward] (net-and-backprop net-and-entries-gen)]
    (let [{:keys [cobj-updates
                  weight-updates]} (grad/grad->to-update backward)
          cobj-update-index        (into {} cobj-updates)
          weight-update-index      (into {} weight-updates)]
      [matrix-net patterns forward backward cobj-update-index weight-update-index])))

(comment
  (-> net-gen/generate-linear-net
      gen/generate
      (generate-net-entries gen-params/test-gen-params)
      gen/generate)
  (-> matrix-net-and-patterns
      gen/generate))
