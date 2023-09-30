(ns mtfe.views.grid
  "Grid view / Flow view."
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch]]
            ["reactflow"
             :refer [MiniMap
                     Controls
                     Background
                     ReactFlowProvider
                     addEdge]
             :default ReactFlow]
            ["d3-dag" :refer [dagConnect decrossOpt sugiyama]]))

;; ---
;; React class adapters
;; ---

(def react-flow-provider (r/adapt-react-class ReactFlowProvider))
(def react-flow (r/adapt-react-class ReactFlow))
(def minimap (r/adapt-react-class MiniMap))
(def controls (r/adapt-react-class Controls))
(def background (r/adapt-react-class Background))

;; ---
;; Pre-effects
;; ---

(def before-fx [nil])
(def demo-before-fx [nil])

;; ---
;; Constants
;; ---

(def node-width 120)
(def node-height 120)

;; ---
;; State
;; ---

(defonce grid-graph-state (r/atom {}))
(defonce grid-flow-state (r/atom {}))

;; De facto per-grid state to determine whether demo or not
;; Use as broadly-scoped state
(defonce demo-state (r/atom false))

(defn reset-state! []
  ;; demo-state is used outisde of this view, basically as global state
  (do
    (reset! grid-graph-state {})
    (reset! grid-flow-state {})))

;; ---
;; Event handlers
;; ---

(defn on-pane-single-click [evt]
  (let [stop-prop!    (. evt stopPropagation)
        sidebar-path! (dispatch [:nav-to-sidebar-for-current-main-view])]
    :default))

(defn on-node-single-click [evt node]
  (let [stop-prop!    (. evt stopPropagation)
        sidebar-path  (util/path ["layer_selection" (j/get node :id)])
        sidebar-path! (dispatch [:nav-route "sidebar-change" sidebar-path])]
    :default))

(defn on-node-double-click [evt node]
  (let [stop-prop!    (. evt stopPropagation)
        sidebar-path  (util/path ["layer" (j/get node :id)])
        sidebar-path! (dispatch [:nav-route "sidebar-change" sidebar-path])
        path          (util/hash-path ["layer" (j/get node :id)])
        path!         (dispatch [:nav-page path])]
    :default))

(defn on-edge-single-click [evt edge]
  (let [stop-prop!    (. evt stopPropagation)
        sidebar-path  (util/path ["weightset_selection" (j/get edge :id)])
        sidebar-path! (dispatch [:nav-route "sidebar-change" sidebar-path])]
    :default))

(defn on-edge-double-click [evt edge]
  (let [stop-prop!    (. evt stopPropagation)
        sidebar-path  (util/path ["weightset" (j/get edge :id)])
        sidebar-path! (dispatch [:nav-route "sidebar-change" sidebar-path])
        path          (util/hash-path ["weightset" (j/get edge :id)])
        path!         (dispatch [:nav-page path])]
    :default))

;; ---
;; Renderers
;; ---

(defn empty-render []
  [:<>
   [:h1 "Welcome to Mertonon!"]
   [:div "Add a responsibility center from the sidebar to begin"]])

(defn grid-page-render [curr-nodes curr-edges]
  (if (and (empty? curr-nodes) (empty? curr-edges))
    [sc/main-section
     [empty-render]]
    [sc/main-section
     [:div {:style {:height "85vh" :width "55vw" :float "left"}}
      [react-flow
       {:default-nodes        curr-nodes
        :default-edges        curr-edges
        :style                {:height "100%" :width "100%"}
        :on-pane-click        on-pane-single-click
        :on-node-click        on-node-single-click
        :on-edge-click        on-edge-single-click
        :on-node-double-click on-node-double-click
        :on-edge-double-click on-edge-double-click
        :snap-to-grid         true
        :fit-view             true}
       [minimap]
       [controls]
       [background]]]]))

;; ---
;; Layouting algorithm call
;; ---

(defn layout!
  "Layouting algorithm from the d3-dag is a side-effecting thing"
  [graph-state]
  (let [edges         (:edges graph-state)
        aug-edges     (into edges (map #(vector %1 %1) (:nodes graph-state)))
        dag-create-op (. (dagConnect) (single true))
        dag           (dag-create-op (clj->js aug-edges))
        layout        (. (sugiyama) (decross (decrossOpt)))
        ;; below mutates the dag inplace
        set-layout!   (layout dag)
        curr-descs    ^js/object (. dag descendants)
        curr-nodes    (->
                        (for [curr-desc curr-descs]
                          (let [layer (->> (filter
                                             #(= (:uuid %) (.. curr-desc -data -id))
                                             (:layers graph-state))
                                           first)]
                            {:id (.. curr-desc -data -id)
                             :style {:width node-width
                                     :height node-height
                                     :fontSize 12}
                             :position {:x (* (. curr-desc -x) node-width 2)
                                        :y (* (. curr-desc -y) node-width 2)}
                             :data {:label (str (:name layer)
                                                " - "
                                                (:label layer))}}))
                        vec)
        curr-edges    (->
                        (for [edge edges]
                          ;; ID is weightset uuid
                          (let [weightset (->> (filter
                                                 #(and (= (:src-layer-uuid %) (first edge))
                                                       (= (:tgt-layer-uuid %) (second edge)))
                                                 (:weightsets graph-state))
                                               first)]
                            {:id           (:uuid weightset)
                             :source       (first edge)
                             :target       (second edge)
                             :type         "simplebezier"
                             :style        {:strokeWidth 2}
                             :labelStyle   {:fill "#FFF"}
                             :labelBgStyle {:fill "#444444"}
                             :label        (:name weightset)
                             :markerEnd    {:type "arrow"}}))
                        vec)]
    {:curr-nodes curr-nodes :curr-edges curr-edges}))

;; ---
;; Top-level Render
;; ---

;; Need to do reagent form 3's because of interop with react flow
;; see here: https://github.com/reagent-project/reagent/blob/master/doc/CreatingReagentComponents.md

(defn grid-page [{:keys [path-params] :as match}]
  (let [uuid (:uuid path-params)]
    (r/create-class
      {:component-did-mount    (fn [_] (GET (api/grid-graph uuid)
                                            {:handler (fn [resp]
                                                        (do
                                                          (reset! grid-graph-state (util/json-parse resp))
                                                          (reset! grid-flow-state (layout! @grid-graph-state))
                                                          (reset! demo-state false)))}))
       :component-will-unmount reset-state!
       :reagent-render         (fn [] [grid-page-render
                                       (:curr-nodes @grid-flow-state)
                                       (:curr-edges @grid-flow-state)])})))

(defn grid-demo-page []
  (r/create-class
    {:component-did-mount    (fn [_] (GET (api/generator-graph)
                                          {:handler (fn [resp]
                                                      (do
                                                        (reset! grid-graph-state (util/json-parse resp))
                                                        (reset! grid-flow-state (layout! @grid-graph-state))
                                                        (reset! demo-state true)))}))
     :component-will-unmount reset-state!
     :reagent-render         (fn [] [grid-page-render
                                     (:curr-nodes @grid-flow-state)
                                     (:curr-edges @grid-flow-state)])}))
