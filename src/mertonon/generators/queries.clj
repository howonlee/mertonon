(ns mertonon.generators.queries
  "Generating increasingly weird queries when we start needing them but not before"
  (:require [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [loom.graph :as graph]
            [loom.alg :as graph-alg]
            [loom.attr :as graph-attr]
            [loom.alg-generic :as graph-alg-generic]
            [mertonon.util.queries :as queries]
            [mertonon.util.registry :as registry]))

(def generate-fkeys (gen/elements registry/fkey-edges))

(def generate-single-joined-query
  (gen/let [[child-table child-table-col parent-table parent-table-col] generate-fkeys]
    {:table            child-table
     :join-tables      [parent-table]
     :join-col-edges   [[child-table-col parent-table-col]]
     :where-clause     [:= 1 1]
     :raw-table->table registry/raw-table->table
     :table->model     registry/table->model}))

;; The network of fkeys in a db induce a graph.
;; To get the multiple joins, we look at arbitrary paths in that graph.

(def fkey-edge-graph
  (let [edges  (vec (for [[child-table child-table-col parent-table parent-table-col] registry/fkey-edges]
                      ;; Annoyed by the fkey semantics being the way it is, even though it's that way for a reason
                      [[parent-table child-table] [parent-table-col child-table-col]]))
        graph  (graph/digraph)
        graph  (graph/add-edges* graph (mapv first edges))
        ;; The edge itself is defined by the parent and child table, but in order to actually make the join we need the table cols too
        graph  (reduce
                 (fn [curr-graph total-edge]
                   (graph-attr/add-attr-to-edges curr-graph :cols (second total-edge) [(first total-edge)]))
                 graph edges)]
    graph))

(def fkey-paths-pred
  (graph/predecessors fkey-edge-graph))

(def fkey-path-gen
  (gen/let [curr-node (gen/elements (graph/nodes fkey-edge-graph))
            curr-path (gen/elements (graph-alg-generic/trace-paths fkey-paths-pred curr-node))]
    (->> curr-path reverse vec)))

(def generate-multi-joined-query
  (gen/let [curr-path (gen/such-that #(>= (count %) 2) fkey-path-gen 1000)]
    (let [;; reverse because of parent child edge semantics
          path-edges   (mapv vec (partition 2 1 curr-path))
          col-path     (mapv #((graph-attr/attrs fkey-edge-graph %) :cols) path-edges)
          fst-table    (first curr-path)
          other-tables (->> curr-path rest vec)]
      {:table            fst-table
       :join-tables      other-tables
       :join-col-edges   col-path
       :where-clause     [:= 1 1]
       :raw-table->table registry/raw-table->table
       :table->model     registry/table->model})))

(comment (queries/select-where-joined (gen/generate generate-single-joined-query)))
(comment (clojure.pprint/pprint fkey-edge-graph))
(comment (gen/generate fkey-path-gen))

(comment (select-keys (gen/generate generate-multi-joined-query) [:columns :table :join-tables :join-col-edges :where-clause]))
(comment (queries/select-where-joined (gen/generate generate-multi-joined-query)))
