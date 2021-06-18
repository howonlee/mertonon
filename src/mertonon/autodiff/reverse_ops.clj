(ns mertonon.autodiff.reverse-ops
  "Ops for reverse-mode autodiff. Defining ops for the differentiable programming we're going to do.

  Most neural everything is reverse-mode ops, so this will have a lot more attention paid to it than the forward-mode."
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmo]
            [mertonon.autodiff.util :as util]
            [schema.core :as s :include-macros true]))

;; TODO: Get a protocol for these ops

(s/defschema Variable {:uuid                      s/Uuid
                       :type                      s/Keyword
                       :value                     s/Any
                       :inputs                    [(s/recursive #'Variable)]
                       (s/optional-key :metadata) s/Any})

;; op-foo is forward pass
;; back-op-foo is backward pass

;; Don't forget to add stuff in the registry if you add it here! Tests look at the registry.
;; Don't forget to also add stuff in the forward-mode impl if you add it in the more important reverse-mode impl
;; TODO: enforce this at test time

(s/defn op-var :- Variable
  [value :- s/Any & metadata]
  {:uuid     (java.util.UUID/randomUUID)
   :type     :var
   :value    value
   :inputs   []
   :metadata (first metadata)})

(s/defn back-op-var :- [s/Any]
  [op   :- Variable
   dout :- s/Any]
  [dout])

(s/defn op+ :- Variable
  [a :- Variable
   b :- Variable]
  {:uuid   (java.util.UUID/randomUUID)
   :type   :+
   :value  (cmo/+ (:value a) (:value b))
   :inputs [a b]})

(s/defn back-op+ :- [s/Any]
  [op   :- Variable
   dout :- s/Any]
  [dout dout])

(s/defn op-sum :- Variable
  "Sum an arbitrary number of variables. Not summation of elements within a vec or mat"
  [& summands]
  {:uuid   (java.util.UUID/randomUUID)
   :type   :sum
   :value  (apply cmo/+ (map :value summands))
   :inputs summands})

(s/defn back-op-sum :- [s/Any]
  [op   :- Variable
   dout :- s/Any]
  (vec (repeat (count (:inputs op)) dout)))

(s/defn op* :- Variable
  [a :- Variable
   b :- Variable]
  {:uuid   (java.util.UUID/randomUUID)
   :type   :*
   :value  (cmo/* (:value a) (:value b))
   :inputs [a b]})

(s/defn back-op* :- [s/Any]
  [op   :- Variable
   dout :- s/Any]
  (let [[a b] (:inputs op)]
    [(cmo/* dout (:value b)) (cmo/* dout (:value a))]))

(s/defn op-mmul :- Variable
  "Matrix multiplication."
  [a :- Variable
   b :- Variable]
  {:uuid   (java.util.UUID/randomUUID)
   :type   :mmul
   :value  (cm/inner-product (:value a) (:value b))
   :inputs [a b]})

(s/defn back-op-mmul :- [s/Any]
  [op   :- Variable
   dout :- s/Any]
  (let [[a b] (:inputs op)
        ;; I hate this
        ;; TODO: figure out something I don't hate
        fst-mult (if (> (count (cm/shape (:value b))) 1)
                   cm/mmul
                   cm/outer-product)
        fst (fst-mult dout (cm/transpose (:value b)))
        snd-mult (if (> (count (cm/shape (:value a))) 1)
                   cm/mmul
                   cm/outer-product)
        snd (snd-mult (cm/transpose (:value a)) dout)]
    [fst snd]))

(s/defn op-norm-1d :- Variable
  [a :- Variable]
  (let [denom (cm/esum (:value a))
        res   (cmo// (:value a) denom)]
    {:uuid     (java.util.UUID/randomUUID)
     :type     :norm1d
     :value    res
     :inputs   [a]
     :metadata {:norm denom}}))

(s/defn back-op-norm-1d :- [s/Any]
  [op   :- Variable
   dout :- s/Any]
  (let [[a]          (:inputs op)
        norm         (-> op :metadata :norm)
        dout-reduced (->> dout cm/esum)
        numer        (cmo/- (cmo/* dout norm)
                            (cmo/* (:value a) dout-reduced))
        denom        (cmo/* norm norm)
        res          (cmo// numer denom)]
    [res]))

(s/defn op-norm-2d :- Variable
  [a :- Variable]
  (let [denoms (util/sum-one-dim (:value a))
        res    (util/broadcasted-elem-op cmo// (:value a) denoms)]
    {:uuid     (java.util.UUID/randomUUID)
     :type     :norm2d
     :value    res
     :inputs   [a]
     :metadata {:norm denoms}}))

(s/defn back-op-norm-2d :- [s/Any]
  [op   :- Variable
   dout :- s/Any]
  (let [[a]          (:inputs op)
        norm         (-> op :metadata :norm)
        dout-reduced (util/sum-one-dim dout)
        numer        (cmo/- (util/broadcasted-elem-op cmo/* dout norm)
                            (util/broadcasted-elem-op cmo/* (:value a) dout-reduced))
        denom        (cmo/* norm norm)
        res          (util/broadcasted-elem-op cmo// numer denom)]
    [res]))

(s/defn op-sin :- Variable
  [a :- Variable]
  {:uuid   (java.util.UUID/randomUUID)
   :type   :sin
   :value  (cm/sin (:value a))
   :inputs [a]})

(s/defn back-op-sin :- [s/Any]
  [op   :- Variable
   dout :- s/Any]
  (let [[a] (:inputs op)]
    [(cmo/* dout (cm/cos (:value a)))]))

(comment 
  (s/def a (op-var (/ 1 2)))

  (s/def b (op-var (/ 42 10)))
  
  (s/def mat-a (op-var [[(/ 1 2) 2 2] [1 2 3]]))

  (def mat-norm (op-norm mat-a))
  (def back-mat-norm (back-op-norm mat-norm [[1 1 1] [1 1 1]]))

  (def c (op* a b))
  (def d (op-sin a))

  (def e (op+ c d)))
