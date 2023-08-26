(ns mertonon.services.grad-service
  "Construct the operator DAG and actually enact the gradient finding
  with respect to a network. Autodiff does this for arbitrary functions.
  Uses the autodiff module"
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmo]
            [loom.alg :as graph-alg]
            [mertonon.autodiff.grad :as grad]
            [mertonon.autodiff.reverse-ops :as ops]
            [mertonon.autodiff.util :as autodiff-util]
            [mertonon.models.cost-object :as cost-object-model]
            [mertonon.models.weight :as weight-model]
            [mertonon.services.matrix-service :as m]
            [mertonon.services.graph-service :as gs]
            [mertonon.util.io :as uio]
            [mertonon.util.schemas :as mts]
            [schema.core :as s]))

;; TODO: Grad types

;; ---
;; Things that should be config one day
;; ---

(def default-learning-rate 0.025)

(def default-reg-val 1e-4)

;; ---
;; Matrix with metadata <-> Matrix autodiff variable
;; ---

(s/defn matrix->matrix-var [matrix opts]
  (let [metadata   (select-keys matrix [:src-cobj-labels :src-cobjs
                                        :tgt-cobj-labels :tgt-cobjs
                                        :weight-labels :weightset])
        curr-mat   (cmo/+ (:matrix matrix) default-reg-val)
        matrix-val (if (:transpose opts)
                     (cm/transpose curr-mat)
                     curr-mat)]
    (ops/op-var matrix-val metadata)))

(s/defn matrix-var->matrix [matrix-var opts]
  (let [metadata   (:metadata matrix-var)
        matrix-val (if (:transpose opts)
                     (cm/transpose (:value matrix-var))
                     (:value matrix-var))
        matrix-val (cmo/- matrix-val default-reg-val)]
    (assoc metadata :matrix matrix-val)))

(defn terminal-pattern->target-var
  [terminal-pattern layer-uuid]
  (ops/op-norm-1d (ops/op-var terminal-pattern layer-uuid)))


;; ---
;; Matrix net and patterns <-> Forward pass
;; ---

