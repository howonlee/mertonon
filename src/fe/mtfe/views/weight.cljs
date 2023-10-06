(ns mtfe.views.weight
  "Individual Weight view."
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.views.grid :as grid-view]
            [mtfe.views.layer :as layer-view]
            [mtfe.util :as util]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]))
;; ---
;; Before-fx
;; ---

(defn before-fx [m]
  (let [is-demo?        @(subscribe [:is-demo?])
        uuid            (->> m :path-params :uuid)
        weight-endpoint (if is-demo?
                          (api/generator-weight uuid)
                          (api/weight-view uuid))]
    [[:dispatch [:selection :weight-view weight-endpoint {}]]]))

(defn weight-page [_]
  (let [{weight :weight
         src-cobj :src-cobj
         tgt-cobj :tgt-cobj
         weightset :weightset} @(subscribe [:selection :weight-view])]
  [:<>
   [:h1 [sc/weight-icon] " Weight " [:strong (str (:uuid weight))]]
   [:h2 (:label weight)]
   [:h2 [sc/ws-icon] " Weightset"]
   [:p [util/path-fsl ["weightset" (:uuid weightset)] (str (:name weightset))]]
   [:h5 [sc/cobj-icon] " Source cost node"]
   [:p [util/path-fsl ["cost_object" (:uuid src-cobj)] (str (:name src-cobj))]]
   [:h5 [sc/cobj-icon] " Target cost node"]
   [:p [util/path-fsl ["cost_object" (:uuid tgt-cobj)] (str (:name tgt-cobj))]]
   [:h5 "Value"]
   [:p (str (:value weight))]
   [:h5 "Gradient"]
   [:p (str (:grad weight))]]))
