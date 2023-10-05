(ns mtfe.sidebars.mt-user
  "Mertonon user sidebar"
  (:require [ajax.core :refer [GET POST]]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.delete-button :as del]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]))

;; ---
;; Creation
;; ---

(defn mt-user-create-sidebar [m]
  nil)

;; ---
;; Reading
;; ---

(defn mt-user-sidebar
  [m]
  [:<>
   [util/sl (util/path ["logout"]) [sc/button "Logout"]]])

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid (->> m :path-params :uuid)]
    {:resource   :curr-mt-user
     :endpoint   (api/mt-user-member uuid)
     :state-path [:mt-user :delete]
     :model-name "Mertonon User"
     :nav-to     "#/admin"}))

(defn mt-user-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn mt-user-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
