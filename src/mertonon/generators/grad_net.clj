(ns mertonon.generators.grad-net
  "Augmentation of generated net with gradient stuff"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.net :as net-gen]
            [mertonon.generators.params :as gen-params]
            [mertonon.models.constructors :as mtc]
            [mertonon.services.grad-service :as grad]
            [mertonon.services.graph-service :as gs]
            [mertonon.services.matrix-service :as ms]))

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
