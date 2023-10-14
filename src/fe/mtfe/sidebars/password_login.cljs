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

(defn action-config [m]
  (let [mt-user-uuid (->> m :path-params :uuid)]
    {:resource      :curr-password-login
     :endpoint      (api/password-login)
     :state-path    [:password-login :action]
     :init-state-fn (fn []
                      {:uuid         (str (random-uuid))
                       :mt-user-uuid mt-user-uuid
                       :password     ""})
     :validations   []
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
        user-name    @(subscribe [:sidebar-state])
        printo       (println user-name)]
    [:<>
     [:h1 "New Mertonon password login"]
     [:p "There will eventually be many login methods, which is why you have to create them separately"]
     [:p "For user: " [:strong (str mt-user-uuid)]]
]))
    ;; [fi/state-password-input (curr-config :state-path) [:action-params :password] "Password"]
    ;; [fi/state-password-input (curr-config :state-path) [:password-dup] "Password again"]
    ;; [act/action-button curr-config]))

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
