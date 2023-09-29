(ns mtfe.sidebars.mt-user
  "Mertonon user sidebar"
  (:require [ajax.core :refer [GET POST]]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.selectors :as sel]
            [mtfe.stylecomps :as sc]
            [mtfe.statecharts.components :as sc-components]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.statecharts.handlers :as sc-handlers]
            [mtfe.statecharts.sideeffects :as sc-se]
            [mtfe.statecharts.validations :as sc-validation]
            [mtfe.util :as util]
            [mtfe.views.layer :as layer-view]
            [mtfe.views.grid :as grid-view]
            [reagent.core :as r]))

(defn mt-user-sidebar
  [m]
  [:<>
   [util/sl (util/path ["logout"]) [sc/button "Logout"]]])
