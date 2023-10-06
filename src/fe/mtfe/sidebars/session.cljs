(ns mtfe.sidebars.session
  "Login and logout sidebar (session management)"
  (:require [ajax.core :refer [GET POST]]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.components.create-button :as cr]
            [mtfe.components.delete-button :as del]
            [mtfe.components.form-inputs :as fi]
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
  {:username ""
   :password ""})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)}))

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))
(defonce delete-sc-state
   (r/atom nil))

(def validation-list
  [(sc-validation/non-blank [:curr-create-params :username] :username-blank)
   (sc-validation/non-blank [:curr-create-params :password] :password-blank)])

(def create-sc
  (mt-statechart/simple-create :mt-session-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler sidebar-state validation-list)
                                :action-fn     (sc-handlers/creation-handler api/session
                                                                             create-sc-state
                                                                             (fn [username password]
                                                                               {:username username
                                                                                :password password})
                                                                             [:username :password])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(def delete-sc
  (mt-statechart/simple-delete :mt-session-delete
                               {:action-fn   (sc-handlers/deletion-handler api/session delete-sc-state)
                                :finalize-fn (sc-handlers/refresh-handler delete-sc-state)}))

(mt-statechart/init-sc! :mt-session-create create-sc-state create-sc)
(mt-statechart/init-sc! :mt-session-delete delete-sc-state delete-sc)

;; ---
;; Creation
;; ---

(defn login-render [m]
  [:<>
   [:h1 "Mertonon Login"]
   [sc/border-region
   [sc-components/validation-popover sidebar-state :username-blank "Username is blank"
    [sc-components/state-text-input create-sc-state "Username" [:curr-create-params :username]]]
   [sc-components/validation-popover sidebar-state :password-blank "Password is blank"
    [sc-components/state-password-input create-sc-state "Password" [:curr-create-params :password]]]]
   [sc-components/create-button @create-sc-state create-sc-state sidebar-state]])

;; ---
;; Top-level render
;; ---

(defn login-sidebar [m]
  (mt-statechart/send-reset-event-if-finished! create-sc-state)
  [login-render m])

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

(defn logout-sidebar [m]
  [:<>
   [:h1 "Log out from Mertonon"]
   [:p "Log out from Mertonon?"]
   [del/delete-button (delete-config m)]])
