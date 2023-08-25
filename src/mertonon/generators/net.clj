(ns mertonon.generators.net
  "Net generation for both testing and demo purposes"
  (:require [clojure.string :as str]
            [clojure.test.check.generators :as gen]
            [mertonon.generators.data :as gen-data]
            [mertonon.generators.params :as net-params]
            [mertonon.models.constructors :as mtc]
            [mertonon.services.graph-service :as gs]
            [mertonon.util.schemas :as mts]
            [schema.core :as s]))

;; ---
;; General utils
;; ---

(defn norm
  "Thinking of the generated table as database-normalized, basically.
  Codd-style normalized, not a linear algebraic norm. Set semantics still avoided for reasons of laziness
  
  Not, like, Haskell-style laziness, Howon-lying-around-eating-potato-chips-style laziness"
  [table]
  (->> table (sort-by :uuid) vec))

;; ---
;; Grid generator
;; ---

(defn generate-grid*
  [{:keys [optimizer-type name-type label-type] :as params}]
  (gen/let [grid-uuid  gen/uuid
            grid-name  (gen-data/gen-grid-names name-type)
            grid-label (gen-data/gen-labels label-type)]
    {:grids [(mtc/->Grid grid-uuid
                         grid-name
                         grid-label
                         optimizer-type
                         {})]}))

(def generate-grid      (generate-grid* net-params/test-gen-params))
(def generate-grid-demo (generate-grid* net-params/demo-gen-params))

;; ---
;; Simpler generators
;; ---

;; Testing use only, do not use for demos. Takes no net gen params because of this

(def generate-simple-layers
  (gen/let [grid           generate-grid
            src-layer-uuid gen/uuid
            tgt-layer-uuid gen/uuid]
    (let [grid-uuid ((first (:grids grid)) :uuid)]
      (assoc grid :layers (norm [(mtc/->Layer src-layer-uuid grid-uuid "first-layer" "trivial")
                                  (mtc/->Layer tgt-layer-uuid grid-uuid "second-layer" "trivial")])))))

(def generate-simple-cost-objects
  (gen/let [layers        generate-simple-layers
            src-cobj-uuid gen/uuid
            tgt-cobj-uuid gen/uuid]
    (let [[src-layer-uuid tgt-layer-uuid] (mapv :uuid (:layers layers))]
      (assoc layers :cost-objects (norm [(mtc/->CostObject src-cobj-uuid src-layer-uuid "first-cobj" "trivial")
                                     (mtc/->CostObject tgt-cobj-uuid tgt-layer-uuid "second-cobj" "trivial")])))))

(def generate-simple-weightsets
  (gen/let [cobjs   generate-simple-cost-objects
            ws-uuid gen/uuid]
    (let [[src-layer-uuid tgt-layer-uuid] (mapv :uuid (:layers cobjs))]
      (assoc cobjs :weightsets [(mtc/->Weightset
                                  ws-uuid
                                  src-layer-uuid
                                  tgt-layer-uuid
                                  "weightset"
                                  "trivial")]))))

