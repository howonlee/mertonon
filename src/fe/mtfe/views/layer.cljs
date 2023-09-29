(ns mtfe.views.layer
  "Layer / Responsibility Center view"
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [goog.string :as gstring]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.views.grid :as grid-view]
            [reagent.core :as r]))

;; ---
;; State
;; ---

(defonce layer-state (r/atom {:selection {}}))

;; ---
;; Partials
;; ---

(defn width-percentage [activation-frac]
  (gstring/format "%f%%" (Math/ceil (* 100 activation-frac))))

(defn activation-view [cost-object]
  [:div (gstring/format "%.2f" (* 100 (:activation cost-object)))])

(defn activation-hist-view [cost-object]
  [:div.bg-light-gray {:style {:height "100%" :width (width-percentage (:activation cost-object))}} " "])

(defn adjusted-activation-view [cost-object]
  (gstring/format "%.2f" (* 100
                            (- (:activation cost-object)
                               (:delta cost-object)))))

(defn adjusted-activation-hist-view [cost-object]
  [:div.bg-light-gray {:style {:height "100%" :width (width-percentage (max 0 (- (:activation cost-object)
                                                                                 (:delta cost-object))))}} " "])

;; ---
;; Main view
;; ---

(defn layer-page-render [layer-state is-demo?]
  (let [grid-path (if is-demo?  ["grid_demo"] ["grid" (->> layer-state :layer :grid-uuid)])]
    [:div.fl.pa2
     [:h1 [sc/layer-icon] " Responsibility Center " [:strong (str (->> layer-state :layer :name))]]
     [:h2 [util/path-fsl grid-path [:p [sc/grid-icon] " Back to Grid"]]]
     [:p (str (->> layer-state :layer :label))]
     [sc/main-table
      [:thead
       [:tr
        [sc/table-head [sc/cobj-icon] " Cost Node"]
        [sc/table-head "% Contribution to Responsibility Center"]
        [sc/histogram-head]
        [sc/table-head "Mertonon Suggested % Contribution"]
        [sc/histogram-head]
        (when (not is-demo?)
          [sc/histogram-head])]]
      [:tbody
       (for [cost-object (->> layer-state :cost-objects)] ^{:key (:uuid cost-object)}
         [:tr
          [sc/table-member
           [util/path-fsl ["cost_object" (:uuid cost-object)] (str (:name cost-object))]]
          [sc/table-member
           [activation-view cost-object]]
          [sc/histogram-member
           [activation-hist-view cost-object]]
          [sc/table-member
           [adjusted-activation-view cost-object]]
          [sc/histogram-member
           [adjusted-activation-hist-view cost-object]]
          (when (not is-demo?)
            [sc/table-member
             [util/sl (util/path ["cost_object" (:uuid cost-object) "delete"])
              [sc/trash-icon]]])])
       (when (not is-demo?)
         [sc/table-member
          [util/sl (util/path ["layer" (->> layer-state :layer :uuid) "cost_object_create"])
           [sc/button
            [sc/plus-icon]]]])]]]))

;; ---
;; Top-level render
;; ---

(defn layer-page [m]
  (let [is-demo?        @grid-view/demo-state
        layer-endpoint  (fn [uuid] (if is-demo?
                                     (api/generator-layer uuid)
                                     (api/layer-view uuid)))
        curr-match-uuid (->> m :path-params :uuid)]
    (sel/set-selection-if-changed! layer-state layer-endpoint curr-match-uuid [:selection :uuid])
    (fn [m]
        [layer-page-render (->> @layer-state :selection) is-demo?])))
