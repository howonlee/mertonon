(ns mertonon.models.constructors
  "Constructors for mertonon models, grouped together in one place"
  (:require [tick.core :as t]))

(defn ->HealthCheck [uuid]
  {:uuid       uuid
   :version    0
   :created-at (t/instant)
   :updated-at (t/instant)})

(defn ->User [uuid email username]
  {:uuid       uuid
   :version    0
   :create-at  (t/instant)
   :updated-at (t/instant)
   :email      email
   :username   username})

;; ---
;; Network models
;; ---

(defn ->Grid [uuid grid-name grid-label optimizer-type hyperparams]
  {:uuid           uuid
   :version        0
   :created-at     (t/instant)
   :updated-at     (t/instant)
   :name           grid-name
   :label          grid-label
   :optimizer-type optimizer-type
   :hyperparams    hyperparams})

(defn ->Layer [uuid grid-uuid layer-name layer-label]
  {:uuid       uuid
   :grid-uuid  grid-uuid
   :version    0
   :created-at (t/instant)
   :updated-at (t/instant)
   :name       layer-name
   :label      layer-label})

(defn ->CostObject
  ([uuid layer-uuid cobj-name cobj-label]
   (->CostObject uuid layer-uuid cobj-name cobj-label 0.0000M))
  ([uuid layer-uuid cobj-name cobj-label activation]
   (->CostObject uuid layer-uuid cobj-name cobj-label activation 0.0000M))
  ([uuid layer-uuid cobj-name cobj-label activation delta]
   {:uuid       uuid
    :layer-uuid layer-uuid
    :version    0
    :created-at (t/instant)
    :updated-at (t/instant)
    :name       cobj-name
    :label      cobj-label
    :activation activation
    :delta      delta}))

(defn ->Weightset
  ([uuid [src-layer-uuid tgt-layer-uuid] weightset-name weightset-label]
   (->Weightset uuid src-layer-uuid tgt-layer-uuid weightset-name weightset-label))
  ([uuid src-layer-uuid tgt-layer-uuid weightset-name weightset-label]
   {:uuid           uuid
    :src-layer-uuid src-layer-uuid
    :tgt-layer-uuid tgt-layer-uuid
    :version        0
    :created-at     (t/instant)
    :updated-at     (t/instant)
    :name           weightset-name
    :label          weightset-label}))

(defn ->Weight
  ([uuid weightset-uuid src-cobj-uuid tgt-cobj-uuid weight-label weight-type weight-val]
   (->Weight uuid weightset-uuid src-cobj-uuid tgt-cobj-uuid weight-label weight-type weight-val 0.0000M))
  ([uuid weightset-uuid src-cobj-uuid tgt-cobj-uuid weight-label weight-type weight-val weight-grad]
   {:uuid           uuid
    :weightset-uuid weightset-uuid
    :src-cobj-uuid  src-cobj-uuid
    :tgt-cobj-uuid  tgt-cobj-uuid
    :version        0
    :created-at     (t/instant)
    :updated-at     (t/instant)
    :label          weight-label
    :type           weight-type
    :value          weight-val
    :grad           weight-grad}))

(defn ->Loss
  ([uuid layer-uuid loss-name loss-label target-type]
   (->Loss uuid layer-uuid loss-name loss-label target-type {}))
  ([uuid layer-uuid loss-name loss-label target-type data]
   {:uuid       uuid
    :layer-uuid layer-uuid
    :version    0
    :created-at (t/instant)
    :updated-at (t/instant)
    :name       loss-name
    :label      loss-label
    :type       target-type
    :data       data}))

(defn ->Input
  ([uuid layer-uuid input-name input-label target-type]
   (->Input uuid layer-uuid input-name input-label target-type {}))
  ([uuid layer-uuid input-name input-label target-type data]
   {:uuid       uuid
    :layer-uuid layer-uuid
    :version    0
    :created-at (t/instant)
    :updated-at (t/instant)
    :name       input-name
    :label      input-label
    :type       target-type
    :data       data}))

(defn ->Entry
  ([uuid cobj-uuid entry-name entry-label entry-type entry-val]
   (->Entry uuid cobj-uuid entry-name entry-label entry-type entry-val (t/instant)))
  ([uuid cobj-uuid entry-name entry-label entry-type entry-val entry-date]
   {:uuid       uuid
    :cobj-uuid  cobj-uuid
    :version    0
    :created-at (t/instant)
    :updated-at (t/instant)
    :name       entry-name
    :label      entry-label
    :entry-date entry-date
    :type       entry-type
    :value      entry-val}))
