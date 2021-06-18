(ns mertonon.autodiff.util
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.stats :as cms]
            [schema.core :as s]))

(defn ops-seq
  "Takes a root op and give a sequence of its members."
  [root-op]
  (tree-seq :uuid :inputs root-op))

(defn root->edges [root-var]
  (mapcat identity
          (mapv #(vec (for [child (:inputs %)]
                        [(:uuid %) (:uuid child)]))
                (ops-seq root-var))))

(defn var-by-uuid
  "Takes a root var and give a map of vars of the whole tree, keyed by uuid"
  [root-var]
  (into {} (map #(vector (:uuid %) %) (ops-seq root-var))))

(defn atleast-2d
  "Takes a maybe 1d tensor, maybe 2d, and coerces it to be at least 2d. noop if 2d or more."
  [vec-or-mat]
  (condp = (count (cm/shape vec-or-mat))
    ;; scalar, actually
    0 [[vec-or-mat]]
    1 [vec-or-mat]
    vec-or-mat))

(defn atleast-1d
  "Takes a maybe 0d tensor, maybe 1d, and coerces it to be at least 1d. noop if 1d or more."
  [vec-or-mat]
  (condp = (count (cm/shape vec-or-mat))
    0 [vec-or-mat]
    vec-or-mat))

(defn maybe-collapse
  "If a tensor is a 'pointlessly high dim' tensor (if it's equivalent to a 1 lower-dimensional tensor),
  whack 1 dim off of it. Otherwise, noop"
  [vec-or-mat]
  (if (= (nth (cm/shape vec-or-mat) 0) 1)
    (nth vec-or-mat 0)
    vec-or-mat))

(defn sum-one-dim
  [maybe-vec-or-mat]
  (condp = (count (cm/shape maybe-vec-or-mat))
    0 maybe-vec-or-mat
    1 (cm/esum maybe-vec-or-mat)
    2 (mapv cm/esum maybe-vec-or-mat)))

(defn stddev-one-dim
  [maybe-vec-or-mat]
  (condp = (count (cm/shape maybe-vec-or-mat))
    0 maybe-vec-or-mat
    1 (cms/sd maybe-vec-or-mat)
    2 (cms/sd maybe-vec-or-mat)))

(defn maybe-elem-sd
  [maybe-vec-or-mat]
  (condp = (count (cm/shape maybe-vec-or-mat))
    0 maybe-vec-or-mat
    (cms/sd (cm/as-vector maybe-vec-or-mat))))


(defn broadcasted-elem-op
  "Default broadcasting for core.matrix is pretty deficient.
  Deal with matrix-vector elementwise stuff being really annoying by using this one.
  Matrix-like first then vector-like second."
  [elem-op fst snd]
  (let [fst>snd? (>= (count (cm/shape fst)) (count (cm/shape snd)))
        moredim  (if fst>snd? fst snd)
        fewdim   (if fst>snd? snd fst)]
    (condp = (count (cm/shape fewdim))
      0 (elem-op moredim (cm/broadcast-coerce moredim fewdim))
      1 (let [atleast-2d-more (atleast-2d moredim)
              broadcasted-few (cm/transpose (cm/broadcast fewdim (-> (cm/shape atleast-2d-more) reverse vec)))]
          (elem-op atleast-2d-more broadcasted-few)))))
