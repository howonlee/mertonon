(ns mertonon.api.generators
  "API for demo generated data. Generated once per program run."
  (:require [clojure.core.matrix :as cm]
            [clojure.test.check.generators :as gen]
            [loom.graph :as graph]
            [mertonon.generators.net :as net-gen]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.generators.grad-net :as grad-net-gen]
            [mertonon.services.grad-service :as grad-service]
            [mertonon.services.graph-service :as graph-service]
            [mertonon.services.matrix-service :as matrix-service]
            [mertonon.util.uuid :as uutils]))

;; ---
;; Utilities for the table-likes and for serializing matrix stuff
;; ---

(defn find-by-uuid [net table uuid]
  (->> (net table)
       flatten ;; for weights
       (filter #(= (:uuid %) uuid))
       first))

(defn join [net table join-key join-key-vals]
  (let [join-key-vals (if (coll? join-key-vals)
                        join-key-vals
                        (hash-set join-key-vals))]
    (->> (net table)
         flatten ;; for weights
         (filter #(contains? join-key-vals (join-key %)))
         vec)))

(defn adjacent-weightsets
  "Adjacent weightsets to a layer, given the whole net and the layer"
  [net layer]
  ;; weightsets to the source direction of the layer or to the target direction of the layer,
  ;; which is why these are backwards
  (let [src-weightsets (join net :weightsets :tgt-layer-uuid (:uuid layer))
        tgt-weightsets (join net :weightsets :src-layer-uuid (:uuid layer))]
    {:src-weightsets src-weightsets
     :tgt-weightsets tgt-weightsets}))

;; ---
;; Generate net singleton
;; ---

(def generated-net-atom
  "One generated net, which stays in memory during run, along with the entries and forward and backward run,
  along with the updates to cobjs and weights implied by said backward run. Do this lazily"
  (atom nil))

(defn generate-demo-net-atom-and-set!
  "Generate the demo net"
  []
  (reset! generated-net-atom
          (gen/generate
            (grad-net-gen/net-and-backprop-and-updates
              aug-net-gen/dag-demo-net-and-entries))))

(defn set-generated-net-atom!
  "Use for testing consonance between non-generated and generated API"
  [net-and-backprop-and-updates]
  (reset! generated-net-atom net-and-backprop-and-updates))

;; ---
;; Get full generated net
;; ---

(defn generated-net-get [_]
  (let [[net patterns & _] @generated-net-atom
        entry-net          (assoc net :entries (sort-by :uuid (:entries patterns)))
        no-matrix-net      (dissoc entry-net :matrices)]
    {:status 200 :body no-matrix-net}))

(def generated-net-endpoint
  {:get generated-net-get :name ::generated-net})

;; ---
;; Get graph (get network structure) of full generated net
;; ---

(defn generated-net-graph-get [_]
  (let [[net & _]  @generated-net-atom
        net-graph  (graph-service/net->graph (:layers net) (:weightsets net))
        nodes      (graph/nodes net-graph)
        edges      (graph/edges net-graph)
        ;; Also dump the weightsets themselves to get their uuids
        grids      (:grids net)
        weightsets (:weightsets net)
        layers     (:layers net)]
    {:status 200 :body {:grids      grids
                        :nodes      nodes
                        :edges      edges
                        :weightsets weightsets
                        :layers     layers}}))

(def generated-net-graph-endpoint
  {:get generated-net-graph-get :name ::generated-net-graph})

;; ---
;; Layer
;; ---

(defn generated-net-layer-get [match]
  (let [[net _ _ _ cobj-index _] @generated-net-atom
        curr-uuid                       (uutils/uuid (-> match :path-params :uuid))
        layer                           (find-by-uuid net :layers curr-uuid)
        {:keys
         [src-weightsets
          tgt-weightsets]}              (adjacent-weightsets net layer)
        cost-objects                    (sort-by :uuid (join net :cost-objects :layer-uuid curr-uuid))
        cost-objects-with-grads         (vec (for [cost-object cost-objects]
                                               (merge cost-object (cobj-index (:uuid cost-object)))))
        body-res                        {:layer          layer
                                         :src-weightsets src-weightsets
                                         :tgt-weightsets tgt-weightsets
                                         :cost-objects   cost-objects-with-grads}]
     {:status 200 :body body-res}))

(def generated-net-layer-endpoint
  {:get generated-net-layer-get :name ::generated-net-layer})

;; ---
;; Cost Object
;; ---

(defn generated-net-cost-object-get [match]
  (let [[net patterns _ _ cobj-index _] @generated-net-atom
        curr-uuid                       (uutils/uuid (-> match :path-params :uuid))
        cost-object                     (find-by-uuid net :cost-objects curr-uuid)
        cost-object-with-grad           (merge cost-object (cobj-index curr-uuid))
        layer                           (find-by-uuid net :layers (:layer-uuid cost-object))
        losses                          (sort-by :uuid (join net :losses :layer-uuid (:layer-uuid cost-object)))
        inputs                          (sort-by :uuid (join net :inputs :layer-uuid (:layer-uuid cost-object)))
        {:keys
         [src-weightsets
          tgt-weightsets]}              (adjacent-weightsets net layer)
        body-res                        {:cost-object    cost-object-with-grad
                                         :layer          layer
                                         :losses         losses
                                         :inputs         inputs
                                         :entries        (or (sort-by :uuid ((:cobj->entries patterns) curr-uuid)) [])
                                         :src-weightsets src-weightsets
                                         :tgt-weightsets tgt-weightsets}]
    {:status 200 :body body-res}))

(def generated-net-cost-object-endpoint
  {:get generated-net-cost-object-get :name ::generated-net-cost-object})

;; ---
;; Weightset
;; ---

(defn generated-net-weightset-get [match]
  (let [[net
         patterns
         _ _ _
         weight-idx] @generated-net-atom
        curr-uuid    (uutils/uuid (-> match :path-params :uuid))
        weightset    (find-by-uuid net :weightsets curr-uuid)
        src-layer    (find-by-uuid net :layers (:src-layer-uuid weightset))
        tgt-layer    (find-by-uuid net :layers (:tgt-layer-uuid weightset))
        weights      (sort-by :uuid (join net :weights :weightset-uuid curr-uuid))
        grad-weights (vec (for [weight weights]
                            (merge weight (weight-idx (:uuid weight)))))
        src-cobjs    (join net :cost-objects :layer-uuid (:src-layer-uuid weightset))
        tgt-cobjs    (join net :cost-objects :layer-uuid (:tgt-layer-uuid weightset))
        body-res     {:weightset weightset
                      :src-layer src-layer
                      :tgt-layer tgt-layer
                      :weights   grad-weights
                      :src-cobjs src-cobjs
                      :tgt-cobjs tgt-cobjs}]
    {:status 200 :body body-res}))

(def generated-net-weightset-endpoint
  {:get generated-net-weightset-get :name ::generated-net-weightset})

;; ---
;; Weight
;; ---

(defn generated-net-weight-get [match]
  (let [[net
         patterns
         _ _ _
         weight-idx] @generated-net-atom
        curr-uuid    (uutils/uuid (-> match :path-params :uuid))
        weight       (find-by-uuid net :weights curr-uuid)
        grad-weight  (merge weight (weight-idx curr-uuid))
        src-cobj  (find-by-uuid net :cost-objects (:src-cobj-uuid weight))
        tgt-cobj  (find-by-uuid net :cost-objects (:tgt-cobj-uuid weight))
        weightset (find-by-uuid net :weightsets   (:weightset-uuid weight))
        body-res  {:weight    grad-weight
                   :src-cobj  src-cobj
                   :tgt-cobj  tgt-cobj
                   :weightset weightset}]
    {:status 200 :body body-res}))

(def generated-net-weight-endpoint
  {:get generated-net-weight-get :name ::generated-net-weight})

;; ---
;; Grid view
;; ---

(defn generated-net-grid-get [_]
  (let [[net & _] @generated-net-atom
        grid      (:grids net)
        losses    (:losses net)
        inputs    (:inputs net)
        body-res  {:grids  grid
                   :losses losses
                   :inputs inputs}]
    {:status 200 :body body-res}))

(def generated-net-grid-endpoint
  {:get generated-net-grid-get :name ::generated-net-grid})

;; ---
;; Get all the denormalized stuff pertaining to one forward pass
;; ---

(defn generated-net-forward-get [_]
  (let [[_ _ forward & _] @generated-net-atom]
    {:status 200 :body forward}))

(def generated-net-forward-endpoint
  {:get generated-net-forward-get :name ::generated-net-forward})

;; ---
;; Get all the denormalized stuff pertaining to one gradient run
;; ---

(defn generated-net-grad-get [_]
  (let [[_ patterns _ grad & _]   @generated-net-atom]
    {:status 200 :body grad}))

(def generated-net-grad-endpoint
  {:get generated-net-grad-get :name ::generated-net-grad})

;; get all that stuff with respect to gradient for one weightset and one layer

(defn generator-routes []
  [["/dump" generated-net-endpoint]
   ["/graph" generated-net-graph-endpoint]
   ["/grad" generated-net-grad-endpoint]
   ["/forward" generated-net-forward-endpoint]
   ["/cost_objects/:uuid" generated-net-cost-object-endpoint]
   ["/layers/:uuid" generated-net-layer-endpoint]
   ["/weightsets/:uuid" generated-net-weightset-endpoint]
   ["/weights/:uuid" generated-net-weight-endpoint]
   ["/grids" generated-net-grid-endpoint]])
