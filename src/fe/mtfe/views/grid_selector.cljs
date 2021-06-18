(ns mtfe.views.grid-selector
  "Grid selector view, for front page nav"
  (:require [ajax.core :refer [GET POST]]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defonce grid-state (r/atom {:selection []}))

(defn grid-selector-render [curr-grid-members]
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
         [sc/trash-icon]]]])]])


(defn grid-selector [m]
  (sel/set-selection! grid-state api/gridApi)
  (fn [m]
    [grid-selector-render (->> @grid-state :selection)]))
