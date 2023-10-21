(ns mtfe.events.util
  "Other functions that are good for futzing with events"
  (:require [mtfe.api :as api]))

(defn layer-join-dag-step [next-step]
  (fn [res]
    (let [layer-uuid (res :layer-uuid)]
      [[:dispatch
        [:select-cust
         {:resource       :curr-layer
          :endpoint       (api/layer-member layer-uuid)
          :params         {}
          :success-event  :sidebar-dag-success
          :success-params {:children-fn next-step}}]]])))

(defn grid-view-terminal-step [res]
  (let [grid-uuid (res :grid-uuid)]
    [[:dispatch [:select-custom :grid-graph (api/grid-graph grid-uuid) {} :sidebar-selection-success]]
     [:dispatch [:select-custom :grid-view (api/grid-view grid-uuid) {} :sidebar-selection-success]]]))
