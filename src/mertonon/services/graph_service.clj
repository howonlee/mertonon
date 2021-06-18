(ns mertonon.services.graph-service
  "Dealing with mertonon networks in graph representation, dealing with graph algorithms
  
  We actually just want plain digraph representations, not weighted digraphs. We handle weights separately"
  (:require [loom.graph :as graph]))

;; TODO: Schema types

(defn net->graph
  "Mertonon net to loom digraph form, from the layer and weightset info.

  Layers describe nodes, weightsets describe directed edges."
  [layers weightsets]
  (let [curr-graph      (graph/digraph)
        curr-graph      (apply (partial graph/add-nodes curr-graph) (map :uuid layers))
        weightset-edges (for [ws weightsets]
                          [(:src-layer-uuid ws) (:tgt-layer-uuid ws)])
        curr-graph      (apply (partial graph/add-edges curr-graph) weightset-edges)]
    curr-graph))

(defn initial-layer-uuids
  "Layers which have no indegree

  Note that this also covers singleton nodes with no outdegree either, which is as intended"
  [curr-graph]
  (filter #(= 0 (graph/in-degree curr-graph %)) (graph/nodes curr-graph)))

(defn initial-layers
  [curr-graph layers]
  (let [layers-by-uuid (group-by :uuid layers)]
    (vec (for [uuid (initial-layer-uuids curr-graph)] (layers-by-uuid uuid)))))

(defn terminal-layer-uuids
  "Layers which have no outdegree

  Note that this also covers singleton nodes with no indegree either, which is as intended"
  [curr-graph]
  (filter #(= 0 (graph/out-degree curr-graph %)) (graph/nodes curr-graph)))

(defn terminal-layers
  [curr-graph layers]
  (let [layers-by-uuid (group-by :uuid layers)]
    (vec (for [uuid (terminal-layer-uuids curr-graph)] (layers-by-uuid uuid)))))
