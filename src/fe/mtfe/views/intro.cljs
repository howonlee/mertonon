(ns mtfe.views.intro
  "Introduction view - shown before user creation, gets you to create admin user"
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
            [reagent.core :as r]))

;; ---
;; State
;; ---

(defn init-create-params []
  {:email    ""
   :username ""
   :password ""})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)}))

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))

(def validation-list
  [(sc-validation/non-blank [:curr-create-params :email] :email-blank)
   (sc-validation/non-blank [:curr-create-params :username] :username-blank)
   (sc-validation/non-blank [:curr-create-params :password] :password-blank)])

(def create-sc
  (mt-statechart/simple-create :mt-user-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler sidebar-state validation-list)
                                ;;;;;;;;;;
                                ;;;;;;;;;;
                                ;;;;;;;;;;
                                :action-fn     (sc-handlers/creation-handler api/introApi
                                                                             create-sc-state
                                                                             ;;;;;
                                                                             ;;;;;
                                                                             ;;;;;
                                                                             dict some crap
                                                                             [:cobj-uuid :name :label :type :value :date])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(mt-statechart/init-sc! :mt-user-create create-sc-state create-sc)

;; ---
;; Creation
;; ---

(defn intro-render [m]
  [:<>
   [:h1 "Welcome to Mertonon"]
   [:p "Make the administrator account for this Mertonon instance."]
   [sc-components/validation-popover sidebar-state :username-blank "Username is blank"
    [sc-components/state-text-input create-sc-state "Username" [:curr-create-params :username]]]
   [sc-components/validation-popover sidebar-state :email-blank "Email is blank"
    [sc-components/state-text-input create-sc-state "Email" [:curr-create-params :email]]]
   [sc-components/validation-popover sidebar-state :password-blank "Password is blank"
    [sc-components/state-password-input create-sc-state [:curr-create-params :password]]]
   [sc-components/create-button @create-sc-state create-sc-state sidebar-state]])

;; ---
;; Top-level render
;; ---

(defn intro-page [m]
  (mt-statechart/send-reset-event-if-finished! create-sc-state)
  [intro-render m])
