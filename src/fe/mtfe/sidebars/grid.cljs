(ns mtfe.sidebars.grid
  "Sidebar for grid"
  (:require [applied-science.js-interop :as j]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.statecharts.components :as sc-components]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.statecharts.handlers :as sc-handlers]
            [mtfe.statecharts.validations :as sc-validation]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.views.grid :as grid-view]
            [reagent.core :as r]))

;; ---
;; States
;; ---

(defonce sidebar-state
  (r/atom {:selection {}}))

;; ---
;; Partials
;; ---

(defn single-loss-partial [loss]
  [:<>
   [:hr]
   [:div "UUID: " (str (:uuid loss))]
   [:div "Name: " (str (:name loss))]
   [:p (->> loss :label str)]
   [:div "Matching Layer UUID: " (str (:layer-uuid loss))]
   [:div "Type: Competitiveness"]
   (if (not @grid-view/demo-state)
     [:div [util/sl (util/path ["loss" (:uuid loss) "delete"]) [sc/button "Delete Annotation"]]])
   [:hr]]) ;; TODO: get these to actually react to loss types

(defn single-input-partial [input]
  [:<>
   [:hr]
   [:div "UUID: " (str (:uuid input))]
   [:div "Name: " (str (:name input))]
   [:p (->> input :label str)]
   [:div "Matching Layer UUID: " (str (:layer-uuid input))]
   [:div "Type: Competitiveness"]
   (if (not @grid-view/demo-state)
     [:div [util/sl (util/path ["input" (:uuid input) "delete"]) [sc/button "Delete Annotation"]]])
   [:hr]]) ;; TODO: differentiate from loss and different types

(defn grid-display-partial [grid]
  [:<>
   [:h1 [sc/grid-icon] " Mertonon Grid"]
   [:h5 "Grid: " (str (:name grid))]
   [:p "Rectangles in the network represent responsibility centers, which we call \"layers\"."]
   [:p "Within the responsibility centers are cost nodes."]
   [:p "Lines in the network represent sets of relations between cost nodes."]
   [:p "Each relation between cost nodes we call a weight. So the lines represent groupings of weights, or weightsets."]
   [:p "Double-click on a rectangle in the network or a line to dive in."]])

(defn goal-display-partial [losses inputs]
  [:<>
   [:h3 "Inputs in Grid"]
   [:div (for [input inputs] ^{:key (:uuid input)}
           [single-input-partial input])]
   (if (empty? inputs)
     [:p "No inputs defined yet."])
   [:h3 "Goals in Grid"]
   [:div (for [loss losses] ^{:key (:uuid loss)}
           [single-loss-partial loss])]
   (if (empty? losses)
     [:p "No goals defined yet."])
   [:p "(We have designed Mertonon goals to be dynamical eventually, but currently they're hardcoded to be competitiveness)"]])

;; ---
;; Top-level rendering
;; ---

(defn grid-demo-sidebar-render [grid losses inputs]
  [:<>
   [grid-display-partial grid]
   [goal-display-partial losses inputs]])

(defn grid-sidebar-render [grid losses inputs]
  [:<>
   [grid-display-partial grid]
   [:p [util/sl (util/path ["grid" (:uuid grid) "layer_create"]) [sc/button [sc/layer-icon] " Add New Responsibility Center (Layer)"]]]
   [:p [util/sl (util/path ["grid" (:uuid grid) "weightset_create"]) [sc/button [sc/ws-icon] " Add New Weightset"]]]
   [:p
    [sc-components/validation-toast sidebar-state :has-input "Currently we only support one input per grid"]]
   [:p
    [sc-components/validated-link sidebar-state :has-input "Add New Input Annotation"
     [util/sl (util/path ["grid" (:uuid grid) "input_create"]) [sc/button "Add New Input Annotation"]]]]
   [:p
    [sc-components/validation-toast sidebar-state :has-loss "Currently we only support one goal per grid"]]
   [:p
    [sc-components/validated-link sidebar-state :has-loss "Add New Goal Annotation"
     [util/sl (util/path ["grid" (:uuid grid) "loss_create"]) [sc/button "Add New Goal Annotation"]]]]
   [:p [util/sl (util/path ["grid" (:uuid grid) "grad_kickoff"]) [sc/button "Kickoff Gradient Calculations"]]]
   [goal-display-partial losses inputs]])

;; ---
;; Class
;; ---

(defn grid-sidebar [m]
  (sel/set-selection-if-changed!
    sidebar-state
    api/grid-view
    (->> m :path-params :uuid)
    [:uuid])
  (fn [m]
    (sc-handlers/do-validations! sidebar-state [(sc-validation/max-num-elems [:selection :losses] 1 :has-loss)
                                                (sc-validation/max-num-elems [:selection :inputs] 1 :has-input)])
    [grid-sidebar-render
     (->> @sidebar-state :selection :grids first)
     (->> @sidebar-state :selection :losses)
     (->> @sidebar-state :selection :inputs)]))

(defn grid-demo-sidebar [m]
  (sel/set-selection! sidebar-state api/generatorGridApi)
  (fn [m]
    [grid-demo-sidebar-render
     (->> @sidebar-state :selection :grids first)
     (->> @sidebar-state :selection :losses)
     (->> @sidebar-state :selection :inputs)]))
