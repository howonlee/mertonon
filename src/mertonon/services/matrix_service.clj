(ns mertonon.services.matrix-service
  "Translates the net from row form to matrix form and vice versa"
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmo]
            [mertonon.models.weight :as weight-model]
            [mertonon.util.io :as uio]
            [schema.core :as s]))

;; TODO: Schema types

;; ---
;; Weightset <-> Matrix
;; ---

(def cobj-keys     [:src-cobj-uuid :tgt-cobj-uuid])
;; Don't use set difference to keep ordering
(def non-cobj-keys (vec (filter #(not (contains? (set cobj-keys) %)) weight-model/columns)))

(defn weights->matrix
  [{:keys [weightset weights src-cobjs tgt-cobjs]}]
  (let [src-cobj-labels (into {} (map-indexed (fn [idx itm] [(:uuid itm) idx]) src-cobjs))
        tgt-cobj-labels (into {} (map-indexed (fn [idx itm] [(:uuid itm) idx]) tgt-cobjs))
        matrix          (cm/mutable (cm/zero-array [(count src-cobjs) (count tgt-cobjs)]))
        _               (doseq [weight weights]
                          (cm/mset! matrix
                                    (src-cobj-labels (:src-cobj-uuid weight))
                                    (tgt-cobj-labels (:tgt-cobj-uuid weight))
                                    (float (:value weight))))
        ;; This section exists to avoid having non-specified norms
        empty-row-idxs  (keep-indexed #(if (zero? %2) %1) (mapv cm/esum (cm/slices matrix)))
        _               (doseq [empty-row-idx empty-row-idxs]
                          (cm/set-row! matrix empty-row-idx (float (/ 1 (count tgt-cobjs)))))
        weight-labels   (into {} (map (fn [weight]
                                        [(mapv weight cobj-keys)
                                         (mapv weight non-cobj-keys)]) weights))]
    {:matrix          matrix
     :src-cobj-labels src-cobj-labels
     :src-cobjs       src-cobjs
     :tgt-cobj-labels tgt-cobj-labels
     :tgt-cobjs       tgt-cobjs
     :weight-labels   weight-labels
     :weightset       weightset}))

(defn matrix->weights
  ;; TODO: Add the subtraction and fiddly semantics for true inversion
  [{:keys [matrix src-cobj-labels src-cobjs
           tgt-cobj-labels tgt-cobjs weight-labels
           weightset]}]
  (let [weights  (->> (for [[[src-cobj-uuid tgt-cobj-uuid] non-cobj-vals] weight-labels] ;;[weight-uuid weight-type weight-val weight-grad]
                        (into {:src-cobj-uuid  src-cobj-uuid
                               :tgt-cobj-uuid  tgt-cobj-uuid
                               :weightset-uuid (:uuid weightset)} (apply map vector [non-cobj-keys non-cobj-vals])))
                      vec
                      (sort-by :uuid))]
    {:weightset weightset
     :weights   weights
     :src-cobjs src-cobjs
     :tgt-cobjs tgt-cobjs}))

;; ---
;; Row-Net <-> Matrix-Net
;; ---

(defn row-net->matrix-net [row-net]
  ;; TODO: Add the subtraction semantics when we add actual values to weight and txn
  (let [{grids        :grids
         layers       :layers
         cost-objects :cost-objects
         inputs       :inputs
         losses       :losses
         weightsets   :weightsets
         weights      :weights}   row-net
        weights-by-weightset    (group-by :weightset-uuid weights)
        cobjs-by-layer          (group-by :layer-uuid cost-objects)
        matrices                (sort-by #(get-in % [:weightset :uuid])
                                  (vec (for [weightset weightsets]
                                       (let [weights   (weights-by-weightset (:uuid weightset))
                                             src-cobjs (cobjs-by-layer (:src-layer-uuid weightset))
                                             tgt-cobjs (cobjs-by-layer (:tgt-layer-uuid weightset))
                                             inp       {:weightset weightset
                                                        :weights   weights
                                                        :src-cobjs src-cobjs
                                                        :tgt-cobjs tgt-cobjs}]
                                         (weights->matrix inp)))))]
    {:grids        grids
     :layers       layers
     :cost-objects cost-objects
     :inputs       inputs
     :losses       losses
     :weightsets   weightsets
     :weights      weights
     :matrices     matrices}))

(defn matrix-net->row-net [matrix-net]
  (let [{grids        :grids
         layers       :layers
         cost-objects :cost-objects
         inputs       :inputs
         losses       :losses
         matrices     :matrices} matrix-net
        reverse-results      (map matrix->weights matrices)
        weightsets           (mapv :weightset reverse-results)
        weights              (->> reverse-results
                                  (map :weights)
                                  flatten
                                  (sort-by :uuid)
                                  vec)]
    {:grids        grids
     :layers       layers
     :cost-objects cost-objects
     :inputs       inputs
     :losses       losses
     :weightsets   weightsets
     :weights      weights}))


;; ---
;; Entries <-> Patterns
;; ---

(s/defn value-hot
  "Like a one-hot, but with the value instead of the one"
  [num-inputs entry mapping]
  (let [index (mapping (:cobj-uuid entry))]
    (cm/mset (cm/zero-vector num-inputs) index (:value entry))))

(s/defn entries->patterns
  "By pattern-layers we mean layers which are pertinent to the pattern,
  which will be included in the layers->pattern mapping.

  So, that's mostly input and output layers"
  [{:keys [entries pattern-layer-uuids cost-objects]}]
  ;; TODO: Add colorants for the loss and inputs for total amounts so we can still calc profit after normalization

  ;; TODO: Add the subtraction semantics when we add actual txn values in the entries
  (let [entries-by-cost-object (group-by :cobj-uuid entries)
        cost-objects-by-layer  (group-by :layer-uuid cost-objects)
        cobj-counts            (update-vals cost-objects-by-layer count)

        cobj->idx              (into {}
                                     (for [[layer-uuid cobj-vec] cost-objects-by-layer]
                                       [layer-uuid (into {} (map-indexed (fn [idx itm] [(:uuid itm) idx]) cobj-vec))]))
        layer->patterns        (into
                                 {}
                                 (for [layer-uuid pattern-layer-uuids]
                                   [layer-uuid
                                    (reduce cmo/+
                                            (for [cost-object (cost-objects-by-layer layer-uuid)]
                                              (reduce cmo/+
                                                      (for [entry (entries-by-cost-object (:uuid cost-object))]
                                                        (value-hot (cobj-counts layer-uuid)
                                                                 entry
                                                                 (cobj->idx layer-uuid))))))]))]
    {:layer->patterns layer->patterns
     :entries         entries
     :cobj->entries   entries-by-cost-object
     :layer->cobj     cost-objects-by-layer}))

(s/defn patterns->entries [{:keys [layer->patterns cobj->entries layer->cobj]}]
  ;; TODO: Nontrivial implementation of this with subtrahend and weight differences
  (let [entries     (->> cobj->entries vals flatten (sort-by :cobj-uuid) vec)
        layer-uuids (->> layer->patterns keys sort)
        cobjs       (->> layer->cobj vals flatten (sort-by :uuid) vec)]
    {:entries             entries
     :pattern-layer-uuids layer-uuids
     :cost-objects        cobjs}))
