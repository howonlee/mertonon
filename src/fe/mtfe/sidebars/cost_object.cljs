(ns mtfe.sidebars.cost-object
  "Sidebar for cost-object"
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
            [mtfe.views.cost-object :as cobj-view]
            [mtfe.views.grid :as grid-view]
            [reagent.core :as r]))

;; ---
;; State
;; ---

(defn init-create-params []
  {:uuid       (str (random-uuid))
   :layer-uuid ""
   :name       ""
   :label      ""})

(defonce sidebar-state (r/atom {:curr-create-params (init-create-params)}))

;; ---
;; Statecharts
;; ---

(defonce create-sc-state
   (r/atom nil))
(defonce delete-sc-state
   (r/atom nil))

(def create-sc
  (mt-statechart/simple-create :cobj-create
                               {:reset-fn      (sc-handlers/reset-handler sidebar-state [:curr-create-params] init-create-params)
                                :mutation-fn   (sc-handlers/mutation-handler sidebar-state)
                                :validation-fn (sc-handlers/validation-handler
                                                 sidebar-state
                                                 [(sc-validation/non-blank [:curr-create-params :name] :name-blank)])
                                :action-fn     (sc-handlers/creation-handler api/cost-object create-sc-state mc/->CostObject [:uuid :layer-uuid :name :label])
                                :finalize-fn   (sc-handlers/refresh-handler create-sc-state)}))

(def delete-sc
  (mt-statechart/simple-delete :cobj-delete
                               {:action-fn   (sc-handlers/deletion-handler api/cost-object-member delete-sc-state)
                                :finalize-fn (sc-handlers/refresh-handler delete-sc-state)}))

(mt-statechart/init-sc! :cobj-create create-sc-state create-sc)
(mt-statechart/init-sc! :cobj-delete delete-sc-state delete-sc)

;; ---
;; Creation
;; ---

(defn cobj-create-sidebar-render [m]
  (let [layer-uuid (->> m :path-params :uuid)]
    [:<>
     [:h1 [sc/cobj-icon] " Add Cost Node"]
     [:div.mb2 "UUID - " (->> @sidebar-state :curr-create-params :uuid str)]
     [:div.mb2 [sc/layer-icon] " Layer UUID - " (->> layer-uuid str)]
     [sc-components/validation-popover sidebar-state :name-blank "Cost Node Name is blank"
      [sc-components/state-text-input create-sc-state "Cost Node Name" [:curr-create-params :name]]]
     [sc-components/state-text-input create-sc-state "Label" [:curr-create-params :label]]
     [sc-components/create-button @create-sc-state create-sc-state sidebar-state]]))

;; ---
;; Partials
;; ---

(defn header-partial []
  [:<>
   [:h1 [sc/cobj-icon] " Cost Node"]
   [:p "Cost nodes in Mertonon are the entities that Mertonon suggests allocations on. They could be people, cost objects for paperclips, etc etc."]
   [:strong "Mertonon contribution percentages and suggested contribution percentages are _only_ updated whenever gradient calculations are kicked off, not on initial creation."]
   [:p]])

;; ---
;; Top-level render
;; ---

(defn cost-object-create-sidebar [m]
  (sel/swap-if-changed! (->> m :path-params :uuid str)
                         sidebar-state
                         [:curr-create-params :layer-uuid])
  (mt-statechart/send-reset-event-if-finished! create-sc-state)
  [cobj-create-sidebar-render m])

(defn cost-object-delete-sidebar [m]
  [sc-components/delete-model-sidebar sidebar-state api/cost-object-member delete-sc-state "Cost Node" m])

(defn cost-object-sidebar [m]
  (let [is-demo?        @grid-view/demo-state
        cobj-uuid       (->> m :path-params :uuid)
        cobj-endpoint   (if is-demo?
                          api/generator-cost-object
                          api/cost-object-view)]
    (sel/set-state-if-changed! sidebar-state
                               cobj-endpoint
                               cobj-uuid
                               [:cobj-selection :cost-object :uuid]
                               [:cobj-selection])
    (sc-handlers/do-validations! sidebar-state
                                 [(sc-validation/and-predicate
                                    (sc-validation/min-num-elems [:cobj-selection :losses] 1 :no-loss)
                                    (sc-validation/min-num-elems [:cobj-selection :inputs] 1 :no-input)
                                    :not-input-or-loss)])
    [:<>
     [header-partial]
     (if (not is-demo?)
       [:<>
        [sc-components/validation-toast sidebar-state :not-input-or-loss "Journal Entries must be for cost nodes in an input or goal responsibility center"]
        [sc-components/validated-link sidebar-state :not-input-or-loss "Create Journal Entry"
         [util/sl (util/path ["cost_object" cobj-uuid "entry_create"]) [sc/button [sc/entry-icon] " Create Journal Entry"]]]])]))
