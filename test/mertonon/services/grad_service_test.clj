(ns mertonon.services.grad-service-test
  (:require [clojure.core.matrix :as cm]
            [clojure.core.matrix.operators :as cmo]
            [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.autodiff.reverse-ops :as ops]
            [mertonon.services.grad-service :as gs]
            [mertonon.services.graph-service :as graphs]
            [mertonon.services.matrix-service :as ms]
            [mertonon.generators.net :as net-gen]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.generators.grad-net :as grad-net-gen]))

(defspec matrix-var-encdec-test
  tu/many
  (prop/for-all [matrix-weights net-gen/generate-matrix-weights]
                (= matrix-weights (-> matrix-weights
                                      ms/weights->matrix
                                      (gs/matrix->matrix-var {:transpose true})
                                      (gs/matrix-var->matrix {:transpose true})
                                      ms/matrix->weights))))

(defspec linear-forward-encdec-test
  tu/many
  (prop/for-all [[matrix-net patterns] (grad-net-gen/matrix-net-and-patterns aug-net-gen/net-and-entries)]
                ;; TODO: deal with the normalization a better way than ignoring it, make it conform to profit
                (let [[new-matrix-net new-patterns] (gs/forward-pass->net-patterns
                                                      (gs/net-patterns->forward-pass
                                                        matrix-net
                                                        patterns))
                      [matrices new-matrices]       [(:matrices matrix-net) (:matrices new-matrix-net)]]
                  (and (= patterns new-patterns)
                       (every? (fn [[fst snd]] (cm/equals (:matrix fst) (:matrix snd) 1e-4))
                               (map vector matrices new-matrices))))))

(defspec original-graph-corresponds-to-forward-encdec
  tu/many
  (prop/for-all [[matrix-net patterns] (grad-net-gen/matrix-net-and-patterns aug-net-gen/dag-net-and-entries)]
                (let [orig-graph     (graphs/net->graph (:layers matrix-net) (map :weightset (:matrices matrix-net)))
                      [encdec-net _] (gs/forward-pass->net-patterns
                                       (gs/net-patterns->forward-pass
                                         matrix-net
                                         patterns))
                      encdec-graph   (graphs/net->graph (:layers encdec-net) (map :weightset (:matrices encdec-net)))]
                  (= orig-graph encdec-graph))))


(defspec dag-forward-encdec-test
  tu/many
  (prop/for-all [[matrix-net patterns] (grad-net-gen/matrix-net-and-patterns aug-net-gen/dag-net-and-entries)]
                ;; TODO: deal with the normalization a better way than ignoring it, make it conform to profit
                (let [[new-matrix-net new-patterns] (gs/forward-pass->net-patterns
                                                      (gs/net-patterns->forward-pass
                                                        matrix-net
                                                        patterns))
                      [matrices new-matrices]       [(:matrices matrix-net) (:matrices new-matrix-net)]]
                  (and (= patterns new-patterns)
                       (every? (fn [[fst snd]] (cm/equals (:matrix fst) (:matrix snd) 1e-4))
                               (map vector matrices new-matrices))))))

(defspec linear-grad-run-test
  tu/many
  (prop/for-all [[matrix-net patterns] (grad-net-gen/matrix-net-and-patterns aug-net-gen/net-and-entries)]
                (let [forward-pass (gs/net-patterns->forward-pass matrix-net patterns)]
                  (not-empty (:grads (gs/forward-pass->grad forward-pass))))))

(defspec dag-grad-run-test
  tu/many
  (prop/for-all [[matrix-net patterns] (grad-net-gen/matrix-net-and-patterns aug-net-gen/dag-net-and-entries)]
                (let [forward-pass (gs/net-patterns->forward-pass matrix-net patterns)]
                  (not-empty (:grads (gs/forward-pass->grad forward-pass))))))

;; TODO: actually kick off the save, within a test ctx
(defspec grad-save-completion-test
  tu/many
  (prop/for-all [[matrix-net patterns forward grads cobj-updates weight-updates]
                 (grad-net-gen/net-and-backprop-and-updates aug-net-gen/dag-net-and-entries)]
                (let [cobjs                            (:cost-objects matrix-net)
                      weights                          (apply concat (:weights matrix-net))
                      cobj-update-counts-match         (= (count (keys cobj-updates)) (count cobjs))
                      weight-update-counts-match       (= (count (keys weight-updates)) (count weights))
                      all-cobj-activations             (every? some? (map :activation (vals cobj-updates)))
                      all-cobj-deltas                  (every? some? (map :delta (vals cobj-updates)))
                      all-weight-grads                 (every? some? (map :grad (vals weight-updates)))]
                  (and cobj-update-counts-match
                       weight-update-counts-match
                       all-cobj-activations
                       all-cobj-deltas
                       all-weight-grads))))

;; TODO profit properties

(comment (grad-save-completion-test))
