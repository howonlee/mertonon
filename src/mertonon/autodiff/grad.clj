(ns mertonon.autodiff.grad
  "Gradient-finding by autodifferentiation.
  
  For an explanation of what autodifferentiation is,
  see [here](https://en.wikipedia.org/wiki/Automatic_differentiation)
  
  For why we do this instead of using a tried-and-true lib like JAX or something,
  see /docs/technical_q_and_a.md"
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmo]
            [loom.graph :as graph]
            [loom.alg :as graph-alg]
            [medley.core :as med]
            [mertonon.autodiff.forward-ops :as forward-ops]
            [mertonon.autodiff.reverse-ops :as ops]
            [mertonon.autodiff.util :as util]
            [mertonon.util.registry :as registry]
            [schema.core :as s :include-macros true]
            [schema.utils :as su]))

(s/defn grad
  "Takes a variable which is a final output, takes an error, and backpropagates it through the whole DAG
  
  Config options:
  norm-grad : bool, enforces the scaling for gradient flow that we get to do because of the sum-to-zero semantics of the L1 norm grad"
  [root-var  :- ops/Variable
   error-val :- s/Any
   config    :- s/Any]
  (let [var-by-uuid (util/var-by-uuid root-var)
        curr-graph  (apply graph/digraph (util/root->edges root-var))
        bp-order    (graph-alg/topsort curr-graph)
        grad-res    (loop [curr-bp-order bp-order
                           curr-grads    {(:uuid root-var) error-val}]
                      (if (empty? curr-bp-order)
                        curr-grads
                        (let [curr-uuid    (first curr-bp-order)
                              curr-var     (var-by-uuid curr-uuid)
                              backward-op  (registry/type->backward-op (:type curr-var))
                              dout         (curr-grads curr-uuid)

                              dout-std-dev (util/maybe-elem-sd dout)
                              ;; Test exact equality w/ 0 because scaling stddevs of, like, 10^-6, 10^-9, whatever is valid usage
                              ;; This is kind of a numerical monstrosity, unfortunately
                              ;; TODO: figure out some kinda regularization to avoid having to do this exact equality w/ 0
                              dout         (if (and (:norm-grad config)
                                                    (not= 0N dout-std-dev)
                                                    (not= 0.0 dout-std-dev))
                                             (util/broadcasted-elem-op cmo// dout dout-std-dev)
                                             dout)

                              curr-inputs  (map :uuid (:inputs curr-var))
                              backprop-res (backward-op curr-var dout)
                              bp-std-devs  (mapv util/maybe-elem-sd backprop-res)
                              backprop-res (if (:norm-grad config)
                                             (into [] (for [[curr-grad curr-std-dev] (map vector backprop-res bp-std-devs)]
                                                        (if (and (not= 0N curr-std-dev) (not= 0.0 curr-std-dev))
                                                          (util/broadcasted-elem-op cmo// curr-grad curr-std-dev)
                                                          curr-grad)))
                                             backprop-res)
                              new-grads    (into {} (map vector curr-inputs backprop-res))]
                          (recur (rest curr-bp-order)
                                 (merge-with cmo/+ curr-grads new-grads)))))]
    {:grads grad-res
     :var-by-uuid var-by-uuid}))

(defn conformance-grad
  [{:keys [curr-loss losses inputs patterns final-var target-var] :as args}]
  (cmo/- (:value final-var)
         (:value target-var)))

(s/defn loss-grad-function
  [{:keys [curr-loss losses inputs patterns final-var target-var] :as args}]
  (condp = (:type curr-loss)
    :conformance     (conformance-grad args)
    ;; Old name for conformance
    :competitiveness (conformance-grad args)
    (conformance-grad args)))

(defn op-numerical-grad
  "For testing only. Apply to a singular op only, not a loss. Returns gradient only.
  Different input returning semantics from the back ops. Just use the backprop ops for real stuff, OK?"
  [op-fn op-inps]
  (let [denom     1e6
        hs        (vec (for [inp op-inps]
                         (cmo/+ (cm/zero-array (cm/shape (:value inp))) (/ 1 denom))))
        sum-inps  (vec (map-indexed (fn [idx inp] (update inp :value #(cmo/+ % (nth hs idx)))) op-inps))
        diff-inps (vec (map-indexed (fn [idx inp] (update inp :value #(cmo/- % (nth hs idx)))) op-inps))
        sum-res   (:value (apply op-fn sum-inps))
        diff-res  (:value (apply op-fn diff-inps))
        total-res (cmo// (cmo/- sum-res diff-res) (/ 2 denom))]
    total-res))

(comment
  (clojure.pprint/pprint (grad ops/e (clojure.lang.Numbers/toRatio 1))))

(comment (let [curr-net (apply graph/digraph (util/root->edges ops/e))]
  (clojure.pprint/pprint (graph-alg/topsort curr-net))))