(defn net-patterns->forward-pass
  "Given a matrix network and patterns,
  apply the patterns for the forward pass for the net"
  [{:keys [matrices losses inputs layers weightsets] :as matrix-net}
   {:keys [layer->patterns] :as patterns}]
  (let [graph                (gs/net->graph layers weightsets)

        init-layer-uuids     (gs/initial-layer-uuids graph)
        terminal-layer-uuids (gs/terminal-layer-uuids graph)

        init-patterns        (for [uuid init-layer-uuids] [uuid (layer->patterns uuid)])
        terminal-patterns    (for [uuid terminal-layer-uuids] [uuid (layer->patterns uuid)])
        init-vars            (for [[layer-uuid init-pattern] init-patterns] (ops/op-var init-pattern layer-uuid))
        ;; Target var meaning, var that holds a target output pattern
        ;; There are an arbitrary number of target vars! So reverse-mode autodiff will scale in time
        ;; based on their cardinality and number of nodes they have!
        target-vars          (for [[layer-uuid terminal-pattern] terminal-patterns] (terminal-pattern->target-var terminal-pattern layer-uuid))
        target-var-map       (into {} (for [[layer-uuid terminal-pattern] terminal-patterns] [layer-uuid (terminal-pattern->target-var terminal-pattern layer-uuid)]))
        matrix-by-weightset  (group-by #(get-in % [:weightset :uuid]) matrices)
        weightset-by-tgt     (group-by :tgt-layer-uuid (:weightsets matrix-net))

        topo-ordering        (graph-alg/topsort graph)

        forward-res          (loop [curr-layer-uuids topo-ordering
                                    layer-uuid->var  (group-by :metadata init-vars)]
                               (let [weightsets          (weightset-by-tgt (first curr-layer-uuids))
                                     ;; Ignore if weightsets are nil (e.g., if there's nothing going to this layer)
                                     ;; TODO: whack this into its own function
                                     new-var             (apply ops/op-sum
                                                                (for [weightset weightsets
                                                                      :let
                                                                      [mat-var       (matrix->matrix-var
                                                                                       (first (matrix-by-weightset (:uuid weightset)))
                                                                                       {:transpose true})
                                                                       src-layer-var (first (layer-uuid->var (:src-layer-uuid weightset)))
                                                                       mul-var       (ops/op-mmul (ops/op-norm-2d mat-var) (ops/op-norm-1d src-layer-var))]]
                                                                  mul-var))
                                     new-layer-uuid->var (if (seq weightsets)
                                                           (assoc layer-uuid->var (first curr-layer-uuids) [new-var])
                                                           layer-uuid->var)]
                                 (if (seq curr-layer-uuids)
                                   (recur (rest curr-layer-uuids)
                                          new-layer-uuid->var)
                                   new-layer-uuid->var)))
        ;; map of _just_ the final vars
        final-var-map        (into {} (for [uuid terminal-layer-uuids] [uuid (forward-res uuid)]))

        ;; | || || |_
        losses-by-layer      (group-by :layer-uuid losses)
        loss-reses           (vec (for [[layer-uuid [final-var]] final-var-map
                                        :let [target-var (target-var-map layer-uuid)
                                              loss       (first (losses-by-layer layer-uuid))]]
                                    (grad/loss-grad-function
                                      {:curr-loss  loss
                                       :losses     losses
                                       :inputs     inputs
                                       :patterns   patterns
                                       :final-var  final-var
                                       :target-var target-var})))

        partial-net          (select-keys matrix-net
                                          [:grids :layers :cost-objects :inputs :losses :weights :weightsets])
        partial-patterns     (select-keys patterns
                                          [:cobj->entries :layer->cobj :entries])]
    (merge partial-net partial-patterns {:vars       forward-res
                                         :final-vars final-var-map
                                         :targets    target-vars
                                         :loss-reses loss-reses})))

(defn forward-pass->net-patterns
  [forward-pass]
  (let [partial-net      (select-keys forward-pass
                                      [:grids :layers :cost-objects :inputs
                                       :losses :weights :weightsets])
        partial-patterns (select-keys forward-pass
                                      [:cobj->entries :layer->cobj :entries])
        forward-res      (forward-pass :vars)
        final-vars       (forward-pass :final-vars)
        ops-seq          (->> final-vars
                              vals
                              (apply concat)
                              (map autodiff-util/ops-seq)
                              (apply concat))

        graph            (gs/net->graph (:layers partial-net) (:weightsets partial-net))
        init-layer-uuids (apply hash-set (gs/initial-layer-uuids graph))

        [init-vars mats] (loop [curr-init-vars []
                                curr-mats      []
                                curr-ops-seq    ops-seq]
                           (let [curr-var       (first curr-ops-seq)
                                 maybe-init-var (if (contains? init-layer-uuids (:metadata curr-var))
                                                  curr-var
                                                  nil)
                                 maybe-mat      (if (get-in curr-var [:metadata :weightset :uuid])
                                                  (matrix-var->matrix curr-var {:transpose true})
                                                  nil)]
                             (if (seq curr-ops-seq)
                               (recur (conj curr-init-vars maybe-init-var)
                                      (conj curr-mats maybe-mat)
                                      (rest curr-ops-seq))
                               [(->> curr-init-vars (remove nil?) distinct vec)
                                (->> curr-mats (remove nil?) distinct vec)])))

        sorted-mats      (sort-by #(get-in % [:weightset :uuid]) mats)
        target-vars      (:targets forward-pass)
        ;; react out from the op-norm and get the original op-var uuid
        out-patterns     (into {} (for [target-var target-vars] [(->> target-var :inputs first :metadata) (->> target-var :inputs first :value)]))
        in-patterns      (into {} (for [init-var init-vars] [(:metadata init-var) (:value init-var)]))

        layer->patterns  (merge in-patterns out-patterns)]
    ;; TODO: deal with the normalization a better way than ignoring it, make it conform to profit
    [(merge partial-net {:matrices sorted-mats})
     (merge partial-patterns {:layer->patterns layer->patterns})]))

;; ---
;; Forward pass -> Gradients
;; Note that the actual people are to adjust their patterns of working to the gradient (or not)
;; So gradient application is _peep-mediated_
;; Gradient tests are in autodiff module so no encdec test. Keep this one minimal
;; ---

(defn forward-pass->grad
  "Backprops for each final variable because the reverse-mode autodifferentiation scales linearly
  to cardinality of final variables"
  [forward-res]
  (let [grads         (vec (for [idx (range (count (:loss-reses forward-res)))
                                 :let [curr-var  (first (nth (vals (:final-vars forward-res)) idx))
                                       loss-res  (nth (:loss-reses forward-res) idx)]]
                             (grad/grad curr-var loss-res {:norm-grad true})))
        into-with-key (fn [curr-key grads] (into (sorted-map) (apply merge (map curr-key grads))))]
    {:layer-uuid->vars (:vars forward-res)
     :grids            (:grids forward-res)
     :final-vars       (:final-vars forward-res)
     :layer->cobj      (:layer->cobj forward-res)
     :grads            (into-with-key :grads grads)
     :var-by-uuid      (into-with-key :var-by-uuid grads)}))


;; ---
;; Serde for gradients into the cost objects and the weights, to fill out delta and grad columns
;; ---


(defn learning-rate
  "Currently only one LR..."
  [grads]
  (or (-> grads :grids first :hyperparams (get "lr")) default-learning-rate))

;; TODO: This hangs occasionally only in test. Prolly could hang in actual usage somehow. Go fix that
(defn grad->to-update
  "To-update meaning, maps to update the unfilled columns in cobj and weight which are left unfiled until and unless a grad exists
  Both activations and deltas for cobj, so forward pass info gets in here too

  We also will dump these into the history"
  [grads]
  (let [cobj-update-iter    (for [[layer-uuid [layer-var]] (:layer-uuid->vars grads)]
                              (let [grad     ((:grads grads) (:uuid layer-var))
                                    cobjs    ((:layer->cobj grads) layer-uuid)
                                    ;; TODO: don't separately calculate this so weirdly
                                    norm-act (:value (ops/op-norm-1d layer-var))]
                                (for [[i cobj] (map-indexed vector cobjs)]
                                  ;; In-out tests with postgres require rounding to 4 digits all the time
                                  [(:uuid cobj) {:activation (uio/round-to-four (nth norm-act i))
                                                 :delta      (uio/round-to-four (* (learning-rate grads) (nth grad i)))}])))
        cobj-update-maps    (into {} (apply concat cobj-update-iter))
        matrix-var-iter     (apply concat
                                   (for [[final-var-uuid [final-var]] (:final-vars grads)]
                                     (filter #(and (map? (:metadata %))
                                                   (contains? (:metadata %) :weight-labels))
                                             (autodiff-util/ops-seq final-var))))
        weight-update-iter  (for [matrix-var matrix-var-iter]
                              (let [curr-grad       ((:grads grads) (:uuid matrix-var))
                                    src-cobj-labels (->> matrix-var :metadata :src-cobj-labels)
                                    tgt-cobj-labels (->> matrix-var :metadata :tgt-cobj-labels)
                                    weight-labels   (->> matrix-var :metadata :weight-labels)]
                                (for [[[src-cobj-label tgt-cobj-label] weight-label-data] weight-labels]
                                  (let [row      (src-cobj-labels src-cobj-label)
                                        col      (tgt-cobj-labels tgt-cobj-label)
                                        curr-mat (:value matrix-var)]
                                    ;; Guard against the cobj deletions not actually cascading to weights
                                    (when (and (< row (first (cm/shape curr-mat)))
                                               (< col (second (cm/shape curr-mat))))
                                      [(first weight-label-data) {:grad (uio/round-to-four
                                                                          (* (learning-rate grads)
                                                                             (cm/mget curr-mat row col)
                                                                             (cm/mget curr-grad row col)))}])))))
        weight-update-maps  (into {} (apply concat weight-update-iter))]
    {:cobj-updates   cobj-update-maps
     :weight-updates weight-update-maps}))

(defn update-grad-fields!
  "Actually updates grad and delta fields in cobj and weights. Mutates!"
  [to-update]
  (let [{cobj-updates   :cobj-updates
         weight-updates :weight-updates} to-update]
    (doall
      (for [[cobj-uuid update-map] cobj-updates]
        ((cost-object-model/model :update-one!) cobj-uuid update-map)))
    (doall
      (for [[weight-uuid update-map] weight-updates]
        ((weight-model/model :update-one!) weight-uuid update-map)))))
