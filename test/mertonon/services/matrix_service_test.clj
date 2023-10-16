(ns mertonon.services.matrix-service-test
  (:require [clojure.core.matrix :as cm]
            [clojure.data :as cd]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [mertonon.generators.aug-net :as aug-net-gen]
            [mertonon.generators.net :as net-gen]
            [mertonon.services.graph-service :as gs]
            [mertonon.services.matrix-service :as ms]
            [mertonon.test-utils :as tu]))

;; TODO: make sure the matrices produced are correct shape

(defspec weightset-matrix-encdec-test
  tu/many
  (prop/for-all [matrix-weights net-gen/generate-matrix-weights]
                (= matrix-weights (-> matrix-weights ms/weights->matrix ms/matrix->weights))))

;; Do not let matrix rows sum to zero because we l1-normalize matrix rows and what's normalization of a zero row anyways
(defspec matrix-rows-do-not-sum-to-zero
  tu/many
  (prop/for-all [matrix-weights net-gen/generate-matrix-weights]
                (let [elem-set (->> matrix-weights ms/weights->matrix :matrix
                                    cm/slices (mapv cm/esum) (apply hash-set))]
                  (every? #(not (zero? %)) elem-set))))


(defspec linear-entry-pattern-encdec-test
  tu/many
  (prop/for-all [[net entries] aug-net-gen/net-and-entries]
                (let [orig-entries          (sort-by :cobj-uuid (entries :entries))
                      cost-objects-by-layer (group-by :layer-uuid (:cost-objects net))
                      curr-graph            (gs/net->graph (:layers net) (:weightsets net))
                      inp-layer-uuid        (first (gs/initial-layer-uuids curr-graph))
                      out-layer-uuid        (first (gs/terminal-layer-uuids curr-graph))
                      orig-input-cobjs      (cost-objects-by-layer inp-layer-uuid)
                      orig-output-cobjs     (cost-objects-by-layer out-layer-uuid)
                      orig-inp              {:entries             orig-entries
                                             :pattern-layer-uuids [inp-layer-uuid out-layer-uuid]
                                             :cost-objects            (->> (into orig-input-cobjs orig-output-cobjs)
                                                                       (sort-by :uuid)
                                                                       vec)}
                      trans-out             (ms/entries->patterns orig-inp)
                      involuted-inp         (ms/patterns->entries trans-out)]
                  (= orig-inp involuted-inp))))

(defspec dag-entry-pattern-encdec-test
  tu/many
  (prop/for-all [[net entries] aug-net-gen/dag-net-and-entries]
                (let [orig-entries          (sort-by :cobj-uuid (entries :entries))
                      cost-objects-by-layer (group-by :layer-uuid (:cost-objects net))
                      curr-graph            (gs/net->graph (:layers net) (:weightsets net))

                      inp-layer-uuids       (gs/initial-layer-uuids curr-graph)
                      out-layer-uuids       (gs/terminal-layer-uuids curr-graph)
                      orig-input-cobjs      (-> (for [inp-layer-uuid inp-layer-uuids]
                                                  (cost-objects-by-layer inp-layer-uuid))
                                                flatten vec)
                      orig-output-cobjs     (-> (for [out-layer-uuid out-layer-uuids]
                                                  (cost-objects-by-layer out-layer-uuid))
                                                flatten vec)
                      orig-inp              {:entries             orig-entries
                                             :pattern-layer-uuids (-> (into inp-layer-uuids out-layer-uuids)
                                                                      sort dedupe vec)
                                             :cost-objects            (->> (into orig-input-cobjs orig-output-cobjs)
                                                                       (sort-by :uuid)
                                                                       vec)}
                      trans-out             (ms/entries->patterns orig-inp)
                      involuted-inp         (ms/patterns->entries trans-out)]

                  (= orig-inp involuted-inp)))
  )

(defspec linear-full-encdec-test
  tu/many
  (prop/for-all [net net-gen/generate-linear-net]
                (= net
                   (-> net
                       ms/row-net->matrix-net
                       ms/matrix-net->row-net))))

(defspec dag-full-encdec-test
  tu/many
  (prop/for-all [dag-net net-gen/generate-dag-net]
                (= dag-net
                   (-> dag-net
                       ms/row-net->matrix-net
                       ms/matrix-net->row-net))))

(comment (run-tests))
