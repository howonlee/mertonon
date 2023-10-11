(ns mtfe.sidebars.session
  "Login and logout sidebar (session management)"
  (:require [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
            [mtfe.components.validation-blurbs :as vblurbs]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [mtfe.validations :as validations]
            [reagent.core :as r]))

;; ---
;; Creation
;; ---

(def create-config
  {:resource      :curr-session
   :endpoint      (api/session)
   :state-path    [:session :create]
   :init-state-fn (fn []
                    {:username ""
                     :password ""})
   :validations   [(validations/non-blank [:create-params :username] :username-blank)
                   (validations/non-blank [:create-params :password] :password-blank)]
   :ctr           (fn [username password] {:username username :password password})
   :ctr-params    [:username :password]
   :nav-to        "#/"})

(defn login-before-fx [m]
  (cr/before-fx create-config m))

(def login-labels
  {
   ;; State labels
   :blank    "Enter username and password"
   :filled   "Press Log in button to log in."
   :creating "Logging in..."
   :success  "Successfully logged in"
   :failure  "Failed to log in. See error."
   :finished "Finished!"

   ;; Button labels
   :submit   "Log in"
   :finish   "Finish"})

(defn login-sidebar [m]
  (let [state-path (create-config :state-path)]
    [:<>
     [:h1 "Mertonon Login"]
     [sc/border-region
      [vblurbs/validation-popover state-path :username-blank "Username is blank"
       [fi/state-text-input state-path [:create-params :username] "Username"]]
      [vblurbs/validation-popover state-path :password-blank "Password is blank"
       [fi/state-password-input state-path [:create-params :password] "Password"]]]
     [cr/create-button create-config login-labels]]))

;; ---
;; Logout
;; ---

(defn delete-config [m]
  {:resource   :curr-weight
   :endpoint   (api/session)
   :state-path [:session :delete]
   :model-name "Session"
   :nav-to     :reload})

(defn logout-before-fx [m]
  (let [cfg (delete-config m)]
    [[:dispatch [:reset-delete-state (:state-path cfg)]]]))

(def logout-labels
  {;; State labels
   :initial  "Press Log out button to log out"
   :deleting "Logging out..."
   :success  "Successfully logged out."
   :failure  "Failed to log out"
   :finished "Finished."

   ;; Button labels
   :delete   "Log out"
   :finish   "Finish"})

(defn logout-sidebar [m]
  [:<>
   [:h1 "Log out from Mertonon"]
   [:p "Log out from Mertonon?"]
   [del/delete-button (delete-config m) logout-labels]])
