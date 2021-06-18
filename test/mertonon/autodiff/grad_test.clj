(ns mertonon.autodiff.grad-test
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmo]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.autodiff.forward-ops :as forward-ops]
            [mertonon.autodiff.reverse-ops :as reverse-ops]
            [mertonon.autodiff.grad :as grad]
            [mertonon.generators.autodiff :as autodiff-gen]
            [mertonon.util.registry :as registry]))

(defspec normalization-normalizes-1d
  100
  (prop/for-all [vec-1d autodiff-gen/generate-entry-vec]
                (let [forward-normalized (forward-ops/op-norm-1d (forward-ops/op-var vec-1d 1))
                      reverse-normalized (reverse-ops/op-norm-1d (reverse-ops/op-var vec-1d))]
                  (and (= (cm/esum (:value forward-normalized)) 1)
                       (= (cm/esum (:value reverse-normalized)) 1)))))

(defspec normalization-normalizes-one-dim-of-2d
  100
  (prop/for-all [vec-2d autodiff-gen/generate-entry-mat]
                (let [forward-normalized (forward-ops/op-norm-2d (forward-ops/op-var vec-2d 1))
                      reverse-normalized (reverse-ops/op-norm-2d (reverse-ops/op-var vec-2d))
                      all-ones           (mapv (constantly 1N) vec-2d)]
                  (and (= (mapv cm/esum (:value forward-normalized)) all-ones)
                       (= (mapv cm/esum (:value reverse-normalized)) all-ones)))))

(defspec matmul-same-shapes
  100
  (prop/for-all [fst-member autodiff-gen/generate-mat-or-vec
                 snd-member autodiff-gen/generate-mat-or-vec]
                ;; dout has same shape as the output so just pretend output is dout
                (let [op-res              (reverse-ops/op-mmul (reverse-ops/op-var fst-member)
                                                               (reverse-ops/op-var snd-member))
                      fake-dout           (:value op-res)
                      [fst-back snd-back] (reverse-ops/back-op-mmul op-res fake-dout)]
                  (and (cm/equals (->> fst-member cm/shape cm/array)
                                  (->> fst-back cm/shape cm/array))
                       (cm/equals (->> snd-member cm/shape cm/array)
                                  (->> snd-back cm/shape cm/array))))))

(defspec same-res-op-and-numerical-diff
  100
  (prop/for-all [applied-op (autodiff-gen/generate-ops autodiff-gen/generate-matrix-var)]
                (let [op-type         (:type applied-op)
                      op-inp          (:inputs applied-op)
                      reverse-op      (registry/type->reverse-op op-type)
                      back-op         (registry/type->backward-op op-type)
                      ;; TODO: coerce to rationals and do the whole numerical rigamarole compared w/ rational
                      dout            (->> applied-op :value cm/shape cm/zero-array (cmo/+ 1))
                      applied-back-op (back-op applied-op dout)
                      numerical-op    (grad/op-numerical-grad reverse-op op-inp)]
                  (<= (cmo/-
                        (cm/esum applied-back-op)
                        (cm/esum numerical-op))
                      1e-3))))

;; TODO: test for everything staying in ratio type! everything!

;; TODO: test on matrix values. Requires a lot of fiddling with autodiff-gen because forward automatic differentiation needs that
(defspec same-res-forward-and-reverse
  100
  (prop/for-all [[complex-var loss-uuid] autodiff-gen/generate-complex-variable-and-loss]
                (let [grads       (grad/grad complex-var 1 {:norm-grad false})
                      forward-var (autodiff-gen/complex-variable->forward-ops [complex-var loss-uuid])]
                  (<= (cm/esum (cm/sub ((:grads grads) loss-uuid) (:differential forward-var))) 1e-3))))

;; TODO: something for the losses

;; TODO: test that the norm grad will always be norm

(comment (matmul-same-shapes))
