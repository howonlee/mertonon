(ns mtfe.views.cost-object
  "Cost object view / Entry view"
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.views.layer :as layer-view]
            [mtfe.views.grid :as grid-view]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defonce cobj-state (r/atom {:selection {}}))

(defn cost-object-page-render [{:keys [cost-object entries layer
                                       src-weightsets tgt-weightsets] :as cobj-state}
                               is-demo?]
  [:<>
   [:h1 [sc/cobj-icon] " Cost Node " [:strong (str (:name cost-object))]]
   [:p (->> cost-object :label str)]
   [:h2 [sc/layer-icon] " Responsibility Center: "
    [util/path-fsl ["layer" (:uuid layer)] (str (:name layer))]]
   [:h5 "% Contribution to Responsibility Center"]
   [layer-view/activation-view cost-object]
   [sc/hist-container
    [layer-view/activation-hist-view cost-object]]
   [:h5 "Mertonon Suggested % Contribution"]
   [layer-view/adjusted-activation-view cost-object]
   [sc/hist-container
    [layer-view/adjusted-activation-hist-view cost-object]]

   ;; TODO: this is pretty confusing, make it less so
   (when (seq src-weightsets)
     [:<>
      [:h5 [sc/ws-icon] " Weightsets where node is a target"]
      (for [src-weightset src-weightsets] ^{:key (:uuid src-weightset)}
        [util/path-fsl ["weightset" (:uuid src-weightset)] [sc/link (str (:name src-weightset))]])])

   (when (seq tgt-weightsets)
     [:<>
      [:h5 [sc/ws-icon] " Weightsets where node is a source"]
      (for [tgt-weightset tgt-weightsets] ^{:key (:uuid tgt-weightset)}
        [util/path-fsl ["weightset" (:uuid tgt-weightset)] [sc/link (str (:name tgt-weightset))]])])

   (when (seq entries)
     [:<>
      [:h5 [sc/entry-icon] " Entries pointing towards this cost node"]
      [sc/main-table
       [:thead
        [:tr
         [sc/table-head "UUID"]
         [sc/table-head "Name"]
         [sc/table-head "Date"]
         [sc/table-head "Value"]
         [sc/table-head ""]]]
       [:tbody
        (for [entry entries] ^{:key (:uuid entry)}
          [:tr
           [sc/table-member
            (str (:uuid entry))]
           [sc/table-member
            (str (:name entry))]
           [sc/table-member
            (str (:entry-date entry))]
           [sc/table-member
            (str (:value entry))]
           (when (not is-demo?)
             [sc/table-member
              [util/sl (util/path ["entry" (:uuid entry) "delete"])
               [sc/trash-icon]]])])
        (when (not is-demo?)
          [sc/table-member
           [util/sl (util/path ["cost_object" (:uuid cost-object) "entry_create"])
            [sc/button
             [sc/plus-icon]]]])]]])])

(defn cost-object-page [m]
  (let [is-demo?        @grid-view/demo-state
        cobj-endpoint   (fn [uuid] (if is-demo?
                                     (api/generatorCostObjectApi uuid)
                                     (api/costObjectViewApi uuid)))
        curr-match-uuid (->> m :path-params :uuid)]
    (sel/set-selection-if-changed! cobj-state cobj-endpoint curr-match-uuid [:selection :uuid])
    (fn [m]
      [cost-object-page-render (->> @cobj-state :selection) is-demo?])))
