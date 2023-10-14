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
;; Creation (Done as action because idiosyncrasy of constructor)
;; ---

;; TODO: there can't be another password login for mt user
;; also enforce on backend side...

(def password-login-labels
  {
   ;; State labels
   :initial  "Enter new password for user"
   :filled   "Press Create Password button to create the password."
   :acting   "Creating password..."
   :success  "Successfully created password for user."
   :failure  "Failed to create password for user. See error."
   :finished "Finished!"

   ;; Button labels
   :submit   "Create Password"
   :finish   "Finish"})

(defn action-config [m]
  (let [mt-user-uuid (->> m :path-params :uuid)]
    {:resource      :curr-password-login
     :endpoint      (api/password-login)
     :state-path    [:password-login :action]
     :init-state-fn (fn []
                      {:uuid         (str (random-uuid))
                       :mt-user-uuid mt-user-uuid
                       :password     ""})
     :validations   [(validations/two-members-equal
                       [:action-params :password]
                       [:password-repeat]
                       :password-not-match)]
     :nav-to        "#/admin"}))

(defn password-login-create-before-fx [m]
  (let [mt-user-uuid (->> m :path-params :uuid)]
    [[:dispatch-n [[:reset-action-state (action-config m)]
                   [:select-with-custom-success [:password-login :action :mt-user]
                    (api/mt-user-member mt-user-uuid) {} :sidebar-selection-success]]]]))

(defn password-login-create-sidebar [m]
  (let [mt-user-uuid (->> m :path-params :uuid)
        curr-config  (action-config m)
        state-path   (curr-config :state-path)
        username     @(subscribe [:sidebar-state :password-login :action :mt-user :username])]
    [:<>
     [:h1 "New Mertonon password login"]
     [:p "There will eventually be many login methods, which is why you have to create them separately"]
     [:p "For user: " [:strong (str username)]]
     [fi/state-password-input (curr-config :state-path) [:action-params :password] "Password" :mutate-action-state]
     [vblurbs/validation-popover state-path :password-not-match "Passwords do not match"
      [fi/state-password-input (curr-config :state-path) [:password-repeat] "Password again" :mutate-action-state]]
     [act/action-button curr-config password-login-labels]]))

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
