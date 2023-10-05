(ns mtfe.sidebars.mt-user
  "Mertonon user sidebar"
  (:require [ajax.core :refer [GET POST]]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]))

;; ---
;; Creation
;; ---

(def create-config
  {:resource    :curr-mt-user
   :endpoint    (api/mt-user)
   :state-path  [:mt-user :create]
   :init-params (fn []
                  {:uuid     (str (random-uuid))
                   :username ""
                   :email    ""})
   :ctr         mc/->MtUser
   :ctr-params  [:uuid :username :email]
   :nav-to      "#/admin"})

(defn mt-user-create-before-fx [m]
  (cr/before-fx create-config m))

(defn mt-user-create-sidebar [m]
  [:<>
   [:h1 "New Mertonon user"]
   [:p "Assign a password separately."]
   [fi/state-text-input (create-config :state-path) [:create-params :username] "User username"]
   [fi/state-text-input (create-config :state-path) [:create-params :email] "User email"]
   [cr/create-button create-config]])

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
