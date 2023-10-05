(ns mtfe.sidebars.mt-user
  "Mertonon user sidebar"
  (:require [ajax.core :refer [GET POST]]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]))

(defn mt-user-create-sidebar [m]
  nil)

(defn mt-user-delete-sidebar [m]
  nil)


(defn mt-user-sidebar
  [m]
  [:<>
   [util/sl (util/path ["logout"]) [sc/button "Logout"]]])