(def generate-simple-weights
  (gen/let [weightsets   generate-simple-weightsets
            weight-uuid  gen/uuid
            weight-type  (gen/return :default)
            value        (gen/fmap #(+ 1 %) gen/nat)]
    (let [[src-cobj-uuid tgt-cobj-uuid] (mapv :uuid (:cost-objects weightsets))
          ws-uuid                       ((first (:weightsets weightsets)) :uuid)]
      (assoc weightsets :weights [(mtc/->Weight
                                    weight-uuid
                                    ws-uuid
                                    src-cobj-uuid
                                    tgt-cobj-uuid
                                    "trivial"
                                    weight-type
                                    value)]))))

(def generate-simple-inputs
  (gen/let [weights    generate-simple-weights
            input-uuid gen/uuid]
    (let [layer-uuid ((first (:layers weights)) :uuid)]
      (assoc weights :inputs [(mtc/->Input input-uuid
                                           layer-uuid
                                           "input"
                                           "trivial"
                                           :competitiveness)]))))

(def generate-simple-losses
  (gen/let [inputs    generate-simple-inputs
            loss-uuid gen/uuid]
    (let [layer-uuid ((last (:layers inputs)) :uuid)]
      (assoc inputs :losses [(mtc/->Loss loss-uuid
                                         layer-uuid
                                         "output"
                                         "trivial"
                                         :competitiveness)]))))

(def generate-simple-net generate-simple-losses)

;; ---
;; Linear net generator utils
;; ---

(defn group-by-dependent-uuid 
  "Handles having a foreign key relation in the generation"
  ([record-constructor upstream-uuids partitioned-downstream-uuids]
   (->> (map-indexed (fn [upstream-idx member]
                       (mapv #(record-constructor % (nth upstream-uuids upstream-idx)) member))
                     partitioned-downstream-uuids)
        flatten
        (sort-by :uuid)
        vec))
  ([record-constructor upstream-uuids partitioned-downstream-uuids & partitioned-other-cols]
   (let [init-res (for [partition-idx (into [] (range (count partitioned-downstream-uuids)))
                        :let [curr-upstream-uuid (nth upstream-uuids partition-idx)
                              curr-partition     (nth partitioned-downstream-uuids partition-idx)
                              curr-other-cols    (map #(nth % partition-idx) partitioned-other-cols)]]
                    (for [downstream-idx (into [] (range (count curr-partition)))
                          :let [curr-partition-member (nth curr-partition downstream-idx)
                                curr-args             (into [curr-partition-member curr-upstream-uuid]
                                                            (mapv #(nth % downstream-idx) curr-other-cols))]]
                      (apply record-constructor curr-args)))]
         (->> init-res flatten (sort-by :uuid) vec))))

(defn bigrams [series]
  (partition 2 1 series))

(defn generate-weights-for-weightset
  "Weights cannot have duplicate src and tgt cost-objects. This creates a whole set of weights satisfying condition."
  [weightset src-cost-objects tgt-cost-objects label-type]
  (let [max-num-weights (* (count src-cost-objects) (count tgt-cost-objects))
        src-cobj-uuids  (map :uuid src-cost-objects)
        tgt-cobj-uuids  (map :uuid tgt-cost-objects)]
    ;; Workaround for nested gen/vector-distinct problem
    (let [num-weights   (gen/generate (gen/choose 1 max-num-weights))
          weight-uuids  (gen/generate (gen/vector gen/uuid num-weights))
          weight-vals   (gen/generate (gen/vector (gen/fmap #(+ 1 %) gen/nat) num-weights))
          weight-types  (gen/generate (gen/vector (gen/return :default) num-weights))
          weight-labels (gen/generate (gen/vector (gen-data/gen-labels label-type) num-weights))
          tuples        (gen/tuple (gen/elements src-cobj-uuids) (gen/elements tgt-cobj-uuids))
          distinct-tups (gen/generate (gen/vector-distinct tuples {:num-elements num-weights :max-tries 3000}))]
      (vec (for [[[src-cobj-uuid tgt-cobj-uuid] weight-uuid weight-val weight-type weight-label]
                 (map vector distinct-tups weight-uuids weight-vals weight-types weight-labels)]
             (mtc/->Weight weight-uuid
                           (:uuid weightset)
                           src-cobj-uuid
                           tgt-cobj-uuid
                           weight-label
                           weight-type
                           weight-val))))))

;; ---
;; Linear net generators
;; ---

(defn generate-linear-layers*
  [{:keys [num-layers name-type label-type] :as params}]
  (gen/let [grid         net-gen/generate-
            layer-uuids   (gen/vector gen/uuid num-layers)
            layer-names   (gen/vector (gen-data/gen-layer-names name-type) num-layers)
            layer-labels  (gen/vector (gen-data/gen-labels label-type) num-layers)]
    (let [grid-uuid       ((first (:grids grid)) :uuid)]
      (assoc grid :layers (norm (for [[layer-uuid layer-name layer-label]
                                      (apply map vector [layer-uuids layer-names layer-labels])]
                                  (mtc/->Layer layer-uuid grid-uuid layer-name layer-label)))))))

(def generate-linear-layers      (generate-linear-layers* net-params/test-gen-params))
(def generate-linear-layers-demo (generate-linear-layers* net-params/demo-gen-params))

(defn generate-linear-cost-objects*
  [{:keys [cobjs-per-layer num-layers name-type label-type] :as params}]
  (let [num-cobjs (* num-layers cobjs-per-layer)]
    (gen/let [layers          (generate-linear-layers* params)
              cobj-uuids      (gen/vector gen/uuid num-cobjs)
              cobj-names      (gen/vector (gen-data/gen-cobj-names name-type) num-cobjs)
              cobj-labels     (gen/vector (gen-data/gen-labels label-type) num-cobjs)]
      (let [layer-uuids           (mapv :uuid (:layers layers))
            cost-objects-by-layer (partition cobjs-per-layer cobj-uuids)
            cobj-names-by-layer   (partition cobjs-per-layer cobj-names)
            cobj-labels-by-layer  (partition cobjs-per-layer cobj-labels)
            cobjs                 (group-by-dependent-uuid mtc/->CostObject
                                                           layer-uuids
                                                           cost-objects-by-layer
                                                           cobj-names-by-layer
                                                           cobj-labels-by-layer)]
        (assoc layers :cost-objects cobjs)))))

(def generate-linear-cost-objects      (generate-linear-cost-objects* net-params/test-gen-params))
(def generate-linear-cost-objects-demo (generate-linear-cost-objects* net-params/demo-gen-params))

(defn generate-linear-weightsets*
  [{:keys [num-layers label-type] :as params}]
  (gen/let [cobjs     (generate-linear-cost-objects* params)
            ws-uuids  (gen/vector gen/uuid (- num-layers 1))
            ws-labels (gen/vector (gen-data/gen-labels label-type) (- num-layers 1))]
    (let [layer-bigrams      (bigrams (map :uuid (:layers cobjs)))
          layer-name-bigrams (bigrams (map :name (:layers cobjs)))
          ws-names           (for [[fst snd] layer-name-bigrams]
                               (str/join " => " [fst snd]))
          weightsets         (mapv mtc/->Weightset ws-uuids layer-bigrams ws-names ws-labels)]
      (assoc cobjs :weightsets (norm weightsets)))))

(def generate-linear-weightsets      (generate-linear-weightsets* net-params/test-gen-params))
(def generate-linear-weightsets-demo (generate-linear-weightsets* net-params/demo-gen-params))

(defn generate-linear-weights*
  [{:keys [label-type] :as params}]
  (gen/let [weightsets (generate-linear-weightsets* params)]
    (let [cobjs-by-layer (group-by :layer-uuid (:cost-objects weightsets))
          weights        (for [weightset (:weightsets weightsets)]
                           (let [src-cobjs (cobjs-by-layer (:src-layer-uuid weightset))
                                 tgt-cobjs (cobjs-by-layer (:tgt-layer-uuid weightset))]
                             (norm (generate-weights-for-weightset
                                     weightset
                                     src-cobjs
                                     tgt-cobjs
                                     label-type))))]
      (assoc weightsets :weights (vec (sort-by #(:weightset-uuid (first %)) weights))))))

(def generate-linear-weights      (generate-linear-weights* net-params/test-gen-params))
(def generate-linear-weights-demo (generate-linear-weights* net-params/demo-gen-params))

(defn generate-linear-inputs*
  "Only for linear net, hardcodes the last layer as being the input and competitiveness input"
  [{:keys [label-type loss-type] :as params}]
  (gen/let [weights     (generate-linear-weights* params)
            input-uuid  gen/uuid
            input-label (gen-data/gen-labels label-type)]
    (let [first-layer-uuid (-> weights :layers first :uuid)
          input-name       (str/join ["Input" " (" (-> weights :layers first :name) ")"])
          input            (mtc/->Input input-uuid
                                        first-layer-uuid
                                        input-name
                                        input-label
                                        loss-type)]
      (assoc weights :inputs [input]))))

(def generate-linear-inputs      (generate-linear-inputs* net-params/test-gen-params))
(def generate-linear-inputs-demo (generate-linear-inputs* net-params/demo-gen-params))

(defn generate-linear-losses*
  "Only for linear net, hardcodes the last layer as being the loss and competitiveness loss"
  [{:keys [label-type loss-type] :as params}]
  (gen/let [inputs     (generate-linear-inputs* params)
            loss-uuid  gen/uuid
            loss-label (gen-data/gen-labels label-type)]
    (let [last-layer-uuid (-> inputs :layers last :uuid)
          loss-name       (str/join ["Output" " (" (-> inputs :layers last :name) ")"])
          loss            (mtc/->Loss loss-uuid
                                      last-layer-uuid
                                      loss-name
                                      loss-label
                                      loss-type)]
      (assoc inputs :losses [loss]))))

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
  [layer-uuids num-dag-weightsets]
  (let [num-layers (count layer-uuids)]
    ;; implicit matrix goes src -> tgt
    ;; [idx, idx] would be recurrent links, which we aren't doing yet
    ;; therefore, the possible coords are [idx, idx + 1] to [idx, num-layers]
    ;; linear forward links are [idx, idx + 1] only
    ;; last layer is guaranteed to not have outgoing
    (gen/set
      (gen/let [row (gen/choose 0 (- num-layers 2))
                col (gen/choose (+ row 1) (- num-layers 1))]
        [(nth layer-uuids row)
         (nth layer-uuids col)])
      {:num-elements num-dag-weightsets
       :max-tries    1000})))

;; ---
;; DAG net generators
;; ---

(defn generate-dag-weightsets*
  [{:keys [label-type] :as params}]
  (gen/let [cobjs              (generate-linear-cost-objects* params)
            num-dag-weightsets (gen-num-weightsets cobjs)
            dag-ws-uuids       (gen/vector gen/uuid num-dag-weightsets)
            dag-ws-labels      (gen/vector (gen-data/gen-labels label-type) num-dag-weightsets)
            dag-ws-coords      (gen-adjacency-coords (mapv :uuid (:layers cobjs)) num-dag-weightsets)]
    (let [layers-by-uuid (group-by :uuid (:layers cobjs))
          dag-ws-names   (for [[fst snd] dag-ws-coords]
                          (str/join " => " [(-> fst layers-by-uuid first :name)
                                            (-> snd layers-by-uuid first :name)]))
          dag-weightsets (mapv #(mtc/->Weightset %1 (first %2) (second %2) %3 %4)
                               dag-ws-uuids
                               dag-ws-coords
                               dag-ws-names
                               dag-ws-labels)
          weightsets     (norm dag-weightsets)]
      (assoc cobjs :weightsets (norm weightsets)))))

(def generate-dag-weightsets      (generate-dag-weightsets* net-params/test-gen-params))
(def generate-dag-weightsets-demo (generate-dag-weightsets* net-params/demo-gen-params))

(defn generate-dag-weights*
  [{:keys [label-type] :as params}]
  (gen/let [weightsets (generate-dag-weightsets* params)]
    (let [cobjs-by-layer (group-by :layer-uuid (:cost-objects weightsets))
          weights (for [weightset (:weightsets weightsets)]
                    (let [src-cobjs (cobjs-by-layer (:src-layer-uuid weightset))
                          tgt-cobjs (cobjs-by-layer (:tgt-layer-uuid weightset))]
                      (norm (generate-weights-for-weightset weightset src-cobjs tgt-cobjs label-type))))]
      (assoc weightsets :weights (vec (sort-by #(:weightset-uuid (first %)) weights))))))

(def generate-dag-weights      (generate-dag-weights* net-params/test-gen-params))
(def generate-dag-weights-demo (generate-dag-weights* net-params/demo-gen-params))

(defn generate-dag-inputs*
  [{:keys [label-type loss-type] :as params}]
  (gen/let [weights (generate-dag-weights* params)]
    (let [graph               (gs/net->graph (:layers weights) (:weightsets weights))
          initial-layer-uuids (gs/initial-layer-uuids graph)
          layers-by-uuid      (group-by :uuid (:layers weights))]
      (gen/let [input-uuids  (gen/vector gen/uuid (count initial-layer-uuids))
                input-labels (gen/vector (gen-data/gen-labels label-type) (count initial-layer-uuids))]
        (let [inputs (for [idx (range (count input-uuids))
                           :let [input-uuid  (nth input-uuids idx)
                                 layer-uuid  (nth initial-layer-uuids idx)
                                 input-name  (str/join
                                               ["Input" " (" (-> (layers-by-uuid layer-uuid) first :name) ")"])
                                 input-label (nth input-labels idx)
                                 input-type  loss-type]]
                       (mtc/->Input input-uuid layer-uuid input-name input-label input-type))]
          (assoc weights :inputs (norm inputs)))))))

(def generate-dag-inputs      (generate-dag-inputs* net-params/test-gen-params))
(def generate-dag-inputs-demo (generate-dag-inputs* net-params/demo-gen-params))

(defn generate-dag-losses*
  [{:keys [label-type loss-type] :as params}]
  (gen/let [inputs (generate-dag-inputs* params)]
    (let [graph                (gs/net->graph (:layers inputs) (:weightsets inputs))
          terminal-layer-uuids (gs/terminal-layer-uuids graph)
          layers-by-uuid       (group-by :uuid (:layers inputs))]
      (gen/let [loss-uuids  (gen/vector gen/uuid (count terminal-layer-uuids))
                loss-labels (gen/vector (gen-data/gen-labels label-type) (count terminal-layer-uuids))]
        (let [losses (for [idx (range (count loss-uuids))
                           :let [loss-uuid  (nth loss-uuids idx)
                                 layer-uuid (nth terminal-layer-uuids idx)
                                 loss-name  (str/join
                                               ["Output" " (" (-> (layers-by-uuid layer-uuid) first :name) ")"])
                                 loss-label (nth loss-labels idx)
                                 loss-type  loss-type]]
                       (mtc/->Loss loss-uuid layer-uuid loss-name loss-label loss-type))]
          (assoc inputs :losses (norm losses)))))))

(def generate-dag-losses      (generate-dag-losses* net-params/test-gen-params))
(def generate-dag-losses-demo (generate-dag-losses* net-params/demo-gen-params))

;; TODO: get consistent shapes for net weights to get persistent vectors nice

(def generate-dag-net generate-dag-losses)

(def generate-dag-demo-net generate-dag-losses-demo)

(comment
  (gen/generate generate-dag-demo-net))
