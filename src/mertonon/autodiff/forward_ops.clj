(ns mertonon.autodiff.forward-ops
  "Forward-mode autodiff. Not to be confused with the forward portion of backpropagation.
  For testing purposes mainly"
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmo]
            [mertonon.autodiff.util :as util]
            [schema.core :as s :include-macros true]))

(s/defschema ForwardVariable {:uuid                      s/Uuid
                              :type                      s/Keyword
                              :value                     s/Any
                              :differential              s/Any
                              (s/optional-key :metadata) s/Any})

(s/defn op-var :- ForwardVariable
  [value        :- s/Any
   differential :- s/Any
   & metadata]
  {:uuid         (java.util.UUID/randomUUID)
   :type         :var
   :value        value
   :differential differential
   :metadata     metadata})

(s/defn op+ :- ForwardVariable
  [a :- ForwardVariable
   b :- ForwardVariable]
  {:uuid         (java.util.UUID/randomUUID)
   :type         :+
   :value        (cmo/+ (:value a) (:value b))
   :differential (cmo/+ (:differential a) (:differential b))})

(s/defn op-sum :- ForwardVariable
  [& summands]
  {:uuid         (java.util.UUID/randomUUID)
   :type         :sum
   :value        (apply cmo/+ (map :value summands))
   :differential (apply cmo/+ (map :differential summands))})

(s/defn op* :- ForwardVariable
  "Elementwise multiplication."
  [a :- ForwardVariable
   b :- ForwardVariable]
  {:uuid         (java.util.UUID/randomUUID)
   :type         :*
   :value        (cmo/* (:value a) (:value b))
   :differential (cmo/+ (cmo/* (:value a) (:differential b))
                        (cmo/* (:value b) (:differential a)))})

(s/defn op-mmul :- ForwardVariable
  "Matrix multiplication."
  [a :- ForwardVariable
   b :- ForwardVariable]
  {:uuid         (java.util.UUID/randomUUID)
   :type         :mmul
   :value        (cm/mmul (:value a) (:value b))
   :differential (cmo/+ (cm/mmul (:differential a) (:value b))
                        (cm/mmul (:value a) (:differential b)))})

(s/defn op-norm-1d :- ForwardVariable
  [a :- ForwardVariable]
  (let [norm           (cm/esum (:value a))
        res            (util/broadcasted-elem-op cmo// (:value a) norm)
        dout-reduced   (->> a :differential cm/esum)
        differential   (cmo//
                         (cmo/- (cmo/* (:differential a) norm)
                                (cmo/* (:value a) dout-reduced))
                         (cmo/* norm norm))]
    {:uuid         (java.util.UUID/randomUUID)
     :type         :norm1d
     :value        res
     :differential differential}))

(s/defn op-norm-2d :- ForwardVariable
  [a :- ForwardVariable]
  (let [norm         (util/sum-one-dim (:value a))
        res          (util/broadcasted-elem-op cmo// (:value a) norm)
        dout-reduced (util/sum-one-dim (:differential a))
        differential (cmo//
                       (cmo/- (cmo/* (:differential a) norm)
                              (cmo/* (:value a) dout-reduced))
                       (cmo/* norm norm))]
    {:uuid         (java.util.UUID/randomUUID)
     :type         :norm2d
     :value        res
     :differential differential}))

(s/defn op-sin :- ForwardVariable
  [a :- ForwardVariable]
  {:uuid         (java.util.UUID/randomUUID)
   :type         :sin
   :value        (cm/sin (:value a))
   :differential (cmo/* (cm/cos (:value a)) (:differential a))})

(comment
  (s/def a :- ForwardVariable
    {:uuid         (java.util.UUID/randomUUID)
     :type         :var
     :value        (cm/array [[2 2] [3 3]])
     :differential (cm/array [[1 0] [0 1]])})

  (s/def b :- ForwardVariable
    {:uuid         (java.util.UUID/randomUUID)
     :type         :var
     :value        (cm/array [[1 2] [5 3]])
     :differential (cm/array [[0 0] [0 0]])})

  (def normed-a (op-norm a))

  (def c (op* a b))
  (def d (op-sin a))

  (def e (op+ c d))

  (clojure.pprint/pprint [a b c d e])

  (clojure.pprint/pprint (cmo/+ (:value b) (cm/emap rationalize (cm/cos (:value a))))))
