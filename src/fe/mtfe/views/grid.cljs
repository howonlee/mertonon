(ns mtfe.views.grid
  "Grid view / Flow view."
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx]]
            ["reactflow"
             :refer [MiniMap
                     Controls
                     Background
                     ReactFlowProvider
                     addEdge]
             :default ReactFlow]
            ["d3-dag" :refer [dagConnect decrossOpt sugiyama]]
            [re-frame.core :refer [dispatch dispatch-sync reg-event-db subscribe]]))

;; ---
;; React class adapters
;; ---

(def react-flow-provider (r/adapt-react-class ReactFlowProvider))
(def react-flow (r/adapt-react-class ReactFlow))
(def minimap (r/adapt-react-class MiniMap))
(def controls (r/adapt-react-class Controls))
(def background (r/adapt-react-class Background))

;; ---
;; Surrounding fx
;; ---

(defn before-fx [m]
  (let [uuid (->> m :path-params :uuid)]
    [[:dispatch
      [:select-with-custom-success :curr-grid
       (api/grid-graph uuid) {} :set-grid-state]]]))

(defn demo-before-fx [_]
  [[:dispatch
    [:select-with-custom-success :curr-grid
     (api/generator-graph) {} :set-grid-state-demo]]])

(defn after-fx [_] [[:dispatch [:reset-grid-state]]])

(defonce demo-state (r/atom false))

;; ---
;; Constants
;; ---

(def node-width 120)
(def node-height 120)

;; ---
;; State
;; ---

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

(defn grid-page [m]
  (let [curr-nodes @(subscribe [:selection :curr-grid :flow :curr-nodes])
        curr-edges @(subscribe [:selection :curr-grid :flow :curr-edges])]
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
         [background]]]])))

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
;; Idiosyncratic events
;; ---

(reg-event-db :reset-grid-state
              ;; Do not reset demo state
              (fn [db _]
                (-> db
                    (assoc-in [:selection :curr-grid :graph] {})
                    (assoc-in [:selection :curr-grid :flow] {}))))

(reg-event-db :set-grid-state
              (fn [db [event resource res]]
                (-> db
                    (assoc-in [:selection :curr-grid :graph] res)
                    (assoc-in [:selection :curr-grid :flow] (layout! res))
                    (assoc-in [:is-demo?] false))))

(reg-event-db :set-grid-state-demo
              (fn [db [event resource res]]
                (-> db
                    (assoc-in [:selection :curr-grid :graph] res)
                    (assoc-in [:selection :curr-grid :flow] (layout! res))
                    (assoc-in [:is-demo?] true))))
