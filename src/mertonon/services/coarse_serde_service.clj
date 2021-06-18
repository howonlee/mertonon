(ns mertonon.services.coarse-serde-service
  "Serialize and deserialize whole network at once
  For fine-grained stuff, use models"
  (:require [mertonon.models.grid :as grid-model]
            [mertonon.models.layer :as layer-model]
            [mertonon.models.cost-object :as cost-object-model]
            [mertonon.models.input :as input-model]
            [mertonon.models.loss :as loss-model]
            [mertonon.models.weightset :as weightset-model]
            [mertonon.models.weight :as weight-model]
            [mertonon.util.db :as db]))

;; Entry can be taken care of by just calling entry-model/create-many and read-many

;; ---
;; Net in-memory -> Net in-DB
;; ---

(defn net->db [{:keys [grids layers cost-objects inputs losses weightsets weights] :as net}]
  (do 
    ((grid-model/model :create-many!) grids)
    ((layer-model/model :create-many!) layers)
    ((cost-object-model/model :create-many!) cost-objects)
    ((input-model/model :create-many!) inputs)
    ((loss-model/model :create-many!) losses)
    ((weightset-model/model :create-many!) weightsets)
    ((weight-model/model :create-many!) (flatten weights))))

(defn cobj-changes->db [cobj-changes]
  (doall (for [[uuid changes] (seq cobj-changes)]
         ((cost-object-model/model :update-one!) uuid changes))))

(defn weight-changes->db [weight-changes]
  (doall (for [[uuid changes] (seq weight-changes)]
         ((weight-model/model :update-one!) uuid changes))))

;; ---
;; Grid UUID -> Net in-memory
;; ---

;; TODO: reimplement by a big-ass multijoin, and put a property of implementation consonance on it
;; TODO: fix intermitten sorting problem
(defn db->net [grid-uuid]
  (let [grid         ((grid-model/model :read-one) grid-uuid)
        layers       ((layer-model/model :read-where)
                      [:= :grid-uuid grid-uuid])
        cost-objects ((cost-object-model/model :read-where)
                      [:in :layer-uuid (mapv :uuid layers)])
        inputs       ((input-model/model :read-where)
                      [:in :layer-uuid (mapv :uuid layers)])
        losses       ((loss-model/model :read-where)
                      [:in :layer-uuid (mapv :uuid layers)])
        weightsets   ((weightset-model/model :read-where) [:in :tgt-layer-uuid (mapv :uuid layers)])
        flat-weights ((weight-model/model :read-where) [:in :weightset-uuid (mapv :uuid weightsets)])
        weights      (->> flat-weights (group-by :weightset-uuid) vals vec)]
    {:grids        [grid]
     :layers       layers
     :cost-objects cost-objects
     :inputs       inputs
     :losses       losses
     :weightsets   weightsets
     :weights      weights}))

(comment (require '[mertonon.generators.net :as net-gen])
         (require '[clojure.test.check.generators :as gen])
         (let [net (gen/generate net-gen/generate-dag-net)
               grid-uuid (-> net :grids first :uuid)]
           (net->db net)
           (db->net grid-uuid)))
