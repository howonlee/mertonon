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
  {:uuid     (str (random-uuid))
   :email    ""
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
                                :action-fn     (sc-handlers/creation-handler api/entryApi
                                                                             create-sc-state
                                                                             mc/->Entry
                                                                             [:uuid :cobj-uuid :name :label :type :value :date])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(mt-statechart/init-sc! :mt-user-create create-sc-state create-sc)

;; ---
;; Creation
;; ---

(defn intro-render [m]
  (let [cobj-uuid (->> m :path-params :uuid)]
    [:<>]))
     ;; [:h1 [sc/entry-icon] " Add Journal Entry"]
     ;; [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
     ;; [:div.mb2 [sc/cobj-icon] " Cost Node UUID - " (->> cobj-uuid str)]
     ;; [:p "Values currently have to be an arbitrary integer only right now."]
     ;; [:p "Currency and lots of other stuff is coming."]
     ;; [sc-components/validation-popover sidebar-state :name-blank "Journal Entry Name is blank"
     ;;  [sc-components/state-text-input create-sc-state "Journal Entry Name" [:curr-create-params :name]]]
     ;; [sc-components/state-text-input create-sc-state "Label" [:curr-create-params :label]]
     ;; [sc-components/validation-popover sidebar-state :value-not-int "Value is not an integer"
     ;;  [sc-components/validation-popover sidebar-state :value-blank "Value is blank"
     ;;   [sc-components/state-text-input create-sc-state "Value" [:curr-create-params :value]]]]
     ;; [sc/border-region
     ;;  [sc/form-label "Entry Date"]
     ;;  [sc-components/state-datepicker create-sc-state sidebar-state [:curr-create-params :date]]]
     ;; [sc-components/create-button @create-sc-state create-sc-state sidebar-state]]))

;; ---
;; Top-level render
;; ---

(defn intro-page [m]
  (sel/swap-if-changed! (->> m :path-params :uuid str)
                         sidebar-state
                         [:curr-create-params :cobj-uuid])
  (mt-statechart/send-reset-event-if-finished! create-sc-state)
  [intro-render m])
