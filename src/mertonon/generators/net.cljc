(ns mertonon.generators.net
  "Net generation for both testing and demo purposes"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.params :as net-params]
            [mertonon.models.constructors :as mtc]
            [mertonon.services.graph-service :as gs]))

;; ---
;; General utils
;; ---

(defn norm
  "Thinking of the generated table as database-normalized, basically.
  Codd-style normalized, not a linear algebraic norm. Set semantics still avoided for reasons of laziness
  
  Not, like, Haskell-style laziness, Howon-lying-around-eating-potato-chips-style laziness"
  [table]
  (->> table (sort-by :uuid) vec))

(defn bigrams [series]
  (partition 2 1 series))

;; ---
;; Individual table row gens
;; ---

(defn gen-grid-row
  [{:keys [optimizer-type name-type label-type] :as params}]
  (gen/let [grid-uuid  gen/uuid
            grid-name  (gen-data/gen-grid-names name-type)
            grid-label (gen-data/gen-labels label-type)]
    (mtc/->Grid grid-uuid grid-name grid-label optimizer-type {})))

(defn gen-layer-row
  [{:keys [name-type label-type] :as params} grid]
  (gen/let [layer-uuid   gen/uuid
            layer-name   (gen-data/gen-layer-names name-type)
            layer-label  (gen-data/gen-labels label-type)]
    (mtc/->Layer layer-uuid (grid :uuid) layer-name layer-label)))

(defn gen-cobj-row
  [{:keys [name-type label-type] :as params} layer]
  (gen/let [cobj-uuid  gen/uuid
            cobj-name  (gen-data/gen-cobj-names name-type)
            cobj-label (gen-data/gen-labels label-type)]
    (mtc/->CostObject cobj-uuid (layer :uuid) cobj-name cobj-label)))

(defn gen-weightset-row
  [{:keys [name-type label-type] :as params} src-layer tgt-layer]
  (gen/let [ws-uuid  gen/uuid
            ws-label (gen-data/gen-labels label-type)]
    (let [ws-name  (str/join " => " [(src-layer :name) (tgt-layer :name)])]
      (mtc/->Weightset ws-uuid (src-layer :uuid) (tgt-layer :uuid) ws-name ws-label))))

