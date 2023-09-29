(ns mtfe.sidebars.grid-select
  "Sidebar for grid selection screen"
  (:require [applied-science.js-interop :as j]
            [com.fulcrologic.statecharts :as fsc]
            [com.fulcrologic.statecharts.protocols :as sp]
            [com.fulcrologic.statecharts.simple :as simple]
            [mertonon.models.constructors :as mc]
            [mtfe.api :as api]
            [mtfe.statecharts.components :as sc-components]
            [mtfe.statecharts.core :as mt-statechart]
            [mtfe.statecharts.handlers :as sc-handlers]
            [mtfe.statecharts.sideeffects :as sc-se]
            [mtfe.statecharts.validations :as sc-validation]
            [mtfe.stylecomps :as sc]
            [mtfe.util :as util]
            [reagent.core :as r]))

;; ---
;; State
;; ---

(defn init-create-params []
  {:uuid           (str (random-uuid))
   :name           ""
   :label          ""
   :optimizer-type :sgd
   ;; TODO: get some recursive semantics
   :hyperparams    (.stringify js/JSON (clj->js {:lr 0.025}))})

(defonce sidebar-state
  (r/atom {:curr-create-params (init-create-params)
           :selection {}
           :validation-errors #{}}))

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))
(defonce delete-sc-state
   (r/atom nil))

(def create-sc
  (mt-statechart/simple-create :grid-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler
                                                 sidebar-state
                                                 [(sc-validation/non-blank [:curr-create-params :name] :name-blank)])
                                :action-fn     (sc-handlers/creation-handler api/grid create-sc-state mc/->Grid
                                                                             [:uuid :name :label :optimizer-type :hyperparams])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(def delete-sc
  (mt-statechart/simple-delete :grid-delete
                               {:action-fn   (sc-handlers/deletion-handler api/grid-member delete-sc-state)
                                :finalize-fn (sc-handlers/refresh-handler delete-sc-state)}))

(mt-statechart/init-sc! :grid-create create-sc-state create-sc)
(mt-statechart/init-sc! :grid-delete delete-sc-state delete-sc)

;; ---
;; Creation
;; ---

(defn grid-create-sidebar-render [create-sc-state]
  [:<>
   [:h1 "New Grid"]
   [:p "More optimization types and ability to change hyperparameters are coming."]
   [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
   [sc-components/validation-popover sidebar-state :name-blank "Grid Name is blank"
    [sc-components/state-text-input create-sc-state "Grid Name" [:curr-create-params :name]]]
   
   [sc-components/state-text-input create-sc-state "Grid Label" [:curr-create-params :label]]
   ;; TODO: let these change, lol
   [:div.mb2 "Optimization Type - SGD"]
   [:div.mb2 "Hyperparameters"
    [:div "Adjustment Rate - 0.025"]]
   [sc-components/create-button @create-sc-state create-sc-state sidebar-state]])


;; ---
;; Entry
;; ---

(defn grid-create-sidebar [m]
  (mt-statechart/send-reset-event-if-finished! create-sc-state)
  [grid-create-sidebar-render create-sc-state])

(defn grid-delete-sidebar [m]
  [sc-components/delete-model-sidebar sidebar-state api/grid-member delete-sc-state "Grid" m])
