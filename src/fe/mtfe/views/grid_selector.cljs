(ns mtfe.views.grid-selector
  "Grid selector view, for front page nav"
  (:require [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch dispatch-sync subscribe]]))

(defn before-events [m]
  [[:intro-check m]
   [:selection :grids (api/gridApi) {}]])

(defn grid-selector [m]
  (let [curr-grid-members @(subscribe [:grid-selection])]
    [:div
     [:h2 "Welcome to Mertonon"]
     [sc/flexwrap-container
      [util/sl (util/path ["grid_create"]) [sc/rounded-button [sc/plus-icon]]]
      [util/path-fsl ["grid_demo"] [sc/rounded-button [sc/grid-icon] " Demo"]]
      (for [member curr-grid-members] ^{:key (:uuid member)}
        [sc/grid-button-container
         [util/path-fsl ["grid" (:uuid member)] [sc/link [sc/grid-icon] (str " " (:name member))]]
         [:p (str (:label member))]
         [util/sl (util/path ["grid" (:uuid member) "delete"])
          [sc/grid-button-trash-container
           [sc/trash-icon]]]])]]))
