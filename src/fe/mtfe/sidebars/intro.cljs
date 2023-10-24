(ns mtfe.sidebars.intro
  "Introduction sidebar - shown before user creation, gets you to create admin user"
  (:require [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
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
  {:resource      :curr-intro
   :endpoint      (api/intro)
   :state-path    [:intro-user :create]
   :init-state-fn (fn []
                    {:username ""
                     :email    ""
                     :password ""})
   :validations   [(validations/non-blank [:create-params :email] :email-blank)
                   (validations/non-blank [:create-params :username] :username-blank)
                   (validations/non-blank [:create-params :password] :password-blank)
                   (validations/two-members-equal
                     [:create-params :password]
                     [:create-params :password-repeat]
                     :password-not-match)]
   :ctr           (fn [username email password]
                    {:username username
                     :email    email
                     :password password})
   :ctr-params    [:username :email :password]
   :nav-to        "#/"})

(defn intro-before-fx [m]
  (cr/before-fx create-config m))

(def intro-labels
  {;; State labels
   :blank    "Enter username, email password for admin account"
   :filled   "Press Create button to create admin account."
   :creating "Creating admin account..."
   :success  "Successfully created admin account"
   :failure  "Failed to create admin account. See error."
   :finished "Finished!"

   ;; Button labels
   :submit   "Create Admin"
   :finish   "Finish"})

(defn intro-sidebar [m]
  (let [state-path (create-config :state-path)]
    [:<>
     [:h1 "Welcome to Mertonon"]
     [:p "Make the administrator account for this Mertonon instance."]
     [sc/border-region
      [vblurbs/validation-popover state-path :username-blank "Username is blank"
       [fi/state-text-input state-path [:create-params :username] "Username"]]
      [vblurbs/validation-popover state-path :email-blank "Email is blank"
       [fi/state-text-input state-path [:create-params :email] "Email"]]
      [vblurbs/validation-popover state-path :password-blank "Password is blank"
       [fi/state-password-input state-path [:create-params :password] "Password"]]
      [vblurbs/validation-popover state-path :password-not-match "Passwords do not match"
       [fi/state-password-input state-path [:create-params :password-repeat] "Password Again"]]]
     [cr/create-button create-config intro-labels]]))
