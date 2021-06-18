(ns mtfe.views.weight
  "Individual Weight view."
  (:require [ajax.core :refer [GET POST]]
            [applied-science.js-interop :as j]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.views.grid :as grid-view]
            [mtfe.views.layer :as layer-view]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defonce weight-state (r/atom {:selection {}}))

(defn weight-page-render [{:keys [weight src-cobj tgt-cobj weightset] :as weight-state}]
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
   [:p (str (:grad weight))]])

(defn weight-page [m]
  (let [is-demo?        @grid-view/demo-state
        weight-endpoint (fn [uuid] (if is-demo?
                                     (api/generatorWeightApi uuid)
                                     (api/weightViewApi uuid)))
        curr-match-uuid (->> m :path-params :uuid)]
    (sel/set-selection-if-changed! weight-state weight-endpoint curr-match-uuid [:selection :uuid])
    (fn [m]
      [weight-page-render (->> @weight-state :selection)])))
