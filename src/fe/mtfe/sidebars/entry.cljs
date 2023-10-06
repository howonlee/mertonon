(ns mtfe.sidebars.entry
  "Sidebar for entry"
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
            [mtfe.views.cost-object :as cobj-view]
            [mtfe.views.grid :as grid-view]
            [reagent.core :as r]))

;; ---
;; State
;; ---

(defn init-create-params []
  {:uuid       (str (random-uuid))
   :cobj-uuid  ""
   :name       ""
   :label      ""
   :type       "abstract.arbitrary.value"
   :value      0
   :date       (js/Date.)})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)}))

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))
(defonce delete-sc-state
   (r/atom nil))

(def validation-list
  [(sc-validation/non-blank [:curr-create-params :name] :name-blank)
   (sc-validation/non-blank [:curr-create-params :value] :value-blank)
   (sc-validation/is-integer [:curr-create-params :value] :value-not-int)])

(def create-sc
  (mt-statechart/simple-create :entry-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler sidebar-state validation-list)
                                :action-fn     (sc-handlers/creation-handler api/entry
                                                                             create-sc-state
                                                                             mc/->Entry
                                                                             [:uuid :cobj-uuid :name :label :type :value :date])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(def delete-sc
  (mt-statechart/simple-delete :entry-delete
                               {:action-fn   (sc-handlers/deletion-handler api/entry-member delete-sc-state)
                                :finalize-fn (sc-handlers/refresh-handler delete-sc-state)}))

(mt-statechart/init-sc! :entry-create create-sc-state create-sc)
(mt-statechart/init-sc! :entry-delete delete-sc-state delete-sc)

;; ---
;; Creation
;; ---

(defn entry-create-sidebar-render [m]
  (let [cobj-uuid (->> m :path-params :uuid)]
    [:<>
     [:h1 [sc/entry-icon] " Add Journal Entry"]
     [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
     [:div.mb2 [sc/cobj-icon] " Cost Node UUID - " (->> cobj-uuid str)]
     [:p "Values currently have to be an arbitrary integer only right now."]
     [:p "Currency and lots of other stuff is coming."]
     [sc-components/validation-popover sidebar-state :name-blank "Journal Entry Name is blank"
      [sc-components/state-text-input create-sc-state "Journal Entry Name" [:curr-create-params :name]]]
     [sc-components/state-text-input create-sc-state "Label" [:curr-create-params :label]]
     [sc-components/validation-popover sidebar-state :value-not-int "Value is not an integer"
      [sc-components/validation-popover sidebar-state :value-blank "Value is blank"
       [sc-components/state-text-input create-sc-state "Value" [:curr-create-params :value]]]]
     [sc/border-region
      [sc/form-label "Entry Date"]
      [sc-components/state-datepicker create-sc-state sidebar-state [:curr-create-params :date]]]
     [sc-components/create-button @create-sc-state create-sc-state sidebar-state]]))

;; ---
;; Top-level render
;; ---

(defn entry-create-sidebar [m]
  (sel/swap-if-changed! (->> m :path-params :uuid str)
                         sidebar-state
                         [:curr-create-params :cobj-uuid])
  (mt-statechart/send-reset-event-if-finished! create-sc-state)
  [entry-create-sidebar-render m])

(defn entry-delete-sidebar [m]
  [sc-components/delete-model-sidebar sidebar-state api/entry-member delete-sc-state "Journal Entry" m])