(defn gen-weight-row
  [{:keys [name-type label-type] :as params} weightset src-cobj tgt-cobj]
  (gen/let [weight-uuid  gen/uuid
            weight-val   (gen/fmap #(+ 1 %) gen/nat)
            weight-type  (gen/return :default)
            weight-label (gen-data/gen-labels label-type)]
    (mtc/->Weight weight-uuid
                  (weightset :uuid)
                  (src-cobj :uuid)
                  (tgt-cobj :uuid)
                  weight-label
                  weight-type
                  weight-val)))

(defn gen-weight-rows
  "Give weights to fill up a weightset's worth of rows, given adjacent source and target cobj vecs"
  [params weightset src-cobjs tgt-cobjs]
  (gen/let [num-weights   (gen/choose 1 (* (count src-cobjs) (count tgt-cobjs)))
            distinct-tups (gen/vector-distinct
                            (gen/tuple (gen/elements src-cobjs) (gen/elements tgt-cobjs))
                            {:num-elements num-weights :max-tries 3000})
            new-weights   (apply gen/tuple
                                 (map (fn [[src-cobj tgt-cobj]] (gen-weight-row params weightset src-cobj tgt-cobj))
                                      distinct-tups))]
    (flatten new-weights)))

(defn gen-input-row
  [{:keys [name-type label-type] :as params} layer]
  (gen/let [input-uuid  gen/uuid
            input-label (gen-data/gen-labels label-type)]
    (mtc/->Input input-uuid (layer :uuid) "Input" input-label :conformance)))

(defn gen-loss-row
  [{:keys [name-type label-type] :as params} layer]
  (gen/let [loss-uuid  gen/uuid
            loss-label (gen-data/gen-labels label-type)]
    (mtc/->Input loss-uuid (layer :uuid) "Output" loss-label :conformance)))

;; ---
;; Grid generator
;; ---

(defn generate-grid*
  [params]
  (gen/let [grid (gen-grid-row params)]
    {:grids [grid]}))

(def generate-grid      (generate-grid* net-params/test-gen-params))
(def generate-grid-demo (generate-grid* net-params/demo-gen-params))

;; ---
;; Simpler generators
;; ---

;; Testing use only, do not use for demos.

(def simple-params net-params/test-gen-params)

(def generate-simple-layers
  (gen/let [grid      generate-grid
            src-layer (gen-layer-row simple-params (-> grid :grids first))
            tgt-layer (gen-layer-row simple-params (-> grid :grids first))]
    (assoc grid :layers (norm [src-layer tgt-layer]))))

(def generate-simple-cost-objects
  (gen/let [layers   generate-simple-layers
            src-cobj (gen-cobj-row simple-params (-> layers :layers first))
            tgt-cobj (gen-cobj-row simple-params (-> layers :layers second))]
    (assoc layers :cost-objects (norm [src-cobj tgt-cobj]))))

(def generate-simple-weightsets
  (gen/let [cobjs generate-simple-cost-objects
            ws    (gen-weightset-row simple-params (-> cobjs :layers first) (-> cobjs :layers second))]
    (assoc cobjs :weightsets [ws])))

(def generate-simple-weights
  (gen/let [weightsets generate-simple-weightsets
            weight     (gen-weight-row simple-params
                                       (-> weightsets :weightsets first)
                                       (-> weightsets :cost-objects first)
                                       (-> weightsets :cost-objects second))]
    (assoc weightsets :weights [[weight]])))

(def generate-simple-inputs
  (gen/let [weights generate-simple-weights
            input   (gen-input-row simple-params (-> weights :layers first))]
    (assoc weights :inputs [input])))

(def generate-simple-losses
  (gen/let [inputs generate-simple-inputs
            loss   (gen-loss-row simple-params (-> inputs :layers last))]
    (assoc inputs :losses [loss])))

(def generate-simple-net generate-simple-losses)

;; ---
;; Linear net generators
;; ---

(defn generate-linear-layers*
  [{:keys [num-layers name-type label-type] :as params}]
  (gen/let [grid          (generate-grid* params)
            layers        (gen/vector (gen-layer-row params (-> grid :grids first)) num-layers)]
    (assoc grid :layers (norm layers))))

(def generate-linear-layers      (generate-linear-layers* net-params/test-gen-params))
(def generate-linear-layers-demo (generate-linear-layers* net-params/demo-gen-params))

(defn generate-linear-cost-objects*
  [{:keys [cobjs-per-layer num-layers name-type label-type] :as params}]
  (gen/let [layers (generate-linear-layers* params)
            cobjs  (apply gen/tuple
                          (map #(gen/vector (gen-cobj-row params %) cobjs-per-layer)
                               (layers :layers)))]
    (assoc layers :cost-objects (norm (flatten cobjs)))))

(def generate-linear-cost-objects      (generate-linear-cost-objects* net-params/test-gen-params))
(def generate-linear-cost-objects-demo (generate-linear-cost-objects* net-params/demo-gen-params))

(defn generate-linear-weightsets*
  [{:keys [num-layers label-type] :as params}]
  (gen/let [cobjs      (generate-linear-cost-objects* params)
            weightsets (apply gen/tuple
                              (map (fn [[src tgt]] (gen-weightset-row params src tgt))
                                   (bigrams (:layers cobjs))))]
      (assoc cobjs :weightsets (norm weightsets))))

(def generate-linear-weightsets      (generate-linear-weightsets* net-params/test-gen-params))
(def generate-linear-weightsets-demo (generate-linear-weightsets* net-params/demo-gen-params))

(defn generate-linear-weights*
  [{:keys [label-type] :as params}]
  (gen/let [weightsets     (generate-linear-weightsets* params)
            cobjs-by-layer (gen/return (group-by :layer-uuid (:cost-objects weightsets)))
            weight-cobjs   (gen/return (vec (for [weightset (:weightsets weightsets)]
                                              [weightset
                                               (cobjs-by-layer (:src-layer-uuid weightset))
                                               (cobjs-by-layer (:tgt-layer-uuid weightset))])))
            weights        (apply gen/tuple
                                  (map (fn [[weightset src-cobjs tgt-cobjs]]
                                         (gen-weight-rows params weightset src-cobjs tgt-cobjs))
                                       weight-cobjs))]
      (assoc weightsets :weights (-> weights vec flatten norm))))

(def generate-linear-weights      (generate-linear-weights* net-params/test-gen-params))
(def generate-linear-weights-demo (generate-linear-weights* net-params/demo-gen-params))

(defn generate-linear-inputs*
  "Only for linear net, hardcodes the first layer as being the input and conformance input"
  [params]
  (gen/let [weights (generate-linear-weights* params)
            input   (gen-input-row params (-> weights :layers first))]
    (assoc weights :inputs [input])))

(def generate-linear-inputs      (generate-linear-inputs* net-params/test-gen-params))
(def generate-linear-inputs-demo (generate-linear-inputs* net-params/demo-gen-params))

(defn generate-linear-losses*
  "Only for linear net, hardcodes the last layer as being the loss and conformance loss"
  [params]
  (gen/let [inputs  (generate-linear-inputs* params)
            loss    (gen-loss-row params (-> inputs :layers last))]
    (assoc inputs :losses [loss])))

(def generate-linear-losses      (generate-linear-losses* net-params/test-gen-params))
(def generate-linear-losses-demo (generate-linear-losses* net-params/demo-gen-params))

(def generate-linear-net generate-linear-losses)

(def generate-matrix-weights
  "Take from a whole net one weightset for matrix testing"
  (gen/let [net generate-linear-net]
    (let [cost-objects-by-layer     (->> net
                                     :cost-objects
                                     (sort-by :uuid)
                                     (group-by :layer-uuid))
          weights-by-weightsets (->> net
                                     :weights
                                     flatten
                                     (sort-by :uuid)
                                     (group-by :weightset-uuid))
          in-weightset          (first (:weightsets net))
          in-src-cobjs          (cost-objects-by-layer (:src-layer-uuid in-weightset))
          in-tgt-cobjs          (cost-objects-by-layer (:tgt-layer-uuid in-weightset))
          in-weights            (weights-by-weightsets (:uuid in-weightset))]
      {:weightset in-weightset
       :weights   in-weights
       :src-cobjs in-src-cobjs
       :tgt-cobjs in-tgt-cobjs})))

;; ---
;; DAG net generator utils
;; ---

(defn gen-num-weightsets [cost-objects]
  (let [num-layers (count (:layers cost-objects))]
    (gen/choose num-layers (/ (* num-layers (- num-layers 1)) 2))))

(defn gen-adjacency-coords
  "Generate adjacency matrix coordinates for a DAG network. Each edge here is a whole weightset, not a weight.
  We need every layer in the net to be reachable so we create a
  linear net first then fill it up with more weightsets

  This stack overflow thing is of use:
  https://stackoverflow.com/questions/12790337/generating-a-random-dag"
  [layers num-dag-weightsets]
  (let [num-layers (count layers)]
    ;; implicit matrix goes src -> tgt
    ;; [idx, idx] would be recurrent links, which we aren't doing yet
    ;; therefore, the possible coords are [idx, idx + 1] to [idx, num-layers]
    ;; linear forward links are [idx, idx + 1] only
    ;; last layer is guaranteed to not have outgoing
    (gen/set
      (gen/let [row (gen/choose 0 (- num-layers 2))
                col (gen/choose (+ row 1) (- num-layers 1))]
        [(nth layers row)
         (nth layers col)])
      {:num-elements num-dag-weightsets
       :max-tries    1000})))

;; ---
;; DAG net generators
;; ---

(defn generate-dag-weightsets*
  [{:keys [label-type] :as params}]
  (gen/let [cobjs              (generate-linear-cost-objects* params)
            num-dag-weightsets (gen-num-weightsets cobjs)
            dag-ws-coords      (gen-adjacency-coords (:layers cobjs) num-dag-weightsets)
            dag-weightsets     (apply gen/tuple
                                      (map (fn [[src tgt]] (gen-weightset-row params src tgt))
                                           dag-ws-coords))]
      (assoc cobjs :weightsets (norm dag-weightsets))))

(def generate-dag-weightsets      (generate-dag-weightsets* net-params/test-gen-params))
(def generate-dag-weightsets-demo (generate-dag-weightsets* net-params/demo-gen-params))

(defn generate-dag-weights*
  [{:keys [label-type] :as params}]
  (gen/let [weightsets     (generate-dag-weightsets* params)
            cobjs-by-layer (gen/return (group-by :layer-uuid (:cost-objects weightsets)))
            weight-cobjs   (gen/return (vec (for [weightset (:weightsets weightsets)]
                                              [weightset
                                               (cobjs-by-layer (:src-layer-uuid weightset))
                                               (cobjs-by-layer (:tgt-layer-uuid weightset))])))
            weights        (apply gen/tuple
                                  (map (fn [[weightset src-cobjs tgt-cobjs]]
                                         (gen-weight-rows params weightset src-cobjs tgt-cobjs))
                                       weight-cobjs))]
    (assoc weightsets :weights (-> weights vec flatten norm))))

(def generate-dag-weights      (generate-dag-weights* net-params/test-gen-params))
(def generate-dag-weights-demo (generate-dag-weights* net-params/demo-gen-params))

(defn generate-dag-inputs*
  [{:keys [label-type loss-type] :as params}]
  (gen/let [weights (generate-dag-weights* params)]
    (let [graph               (gs/net->graph (:layers weights) (:weightsets weights))
          initial-layer-uuids (gs/initial-layer-uuids graph)
          layers-by-uuid      (group-by :uuid (:layers weights))
          initial-layers      (flatten (mapv layers-by-uuid initial-layer-uuids))]
      (gen/let [inputs (apply gen/tuple (map (fn [layer] (gen-input-row params layer)) initial-layers))]
        (assoc weights :inputs (norm inputs))))))

(def generate-dag-inputs      (generate-dag-inputs* net-params/test-gen-params))
(def generate-dag-inputs-demo (generate-dag-inputs* net-params/demo-gen-params))

(defn generate-dag-losses*
  [{:keys [label-type loss-type] :as params}]
  (gen/let [inputs (generate-dag-inputs* params)]
    (let [graph                (gs/net->graph (:layers inputs) (:weightsets inputs))
          terminal-layer-uuids (gs/terminal-layer-uuids graph)
          layers-by-uuid       (group-by :uuid (:layers inputs))
          terminal-layers      (flatten (mapv layers-by-uuid terminal-layer-uuids))]
      (gen/let [losses (apply gen/tuple (map (fn [layer] (gen-loss-row params layer)) terminal-layers))]
        (assoc inputs :losses (norm losses))))))

(def generate-dag-losses      (generate-dag-losses* net-params/test-gen-params))
(def generate-dag-losses-demo (generate-dag-losses* net-params/demo-gen-params))

;; TODO: get consistent shapes for net weights to get persistent vectors nice

(def generate-dag-net generate-dag-losses)

(def generate-dag-demo-net generate-dag-losses-demo)

(comment
  (gen/generate generate-dag-net))
