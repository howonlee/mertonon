(ns mertonon.generators.autodiff
  (:require [clojure.core.matrix :as cm]
            [clojure.test.check.generators :as gen]
            [clojure.walk :as walk]
            [mertonon.autodiff.reverse-ops :as ops]
            [mertonon.autodiff.forward-ops :as forward-ops]
            [mertonon.autodiff.util :as util]
            [mertonon.util.registry :as registry]
            [schema.core :as s]))

;; ---
;; Reverse mode
;; ---

(def generate-entry-val
  (gen/fmap #(+ 1 %) gen/nat))

(def generate-entry-vec
  (gen/vector generate-entry-val 3))

(def generate-entry-mat
  (gen/vector generate-entry-vec 3))

(def generate-mat-or-vec
  (gen/one-of [generate-entry-vec generate-entry-mat]))

(def generate-simple-var
  "Generate non-recursive scalar variables (input variable, not ops)"
  (gen/let [uuid    gen/uuid
            var-val (gen/fmap #(+ 1 (abs %)) gen/ratio)]
    {:uuid   uuid
     :type   :var
     :value  var-val
     :inputs []}))

;; TODO: Use matrix generator for the arbitrary complex vars.
;; This will require a refactor of the forward steps because
;; the forward depends upon differential of one param only
;; and matrix-valued param is multiple params

(def generate-matrix-var
  "Generate non-recursive matrix-valued variable"
  (gen/let [uuid    gen/uuid
            var-mat generate-entry-mat]
    {:uuid   uuid
     :type   :var
     :value  var-mat
     :inputs []}))

(defn generate-ops
  "Given variable generator, generate ops variables which operate on generated members of that generator"
  [generator]
  (gen/let [arity   (gen/elements [1 2 :variadic])
            op      (gen/elements (registry/arity->reverse-op arity))
            members (gen/vector
                      generator
                      (if (= arity :variadic)
                        (+ 1 (rand-int 5))
                        arity))]
    (apply op members)))

(def generate-complex-variable
  "Generate recursive variables (variables which may be ops)"
  (gen/recursive-gen generate-ops generate-simple-var))

(def generate-complex-variable-and-loss
  "Generate recursive variables (variables which may be ops) and loss"
  (gen/let [complex     (gen/recursive-gen generate-ops generate-simple-var)
            ;; Valid losses are :var type only
            loss-uuid   (gen/elements (map :uuid
                                           (filter #(= (:type %) :var)
                                                   (util/ops-seq complex))))]
    [complex loss-uuid]))

;; ---
;; Reverse mode -> Forward mode
;; ---

(defn reverse-op->forward-op
  "This is basically an eval, but instead of actually evalling, we do the equivalent forward op"
  [target-uuid]
  (fn [member]
    (if (= (:type member) :var)
      (let [partial-var {:uuid  (:uuid member)
                         :type  :var
                         :value (:value member)}]
        (if (= (:uuid member) target-uuid)
          ;; TODO: make these do non-trivial values so we can do nontrivial values for the forward-backward tests
          (assoc partial-var :differential 1)
          (assoc partial-var :differential 0)))
      (if (and (map? member) (:uuid member)) ;; really if it's a var
        (let [curr-fn (registry/type->forward-op (:type member))]
          (apply curr-fn (:inputs member)))
        member))))

(defn complex-variable->forward-ops
  [[complex-variable target-uuid]]
  (walk/postwalk (reverse-op->forward-op target-uuid) complex-variable))

(comment
  (complex-variable->forward-ops (gen/generate generate-complex-variable-and-target)))
