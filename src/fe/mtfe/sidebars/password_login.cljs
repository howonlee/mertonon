(ns mtfe.sidebars.password-login
  "Mertonon password login sidebar"
  (:require [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.action-button :as act]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe]]))

;; ---
;; Creation (Done as action because idiosyncrasy of ctr)
;; ---

;; TODO: there can't be another password login for mt user
;; also enforce on backend side...

(def action-config
  {:resource    :curr-password-login
   :endpoint    (api/password-login)
   :state-path  [:password-login :create]
   :init-params (fn []
                  {:uuid         (str (random-uuid))
                   :mt-user-uuid ""
                   :password     ""})
   :validation  []
   :nav-to      "#/admin"})
;; 
;; (defn mt-user-create-before-fx [m]
;;   (cr/before-fx create-config m))
;; 
;; (defn mt-user-create-sidebar [m]
;;   [:<>
;;    [:h1 "New Mertonon user"]
;;    [:p "Assign a password separately."]
;;    [fi/state-text-input (create-config :state-path) [:create-params :username] "User username"]
;;    [fi/state-text-input (create-config :state-path) [:create-params :email] "User email"]
;;    [cr/create-button create-config]])

;; ---
;; No reading - no password reading lol
;; ---

;; ---
;; Deletion
;; ---

(defn delete-config [m]
  (let [uuid (->> m :path-params :uuid)]
    {:resource   :curr-password-login
     :endpoint   (api/password-login-member uuid)
     :state-path [:password-login :delete]
     :model-name "Mertonon Password Login (for Mertonon user)"
     :nav-to     "#/admin"}))

(defn password-login-delete-before-fx [m]
  (del/before-fx (delete-config m) m))

(defn password-login-delete-sidebar [m]
  [del/delete-model-sidebar (delete-config m) m])
