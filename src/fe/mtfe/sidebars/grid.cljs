(ns mtfe.sidebars.grid
  "Sidebar for grid"
  (:require [mtfe.api :as api]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

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
   (if (not @(subscribe [:is-demo?]))
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
   (if (not @(subscribe [:is-demo?]))
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
;; Before-fx
;; ---

(defn before-fx [m]
  (let [uuid (->> m :path-params :uuid)]
    [[:dispatch
      [:select-with-custom-success
       :grid-sidebar
       (api/grid-view uuid)
       {}
       :sidebar-selection-and-validate
       {:validations
        [(validations/max-num-elems [:losses] 1 :has-loss)
         (validations/max-num-elems [:inputs] 1 :has-input)]}]]]))

(defn demo-before-fx [_]
  [[:dispatch
    [:selection :grid-sidebar
     (api/generator-grid) {}]]])

;; ---
;; Sidebar View
;; ---

(defn grid-sidebar [m]
  (let [val-path   [:grid-sidebar]
        grid       @(subscribe [:sidebar-state :grid-sidebar :grids 0])
        losses     @(subscribe [:sidebar-state :grid-sidebar :losses])
        inputs     @(subscribe [:sidebar-state :grid-sidebar :inputs])
        printo     (println @(subscribe [:sidebar-state :grid-sidebar]))]
    [:<>
     [grid-display-partial grid]
     (when (not @(subscribe [:is-demo?]))
       [:<>
        [:p [util/sl (util/path ["grid" (:uuid grid) "layer_create"]) [sc/button [sc/layer-icon] " Add New Responsibility Center (Layer)"]]]
        [:p [util/sl (util/path ["grid" (:uuid grid) "weightset_create"]) [sc/button [sc/ws-icon] " Add New Weightset"]]]
        [:p
         [vblurbs/validation-toast val-path :has-input "Currently we only support one input per grid"]]
        [:p
         [vblurbs/validated-link val-path :has-input "Add New Input Annotation"
          [util/sl (util/path ["grid" (:uuid grid) "input_create"]) [sc/button "Add New Input Annotation"]]]]
        [:p
         [vblurbs/validation-toast val-path :has-loss "Currently we only support one goal per grid"]]
        [:p
         [vblurbs/validated-link val-path :has-loss "Add New Goal Annotation"
          [util/sl (util/path ["grid" (:uuid grid) "loss_create"]) [sc/button "Add New Goal Annotation"]]]]
        [:p [util/sl (util/path ["grid" (:uuid grid) "grad_kickoff"]) [sc/button "Kickoff Gradient Calculations"]]]])
     [goal-display-partial losses inputs]]))
