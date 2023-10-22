(ns mtfe.events.util
  "Other functions that are good for futzing with events"
  (:require [mtfe.api :as api]))

;; ---
;; Dag event steps - joining
;; ---

(defn layer-join-dag-step
  "Join the selection by layer or src-layer"
  [resource next-step]
  (fn [res]
    (let [layer-uuid (or (res :layer-uuid)
                         (res :src-layer-uuid))]
      [[:dispatch
        [:select-cust
         {:resource       resource
          :endpoint       (api/layer-member layer-uuid)
          :params         {}
          :success-event  :sidebar-dag-success
          :success-params {:children-fn next-step}}]]])))

;; ---
;; Dag event steps - terminal
;; ---

(defn grid-view-terminal-step [graph-resource view-resource]
  (fn [res]
    (let [grid-uuid (res :grid-uuid)]
      [[:dispatch [:select-custom graph-resource (api/grid-graph grid-uuid) {} :sidebar-selection-success]]
       [:dispatch [:select-custom view-resource (api/grid-view grid-uuid) {} :sidebar-selection-success]]])))

(defn weightset-view-terminal-step [ws-resource]
  (fn [res]
    (let [ws-uuid (res :weightset-uuid)]
      [[:dispatch [:select-custom ws-resource (api/weightset-view ws-uuid) {} :sidebar-selection-success]]])))
