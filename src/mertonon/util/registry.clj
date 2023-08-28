(ns mertonon.util.registry
  "Bunch of global registries for stuff"
  (:require [clojure.test.check.generators :as gen]
            [mertonon.autodiff.forward-ops :as forward-ops]
            [mertonon.autodiff.reverse-ops :as ops]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.generators.mt-user :as mt-user-gen]
            [mertonon.generators.net :as net-gen]
            [mertonon.models.grid :as grid-model]
            [mertonon.models.layer :as layer-model]
            [mertonon.models.weightset :as weightset-model]
            [mertonon.models.cost-object :as cost-object-model]
            [mertonon.models.weight :as weight-model]
            [mertonon.models.input :as input-model]
            [mertonon.models.loss :as loss-model]
            [mertonon.models.entry :as entry-model]

            [mertonon.models.health-check :as health-check-model]
            [mertonon.models.mt-user :as mt-user-model]))

;; TODO: enforce the registry having this stuff by tests
;; I don't like the obvious move of auto-introspecting stuff at runtime because the poor locality of it

;; ---
;; Autodiff forward and backward registry
;; ---

(def type->forward-op
  {:+      forward-ops/op+
   :sum    forward-ops/op-sum
   :*      forward-ops/op*
   :sin    forward-ops/op-sin
   :mmul   forward-ops/op-mmul
   :norm1d forward-ops/op-norm-1d
   :norm2d forward-ops/op-norm-2d})

(def arity->reverse-op
  "Map from arity of reverse ops to the ops that have that arity. Used in op generation."
  {0         [ops/op-var]
   1         [ops/op-sin ops/op-norm-1d ops/op-norm-2d]
   2         [ops/op+ ops/op* ops/op-mmul]
   :variadic [ops/op-sum]})

(def type->reverse-op
  {:+      ops/op+
   :sum    ops/op-sum
   :*      ops/op*
   :mmul   ops/op-mmul
   :norm1d ops/op-norm-1d
   :norm2d ops/op-norm-2d
   :sin    ops/op-sin
   :var    ops/op-var})

(def type->backward-op
  {:+      ops/back-op+
   :sum    ops/back-op-sum
   :*      ops/back-op*
   :mmul   ops/back-op-mmul
   :norm1d ops/back-op-norm-1d
   :norm2d ops/back-op-norm-2d
   :sin    ops/back-op-sin
   :var    ops/back-op-var})

;; ---
;; Model / Table / Etc registry
;; ---

(def tables [:mertonon.grids :mertonon.layers :mertonon.cost-objects
             :mertonon.weightsets :mertonon.weights
             :mertonon.inputs :mertonon.losses :mertonon.entries

             :mertonon.health-checks

             :mertonon.mt-users :mertonon.mt-user-passwords])

(def net-tables
  "Tables which participate in the neural net portion of Mertonon. Used for testing"
  [:mertonon.grids :mertonon.layers :mertonon.cost-objects
   :mertonon.weightsets :mertonon.weights
   :mertonon.inputs :mertonon.losses :mertonon.entries])

(def raw-table->table {:grid             :mertonon.grids
                       :layer            :mertonon.layers
                       :cost_object      :mertonon.cost-objects
                       :weightset        :mertonon.weightsets
                       :weight           :mertonon.weights
                       :input            :mertonon.inputs
                       :loss             :mertonon.losses
                       :entry            :mertonon.entries
                       :health_check     :mertonon.health-checks
                       :mt_user          :mertonon.mt-users
                       :mt_user_password :mertonon.mt-user-passwords})

(def table->model {:mertonon.grids             grid-model/model
                   :mertonon.layers            layer-model/model
                   :mertonon.weightsets        weightset-model/model
                   :mertonon.cost-objects      cost-object-model/model
                   :mertonon.weights           weight-model/model
                   :mertonon.inputs            input-model/model
                   :mertonon.losses            loss-model/model
                   :mertonon.entries           entry-model/model

                   :mertonon.health-checks     health-check-model/model

                   :mertonon.mt-users          mt-user-model/model
                   :mertonon.mt-user-passwords mt-user-password-model/model
                   })

(def table->generator
  {:mertonon.grids        net-gen/generate-grid
   :mertonon.layers       net-gen/generate-linear-layers
   :mertonon.cost-objects net-gen/generate-linear-cost-objects
   :mertonon.weightsets   net-gen/generate-dag-weightsets
   :mertonon.weights      (gen/let [generates net-gen/generate-dag-weights]
                            (update generates :weights flatten))
   :mertonon.losses       net-gen/generate-dag-losses
   :mertonon.inputs       net-gen/generate-dag-inputs
   :mertonon.entries      aug-net-gen/merged-dag-net-and-entries

   :mertonon.mt-users     mt-user-gen/generate-mt-user})

;; child-table child-table-col parent-table-col
;; change if we need to actually have parent table name specifically ever
(def fkey-edges [[:mertonon.layer :mertonon.layer.grid_uuid
                  :mertonon.grid :mertonon.grid.uuid]
                 [:mertonon.cost_object :mertonon.cost_object.layer_uuid
                  :mertonon.layer :mertonon.layer.uuid]
                 [:mertonon.weightset :mertonon.weightset.src_layer_uuid
                  :mertonon.layer :mertonon.layer.uuid]
                 [:mertonon.weightset :mertonon.weightset.tgt_layer_uuid
                  :mertonon.layer :mertonon.layer.uuid]
                 [:mertonon.weight :mertonon.weight.src_cobj_uuid
                  :mertonon.cost_object :mertonon.cost_object.uuid]
                 [:mertonon.weight :mertonon.weight.tgt_cobj_uuid
                  :mertonon.cost_object :mertonon.cost_object.uuid]
                 [:mertonon.weight :mertonon.weight.weightset_uuid
                  :mertonon.weightset :mertonon.weightset.uuid]
                 [:mertonon.loss :mertonon.loss.layer_uuid
                  :mertonon.layer :mertonon.layer.uuid]
                 [:mertonon.input :mertonon.input.layer_uuid
                  :mertonon.layer :mertonon.layer.uuid]
                 [:mertonon.entry :mertonon.entry.cobj_uuid
                  :mertonon.cost_object :mertonon.cost_object.uuid]
                 [:mertonon.password_login :mertonon.password_login.mt_user_uuid
                  :mertonon.mt_user :mertonon.mt_user.uuid]])
